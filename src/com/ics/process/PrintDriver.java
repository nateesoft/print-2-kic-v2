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
import com.ics.constant.Value;
import com.ics.util.AppLogUtil;
import com.ics.util.DateConvert;

public class PrintDriver {

    private String textAll = "";
    private String textNormal = "";
    private final String header = "<html><head></head><body><table border=0 cellpadding=0 cellspaceing=0 width=100% height=50px>";
    private final String footer = "</table></body></html>";
    private final String fontName = "Angsana New";
    private float width = 75;
    private float height = 72;
    private final DateConvert dc = new DateConvert();

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
        textNormal += str + "\n";
    }

    public void printHTMLKitChen(String printerName) {
        //Print Cashier
        String text = header + textAll + footer;
        try {
            JEditorPane editor = new JEditorPane();
            editor.setContentType("text/html");
            editor.setText(text);

            HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new MediaPrintableArea(0f, 0f, width, height, MediaPrintableArea.INCH));

            PrintService printService = getPrinterKitchen();
            if (printService != null) {
                editor.print(null, null, false, printService, attr, false);
            } else {
                AppLogUtil.htmlFile(text);
                System.out.println("Process Print kic No.:...>>>  " + printerName);
                try {
                    Thread.sleep(90 * 3);
                } catch (InterruptedException e) {
                }
            }
        } catch (PrinterException ex) {
            System.err.println(ex.getMessage());
        }
        close();
    }

    private PrintService getPrinterKitchen() {
        PrintService[] printService = PrinterJob.lookupPrintServices();
        for (PrintService printService1 : printService) {
            if (printService1.getName().equals(Value.printerDriverKitChenName)) {
                return printService1;
            }
        }

        if (printService.length > 0) {
            return printService[0];
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
