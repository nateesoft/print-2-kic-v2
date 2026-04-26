package com.ics.process;

/**
 *
 * @author User
 */
import com.ics.model.TableFileBean;
import com.ics.util.ThaiUtil;
import com.ics.controller.TableFileControl;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

public class PrintKicFormReport {

    private final DecimalFormat intFM = new DecimalFormat("#,##0");
    private static final MySQLConnect mysql = new MySQLConnect();

    public void PrintKicForm8_Report(
            final String tableNo, final String printerName, final String Macno, final String R_ETD, final String R_Index) throws Exception {

        TableFileControl tbControl = new TableFileControl();
        TableFileBean tbBean = tbControl.getData(tableNo);

        ControlPrintCheckBill ctPrint = new ControlPrintCheckBill();

        try {
            PrintKicFormReport report = new PrintKicFormReport();

            // open connection
            mysql.open();

            // source file
            String sourceFileName = getClass().getResource("/com/ics/jasperreport/kicFrom_8Qrcode.jrxml").getPath();
            String reportSource = JasperCompileManager.compileReportToFile(sourceFileName);

            // set parameters
            Map param = new HashMap();
            //Header
            param.put("tableNo", tbBean.getTcode());
            param.put("printerName", printerName);
            param.put("etd", R_ETD);
            String etdThai = R_ETD;
            switch (etdThai) {
                case "E" : {
                    etdThai = "ทานในร้าน";
                    param.put("etdThai", etdThai);
                    break;
                }
                case "T" : {
                    etdThai = "ใส่ห่อ";
                    param.put("etdThai", etdThai);
                    break;
                }
                case "D" : {
                    etdThai = "เดลิเวอรี่";
                    param.put("etdThai", etdThai);
                    break;
                }
                case "P" : {
                    etdThai = "ปิ่นโต";
                    param.put("etdThai", etdThai);
                    break;
                }
                case "W" : {
                    etdThai = "ค้าส่ง";
                    param.put("etdThai", etdThai);
                    break;
                }
            }

            //Footer
            param.put("R_Index", ThaiUtil.ASCII2Unicode(R_Index));
            param.put("custTomer", ThaiUtil.ASCII2Unicode(intFM.format(tbBean.getTCustomer())));

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportSource, param, mysql.getConnection());
            PrinterJob printerJob = PrinterJob.getPrinterJob();

            PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
            printerJob.defaultPage(pageFormat);

            int selectedService = 0;

            AttributeSet attributeSet = new HashPrintServiceAttributeSet(new PrinterName(printerName, null));
            PrintService[] printService = PrintServiceLookup.lookupPrintServices(null, attributeSet);

            try {
                printerJob.setPrintService(printService[selectedService]);
            } catch (PrinterException e) {
                System.err.println(e);
            }
            JRPrintServiceExporter exporter;
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
            printRequestAttributeSet.add(new Copies(1));

            // these are deprecated
            exporter = new JRPrintServiceExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService[selectedService]);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printService[selectedService].getAttributes());
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
            exporter.exportReport();

            param.clear();
            ctPrint.setPrintCheckBillItemAfterSendKic(tableNo);
        } catch (JRException ex) {
            System.err.println(ex.getMessage());
        } finally {
            mysql.close();
        }
    }
}
