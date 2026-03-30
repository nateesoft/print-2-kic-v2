package com.ics.process;

import com.ics.constant.Value;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ Consumer สำหรับรับ trigger การพิมพ์ KIC
 *
 * <p>รับ message จาก queue แล้วเรียก {@link PrintTriggerCallback} เพื่อสั่งพิมพ์ทันที
 * แทนที่จะรอ polling รอบถัดไป (ทุก 30 วินาที)</p>
 *
 * <h3>รูปแบบ Message ที่รองรับ:</h3>
 * <ul>
 *   <li>{@code table,macno} — พิมพ์เฉพาะโต๊ะที่ระบุ เช่น {@code 5,001}</li>
 *   <li>ข้อความอื่น / ว่างเปล่า — trigger scan ทั้งหมด (เหมือน polling รอบปกติ)</li>
 * </ul>
 *
 * <h3>การตั้งค่า (connect.ini):</h3>
 * <pre>
 * rabbitmq_host,localhost
 * rabbitmq_port,5672
 * rabbitmq_user,guest
 * rabbitmq_pass,guest
 * rabbitmq_vhost,/
 * rabbitmq_queue,kic_print_queue
 * use_rabbitmq,true
 * </pre>
 *
 * @author ICS
 */
public class RabbitMQConsumer {

    /**
     * Callback ที่ถูกเรียกเมื่อมี message เข้ามาจาก RabbitMQ
     */
    public interface PrintTriggerCallback {
        /**
         * @param tableNo    หมายเลขโต๊ะ (อาจเป็น null ถ้า message ไม่ได้ระบุ)
         * @param macno      หมายเลขเครื่อง (อาจเป็น null ถ้า message ไม่ได้ระบุ)
         * @param rawMessage raw JSON string ที่รับมาจาก RabbitMQ (สำหรับ mapping เพิ่มเติม)
         */
        void onPrintTriggered(String tableNo, String macno, String rawMessage);
    }

    private volatile Connection connection;
    private volatile Channel channel;
    private volatile boolean running = false;

    private final PrintTriggerCallback callback;
    private final ScheduledExecutorService reconnectScheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "RabbitMQ-Reconnect");
                t.setDaemon(true);
                return t;
            });

    private static final int RECONNECT_DELAY_SECONDS = 10;

    public RabbitMQConsumer(PrintTriggerCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        this.callback = callback;
    }

    /**
     * เริ่ม consumer — เชื่อมต่อ RabbitMQ และเริ่ม listen queue
     * สามารถเรียกได้จาก thread ใดก็ได้
     */
    public void start() {
        running = true;
        System.out.println("RabbitMQ: starting consumer...");
        connect();
    }

    /**
     * หยุด consumer และปิด connection ทั้งหมด
     */
    public void stop() {
        running = false;
        reconnectScheduler.shutdownNow();
        closeQuietly();
        System.out.println("RabbitMQ: consumer stopped.");
    }

    private void connect() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(Value.rabbitmqHost);
            factory.setPort(Value.rabbitmqPort);
            factory.setUsername(Value.rabbitmqUser);
            factory.setPassword(Value.rabbitmqPass);
            factory.setVirtualHost(Value.rabbitmqVhost);
            // ปิด auto-recovery ของ library เพราะเราจัดการ reconnect เอง
            factory.setAutomaticRecoveryEnabled(false);
            // timeout การเชื่อมต่อ 10 วินาที
            factory.setConnectionTimeout(10_000);

            connection = factory.newConnection("PrintToKic-Consumer");
            channel = connection.createChannel();

            // ประกาศ Topic Exchange ให้ตรงกับ food-ordering-service
            channel.exchangeDeclare(Value.rabbitmqExchange, "topic", true);

            // ประกาศ queue แบบ durable = message ไม่หายเมื่อ RabbitMQ restart
            channel.queueDeclare(Value.rabbitmqQueue, true, false, false, null);

            // Bind queue กับ Exchange ด้วย routing key เพื่อรับ order.created
            channel.queueBind(Value.rabbitmqQueue, Value.rabbitmqExchange, Value.rabbitmqRoutingKey);

            // รับทีละ 1 message — ป้องกันการ queue ล้น กรณีพิมพ์นาน
            channel.basicQos(1);

            // ตั้ง listener สำหรับ reconnect เมื่อ connection ขาด
            connection.addShutdownListener(cause -> {
                if (running && !cause.isInitiatedByApplication()) {
                    System.err.println("RabbitMQ: connection lost — " + cause.getMessage());
                    scheduleReconnect();
                }
            });

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8).trim();
                System.out.println("RabbitMQ: received message = [" + message + "]");
                try {
                    dispatchPrint(message);
                } catch (Exception e) {
                    System.err.println("RabbitMQ: error in print callback — " + e.getMessage());
                } finally {
                    // ยืนยัน acknowledge หลังประมวลผลเสร็จเสมอ
                    try {
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    } catch (IOException ackEx) {
                        System.err.println("RabbitMQ: ack failed — " + ackEx.getMessage());
                    }
                }
            };

            channel.basicConsume(
                    Value.rabbitmqQueue,
                    false,           // autoAck=false เพื่อให้เราควบคุม ack เอง
                    deliverCallback,
                    consumerTag -> {
                        System.err.println("RabbitMQ: consumer cancelled — " + consumerTag);
                        if (running) {
                            scheduleReconnect();
                        }
                    }
            );

            System.out.println("RabbitMQ: connected to "
                    + Value.rabbitmqHost + ":" + Value.rabbitmqPort
                    + " queue=[" + Value.rabbitmqQueue + "]");

        } catch (IOException | TimeoutException e) {
            System.err.println("RabbitMQ: connect failed — " + e.getMessage());
            if (running) {
                scheduleReconnect();
            }
        }
    }

    /**
     * แปลง message เป็น tableNo/macno แล้วเรียก callback
     *
     * <p>รองรับ 2 รูปแบบ:</p>
     * <ul>
     *   <li>JSON จาก food-ordering-service:
     *       {@code {"payload":{"tableNumber":"5"},"source":"food-ordering-service",...}}</li>
     *   <li>Plain text: {@code "table,macno"} เช่น {@code "5,001"}</li>
     *   <li>ข้อความอื่น / ว่างเปล่า — trigger scan ทั่วไป</li>
     * </ul>
     */
    private void dispatchPrint(String message) {
        if (message == null || message.isEmpty()) {
            callback.onPrintTriggered(null, null, message);
            return;
        }

        // รูปแบบ JSON จาก food-ordering-service
        if (message.startsWith("{")) {
            String tableNo = extractJsonString(message, "tableNumber");
            // food-ordering-service ไม่ส่ง macno — ส่งแค่ tableNo แล้วให้ scan ตาม DB
            callback.onPrintTriggered(tableNo, null, message);
            return;
        }

        // รูปแบบ plain text: "table,macno"
        if (message.contains(",")) {
            String[] parts = message.split(",", 2);
            if (parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
                callback.onPrintTriggered(parts[0].trim(), parts[1].trim(), message);
                return;
            }
        }

        // fallback: scan ทั่วไป
        callback.onPrintTriggered(null, null, message);
    }

    /**
     * ดึงค่า String จาก JSON แบบง่าย (ไม่ใช้ library เพิ่มเติม)
     * รองรับ nested key เช่น payload.tableNumber โดยค้นหาจาก key ตัวสุดท้ายใน JSON
     *
     * @return ค่าที่พบ หรือ null ถ้าไม่พบ / ค่าเป็น null ใน JSON
     */
    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.lastIndexOf(search);
        if (idx < 0) {
            return null;
        }
        int colon = json.indexOf(':', idx + search.length());
        if (colon < 0) {
            return null;
        }
        // ข้าม whitespace หลัง colon
        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }
        if (valueStart >= json.length()) {
            return null;
        }
        if (json.charAt(valueStart) == '"') {
            // String value
            int end = json.indexOf('"', valueStart + 1);
            if (end < 0) {
                return null;
            }
            return json.substring(valueStart + 1, end);
        }
        if (json.startsWith("null", valueStart)) {
            return null;
        }
        // number หรือ boolean
        int end = valueStart;
        while (end < json.length() && ",}]".indexOf(json.charAt(end)) < 0) {
            end++;
        }
        return json.substring(valueStart, end).trim();
    }

    private void scheduleReconnect() {
        System.out.println("RabbitMQ: scheduling reconnect in " + RECONNECT_DELAY_SECONDS + "s...");
        try {
            reconnectScheduler.schedule(() -> {
                closeQuietly();
                connect();
            }, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // scheduler ถูก shutdown แล้ว (กรณีเรียก stop())
        }
    }

    private void closeQuietly() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (IOException | TimeoutException ignored) {
        } finally {
            channel = null;
        }
        try {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (IOException ignored) {
        } finally {
            connection = null;
        }
    }

    /**
     * ตรวจสอบว่า consumer กำลัง listen อยู่หรือไม่
     * @return 
     */
    public boolean isConnected() {
        return connection != null && connection.isOpen()
                && channel != null && channel.isOpen();
    }
}
