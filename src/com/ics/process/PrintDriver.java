package com.ics.process;

import com.ics.constant.OSValidator;
import com.ics.constant.Value;
import com.ics.constant.PublicVar;
import com.ics.util.AppLogUtil;
import com.ics.util.DateConvert;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class PrintDriver {

    private String textAll = "";
    private String textNormal = "";
    private final String header = "<html><head></head><body><table border=0 cellpadding=0 cellspaceing=0 width=100% height=50px>";
    private final String footer = "</table></body></html>";
    private final String fontName = "Angsana New";
    private float width = 75;
    private float height = 72;

    public PrintDriver() {
        if (OSValidator.isWindows()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            }
        }
    }

    public void setResolution(float w, float h) {
        this.width = w;
        this.height = h;
    }

    public String getPrinterName() {
        return Value.printerDriverName;
    }

    public void setPrinterName(String printerName) {
        Value.printerDriverName = printerName;
    }

//    public void addImage(String path) {
//        textAll += "<img src=\"" + path + "\" width=200 height=200></img>";
//        textNormal += "...";
//    }
//    public void addImageQR() {
//        textAll += "\n";
//        textAll += "testPrint QRCode";
//        textAll += "\n";
//        textAll += "<tr><td><img src=\"../QRPayment.png\" width=100 height=100></img></td></tr>";
//        textAll += "<tr><td><img src=\"https://www.w3schools.com/images/picture.jpg\" width=100 height=100></img></td></tr>";
//        textNormal += "...";
//    }
    public void addText(String str, String size) {
        textAll += "<font face=" + fontName + " size=" + size + ">" + str + "</font>";
        textNormal += str;
    }

    public void addTextIFont(String str) {
        textAll += "<tr><td " + str + "</td></tr>";
        textNormal += str + "\n";
    }

    public void addTextLn(String str, String size) {
        textAll += "<font face=" + fontName + " size=" + size + ">" + str + "</font><br>";
        textNormal += str + "\n";
    }

    public void addText(String str) {
        String[] datas = str.split("=", str.length());
        for (int i = 0; i <= str.length(); i++) {

        }
        String[] temps = new String[]{"", "", ""};
        System.arraycopy(datas, 0, temps, 0, datas.length);
        textAll += "<tr><td><font face=" + fontName + " size=0>" + str + "</font></td></tr>";
        textNormal += str;
    }

    public void addTextLn(String str) {
        textAll += "<tr><td><font face=" + fontName + " size=-1>" + str + "</font><br></td></tr>";
        textNormal += str + "\n";
    }

    public void printHTML() {
        //Print Cashier
        String text = header + textAll + footer;

        try {
            JEditorPane editor = new JEditorPane();
            editor.setContentType("text/html");
            editor.setText(text);

            HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new MediaPrintableArea(0f, 0f, width, height, MediaPrintableArea.INCH));

            PrintService printService = getPrinter();
            if (printService != null) {
                editor.print(null, null, false, printService, attr, false);
            }
            printService = null;
        } catch (PrinterException ex) {
            System.err.println(ex.getMessage());
        }
        close();
    }

    public void printHTMLIntoFile() throws FileNotFoundException, UnsupportedEncodingException {
        //Print Cashier
        String header1 = "<html><head><meta charset=\"UTF-8\"></head><body><table align='center' border=0 cellpadding=0 cellspaceing=0 width=180 height=50>";
        String footer1 = "</table></body></html>";

        String barcode = " <div style='padding-top:8px; text-align:center; font-size:15px; font-family: Source Sans Pro, Arial, sans-serif;'>\n"
                + "            <!-- back-linking to www.tec-it.com is required -->\n"
                + "            <a href='https://www.tec-it.com' title='Barcode Software by TEC-IT' target='_blank'>\n"
                + "                TEC-IT Barcode Generator<br/>\n"
                + "                <!-- logos are optional -->\n"
                + "                <img alt='TEC-IT Barcode Software' border='0'\n"
                + "                     src='http://www.tec-it.com/pics/banner/web/TEC-IT_Logo_75x75.gif'>\n"
                + "            </a>\n"
                + "        </div>";

        String text = header1 + textAll + barcode + footer1;
        BufferedWriter output = null;
        DateConvert dc = new DateConvert();
        try {
            String filename = dc.dateGetToShow(dc.GetCurrentDate()).replace("/", "").replace(" ", "");
            String path = "D:/DailySales/" + filename + "MTD.html";
            PublicVar.filePath = path;
            File file = new File(path);
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);

        } catch (IOException e) {

        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public void printHTMLKitChen(String printerName) {
        //Print Cashier
        String text = header + textAll + footer;
//        String printer = "";
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
                } catch (Exception e) {
                }
            }
        } catch (PrinterException ex) {
            System.err.println(ex.getMessage());
        }
        close();
    }

    public void printHTMLKitChenByKictran(String printerName) {
        //Print Cashier
        String text = header + textAll + footer;
//        String printer = "";
        try {
            JEditorPane editor = new JEditorPane();
            editor.setContentType("text/html");
            editor.setText(text);

            HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new MediaPrintableArea(0f, 0f, width, height, MediaPrintableArea.INCH));

            PrintService printService = getPrinterKitchenKictran(printerName);
            if (printService != null) {
                editor.print(null, null, false, printService, attr, false);
            } else {
                AppLogUtil.htmlFile(text);
                System.out.println("Process Print kic No.:...>>>  " + printerName);
                try {
                    Thread.sleep(90 * 3);
                } catch (Exception e) {
                }
            }
        } catch (PrinterException ex) {
            System.err.println(ex.getMessage());
        }
        close();
    }

    public void printHTMLOrder() {
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
            }
        } catch (PrinterException ex) {
            System.err.println(ex.getMessage());
        }
        close();
    }

    public void printNormal() {
        try {
            JTextArea textArea = new JTextArea(textNormal);
            textArea.setFont(new Font("Tahoma", Font.PLAIN, 10));

            HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new MediaPrintableArea(0f, 0f, width, height, MediaPrintableArea.INCH));

            PrintService printService = getPrinter();
            if (printService != null) {
                textArea.print(null, null, false, printService, attr, false);
            }
        } catch (PrinterException ex) {
            System.err.println(ex.getMessage());
        }

        close();
    }

    public void printKichen() {
        try {
            JTextArea textArea = new JTextArea(textNormal);
            textArea.setFont(new Font("Tahoma", Font.PLAIN, 16));

            HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new MediaPrintableArea(0f, 0f, width, height, MediaPrintableArea.INCH));
            PrintService printService = getPrinter();
            if (printService != null) {
                textArea.print(null, null, false, printService, attr, false);
            }
        } catch (PrinterException ex) {
            System.err.println(ex.getMessage());
        }

        close();
    }

    private PrintService getPrinter() {
        PrintService[] printService = PrinterJob.lookupPrintServices();

        for (PrintService printService1 : printService) {
            if (PublicVar.printerCheckBill == true) {
                if (printService1.getName().equals(PublicVar.printerCheckBillName)) {
                    PublicVar.printerCheckBill = false;
                    return printService1;
                }
            } else {
                if (printService1.getName().equals(Value.printerDriverName)) {
                    return printService1;
                }
            }
        }

        if (printService.length > 0) {
            return printService[0];
        } else {
            return null;
        }
    }

    private PrintService getPrinterKitchen(String printer) {
        PrintService[] printService = PrinterJob.lookupPrintServices();
//        printer = "Snagit 9";
        for (PrintService printService1 : printService) {
            if (PublicVar.printerCheckBill == true) {
                if (printService1.getName().equals(printer)) {
                    PublicVar.printerCheckBill = false;
                    return printService1;
                }
            } else {
                if (printService1.getName().equals(printer)) {
                    return printService1;
                }
            }
        }

        if (printService.length > 0) {
            return printService[0];
        } else {
            return null;
        }
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

    private PrintService getPrinterKitchenKictran(String printer) {
        PrintService[] printService = PrinterJob.lookupPrintServices();

        for (PrintService printService1 : printService) {
            if (printService1.getName().equals(printer)) {
                return printService1;
            }
        }

        if (printService.length > 0) {
            return printService[0];
        }

        return null;
    }

    public void close() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (OSValidator.isWindows()) {
                    try {
//                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                        UIManager.put("OptionPane.messageFont", new javax.swing.plaf.FontUIResource(new java.awt.Font(
                                "Tahoma", java.awt.Font.PLAIN, 14)));
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        });
    }

    public Image addTextToImage(BufferedImage bufferImage, String[] text) {
        final int VERTICLE_PADDING_PIXELS = 5;
        final int LEFT_MARGIN_PIXELS = 5;
        FontMetrics fm = bufferImage.createGraphics().getFontMetrics();
        int ww = bufferImage.getWidth();
        int hh = bufferImage.getHeight() + (text.length * (fm.getHeight() + VERTICLE_PADDING_PIXELS));

        for (String s : text) {
            ww = Math.max(ww, fm.stringWidth(s) + LEFT_MARGIN_PIXELS);
        }

        BufferedImage result = new BufferedImage(bufferImage.getHeight(), ww, hh);
        Graphics2D g = result.createGraphics();
        g.drawImage(bufferImage, 0, 0, null);

        for (int x = 0; x < text.length; x++) {
            g.drawString(text[x], LEFT_MARGIN_PIXELS, bufferImage.getHeight() + (x + 1) * VERTICLE_PADDING_PIXELS + x * fm.getHeight());
        }

        return result;
    }

}
