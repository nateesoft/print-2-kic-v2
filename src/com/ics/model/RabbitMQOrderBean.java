package com.ics.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean สำหรับ mapping ข้อมูล order ที่รับมาจาก RabbitMQ ของ food-ordering-service
 *
 * <p>รองรับ message envelope format:</p>
 * <pre>
 * {
 *   "eventId": "...",
 *   "eventType": "order.created",
 *   "source": "food-ordering-service",
 *   "branchId": 1,
 *   "payload": {
 *     "orderId": "ORD-xxx",
 *     "tableNumber": "A1",
 *     "status": "PREPARING",
 *     "totalAmount": 150.0,
 *     "totalItems": 1,
 *     "itemCount": 1,
 *     "createdAt": "2026-03-24T05:47:09.468Z",
 *     "itemDetails": [
 *       { "itemId": "ITEM-001", "name": "...", "quantity": 1, "price": 150.0, "specialInstructions": "..." }
 *     ]
 *   }
 * }
 * </pre>
 */
public class RabbitMQOrderBean {

    private String orderId;
    private int internalId;
    private String tableNumber;
    private String status;
    private double totalAmount;
    private int totalItems;
    private int itemCount;
    private String createdAt;
    private String branchId;
    private List<ItemDetail> itemDetails = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Inner class: ItemDetail
    // -----------------------------------------------------------------------

    public static class ItemDetail {
        private String itemId;
        private String code;
        private String name;
        private int quantity;
        private double price;
        private String specialInstructions;
        private String diningOption;

        public String getItemId() { return itemId; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getSpecialInstructions() { return specialInstructions; }
        public String getDiningOption() { return diningOption; }

        @Override
        public String toString() {
            String note = (specialInstructions != null && !specialInstructions.isEmpty())
                    ? " [" + specialInstructions + "]" : "";
            return String.format("  %s | %s %s | %s x%d @ %.2f%s",
                    itemId, code, diningOption, name, quantity, price, note);
        }
    }

    // -----------------------------------------------------------------------
    // Factory method: parse JSON → RabbitMQOrderBean
    // -----------------------------------------------------------------------

    /**
     * แปลง JSON string (envelope หรือ raw payload) เป็น RabbitMQOrderBean
     *
     * @param json raw JSON message จาก RabbitMQ
     * @return bean ที่ map ข้อมูลแล้ว หรือ null ถ้า json เป็น null/ว่าง
     */
    public static RabbitMQOrderBean fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        json = json.trim();

        RabbitMQOrderBean bean = new RabbitMQOrderBean();

        // ดึง branchId จาก envelope level
        bean.branchId = extractValue(json, "branchId");

        // ดึง payload object ถ้ามี envelope wrapper
        String payload = extractObject(json, "payload");
        if (payload == null) {
            // fallback: ไม่มี envelope ให้ treat ทั้ง message เป็น payload
            payload = json;
        }

        bean.orderId        = extractValue(payload, "orderId");
        bean.internalId     = parseInt(extractValue(payload, "internalId"), 0);
        bean.tableNumber    = extractValue(payload, "tableNumber");
        bean.status         = extractValue(payload, "status");
        bean.totalAmount    = parseDouble(extractValue(payload, "totalAmount"), 0.0);
        bean.totalItems     = parseInt(extractValue(payload, "totalItems"), 0);
        bean.itemCount      = parseInt(extractValue(payload, "itemCount"), 0);
        bean.createdAt      = extractValue(payload, "createdAt");

        String itemsArrayJson = extractArray(payload, "itemDetails");
        if (itemsArrayJson != null) {
            bean.itemDetails = parseItemDetails(itemsArrayJson);
        }

        return bean;
    }

    // -----------------------------------------------------------------------
    // JSON parser utilities (ไม่พึ่ง external library)
    // -----------------------------------------------------------------------

    /**
     * Extract nested JSON object ตาม key เช่น "payload":{...}
     * คืนค่า string ของ {...} รวม braces
     */
    private static String extractObject(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx < 0) return null;

        int braceStart = json.indexOf('{', keyIdx + search.length());
        if (braceStart < 0) return null;

        int depth = 0;
        for (int i = braceStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return json.substring(braceStart, i + 1);
            }
        }
        return null;
    }

    /**
     * Extract JSON array ตาม key เช่น "itemDetails":[...]
     * คืนค่า string ของ [...] รวม brackets
     */
    private static String extractArray(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx < 0) return null;

        int arrayStart = json.indexOf('[', keyIdx + search.length());
        if (arrayStart < 0) return null;

        int depth = 0;
        for (int i = arrayStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) return json.substring(arrayStart, i + 1);
            }
        }
        return null;
    }

    /**
     * Extract ค่า String หรือ number จาก JSON ตาม key (ค้นหา occurrence สุดท้าย)
     * คืน null ถ้าไม่พบหรือค่าเป็น null ใน JSON
     */
    private static String extractValue(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.lastIndexOf(search);
        if (idx < 0) return null;

        int colon = json.indexOf(':', idx + search.length());
        if (colon < 0) return null;

        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        if (start >= json.length()) return null;

        char first = json.charAt(start);
        if (first == '"') {
            int end = json.indexOf('"', start + 1);
            return (end < 0) ? null : json.substring(start + 1, end);
        }
        if (json.startsWith("null", start)) return null;

        // number หรือ boolean
        int end = start;
        while (end < json.length() && ",}]".indexOf(json.charAt(end)) < 0) end++;
        return json.substring(start, end).trim();
    }

    /**
     * Parse JSON array เช่น "[{...},{...}]" เป็น List<ItemDetail>
     */
    private static List<ItemDetail> parseItemDetails(String arrayJson) {
        List<ItemDetail> list = new ArrayList<>();
        if (arrayJson == null || arrayJson.length() < 2) return list;

        String content = arrayJson.substring(1, arrayJson.length() - 1).trim();
        if (content.isEmpty()) return list;

        // แยก objects โดย track brace depth
        int depth = 0;
        int start = -1;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    ItemDetail item = parseOneItemDetail(content.substring(start, i + 1));
                    if (item != null) list.add(item);
                    start = -1;
                }
            }
        }
        return list;
    }

    private static ItemDetail parseOneItemDetail(String json) {
        ItemDetail item = new ItemDetail();
        item.itemId              = extractValue(json, "itemId");
        item.code                = extractValue(json, "code");
        item.name                = extractValue(json, "name");
        item.quantity            = parseInt(extractValue(json, "quantity"), 0);
        item.price               = parseDouble(extractValue(json, "price"), 0.0);
        item.specialInstructions = extractValue(json, "specialInstructions");
        item.diningOption        = extractValue(json, "diningOption");

        return (item.itemId == null && item.code == null && item.name == null) ? null : item;
    }

    private static int parseInt(String val, int def) {
        if (val == null || val.isEmpty()) return def;
        try { return Integer.parseInt(val.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private static double parseDouble(String val, double def) {
        if (val == null || val.isEmpty()) return def;
        try { return Double.parseDouble(val.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    public String getOrderId()              { return orderId; }
    public int getInternalId()              { return internalId; }
    public String getTableNumber()          { return tableNumber; }
    public String getStatus()               { return status; }
    public double getTotalAmount()          { return totalAmount; }
    public int getTotalItems()              { return totalItems; }
    public int getItemCount()               { return itemCount; }
    public String getCreatedAt()            { return createdAt; }
    public String getBranchId()             { return branchId; }
    public List<ItemDetail> getItemDetails(){ return itemDetails; }

    // -----------------------------------------------------------------------
    // toString: ใช้ใน console log เพื่อ verify mapping
    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== RabbitMQ Order Mapping ==========\n");
        sb.append(String.format("  Order ID    : %s (ID=%d)%n", orderId, internalId));
        sb.append(String.format("  Branch ID   : %s%n", branchId));
        sb.append(String.format("  Table       : %s%n", tableNumber));
        sb.append(String.format("  Status      : %s%n", status));
        sb.append(String.format("  Total       : %.2f  Items=%d  Count=%d%n", totalAmount, totalItems, itemCount));
        sb.append(String.format("  Created At  : %s%n", createdAt));
        if (!itemDetails.isEmpty()) {
            sb.append("  Item Details:\n");
            for (ItemDetail item : itemDetails) {
                sb.append(item.toString()).append("\n");
            }
        }
        sb.append("============================================");
        return sb.toString();
    }
}
