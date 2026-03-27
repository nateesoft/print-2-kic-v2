package com.ics.process;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ics.constant.OSValidator;
import com.ics.util.AppLogUtil;

public class PrintDriver {

    private String textAll = "";
    private final String header = "<html><head></head><body><table border=0 cellpadding=0 cellspaceing=0 width=100% height=50px>";
    private final String footer = "</table></body></html>";
    private float width = 75;   // หน่วย mm — กระดาษ thermal มาตรฐาน 80mm (printable area 75mm)
    private float height = 72;  // หน่วย mm

    public PrintDriver() {
        if (OSValidator.isWindows()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            }
        }
    }

    public void addTextIFont(String str) {
        textAll += "<tr><td " + str + "</td></tr>";
    }

    public void printHTMLKitChen(String printerName) {
        String text = header + textAll + footer;

        // Fix #4: JEditorPane ต้องสร้างและใช้บน EDT เท่านั้น
        SwingUtilities.invokeLater(() -> {
            try {
                JEditorPane editor = new JEditorPane();
                editor.setContentType("text/html");
                editor.setText(text);

                HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
                // Fix #2: เปลี่ยนจาก INCH → MM เพื่อให้ขนาดกระดาษถูกต้อง (75mm × 72mm)
                attr.add(new MediaPrintableArea(0f, 0f, width, height, MediaPrintableArea.MM));

                PrintService printService = getPrinterKitchen(printerName);
                if (printService != null) {
                    editor.print(null, null, false, printService, attr, false);
                } else {
                    AppLogUtil.htmlFile(text);
                    System.out.println("ไม่พบเครื่องพิมพ์ครัว: " + printerName + " — บันทึกลงไฟล์แทน");
                }
            } catch (PrinterException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    // Fix #1: รับ printerName เป็น parameter แทนการอ่านจาก static Value
    // Fix #1: ถ้าไม่เจอเครื่องพิมพ์ที่ต้องการ คืน null เสมอ — ไม่ fallback ไปเครื่องอื่น
    private PrintService getPrinterKitchen(String printerName) {
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (PrintService service : services) {
            if (service.getName().equals(printerName)) {
                return service;
            }
        }
        return null;
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            if (OSValidator.isWindows()) {
                try {
                    UIManager.put("OptionPane.messageFont", new javax.swing.plaf.FontUIResource(new java.awt.Font(
                            "Tahoma", java.awt.Font.PLAIN, 14)));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });
    }

}
