package com.ics.process;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.print.PrintService;
import com.ics.constant.Value;
import com.ics.model.BranchBean;
import com.ics.model.ConfigFile;
import com.ics.model.TableFileBean;
import com.ics.model.BalanceBean;
import com.ics.util.ThaiUtil;
import com.ics.util.DateConvert;
import com.ics.controller.BranchControl;
import com.ics.controller.PrintToKicController;
import com.ics.controller.TableFileControl;

public class PrintSimpleForm {

    static SimpleDateFormat simp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
    private DecimalFormat df = new DecimalFormat("##0.00");
    private DecimalFormat inf = new DecimalFormat("#,##0");
    private String Space = " &nbsp; ";
    private String TAB = Space + Space + Space;
    private String TAB2 = TAB + TAB;
    private String ETD = "";
    private String CustomerIn = "";
    private final DateConvert dc = new DateConvert();
    private final MySQLConnect mysql = new MySQLConnect();

    private final PrintToKicController control = new PrintToKicController();

    public String DataFullR(String Str, int Len) {
        String ReturnStr;
        String AddStr = "";
        int List1[] = {209, 212, 213, 214, 215, 216, 217, 218, 219, 231, 232, 233, 234, 235, 236, 237};
        if (Len < Str.length()) {
            ReturnStr = Str.substring(0, Len - 1);
        } else {
            for (int i = 1; i <= (Len - Str.length()); i++) {
                AddStr = AddStr + " ";
            }

            ReturnStr = Str.trim() + AddStr;
        }
        int I = 0;
        int ICnt = 0;
        char ch;
        String TempStr = ThaiUtil.Unicode2ASCII(ReturnStr);
        while (I <= TempStr.length() - 1) {   // Check TIS Upper }

            ch = TempStr.charAt(I);
            if (searchArray((int) ch, List1) != -1) {
                ICnt++;
            }
            I = I + 1;
        }

        if (ICnt > 0) {
            for (int i = 1; i <= ICnt; i++) {
                ReturnStr = ReturnStr + " ";
            }
        }
        return ReturnStr;
    }

    public int searchArray(int key, int[] list) {
        int ans = -1;
        for (int i = 0; i < list.length; i++) {
            if (key == list[i]) {
                ans = i;
            }
        }
        return ans;
    }

    public void KIC_FORM_1(String printerName, final String tableNo, final String[] PCode) {
        String t = "";
        String tt = "";
        String td = "";
        String printerName2 = printerName;
        //FORM 1 **** 
        //จะ CUT ปริ้นทีละสินค้า โดย group by จำนวนสินค้า//
        //โต๊ะ 1           C0
        //***** Eat In *****
        //น้ำลำใย            5
        //__________________
        //28/04/2014 14:15 001/
        //โต๊ะ 1           C0
        //***** Eat In *****
        //น้าตะไคร้ใบเตย       2
        //__________________
        //28/04/2014 14:15 001/
        /**
         * * OPEN CONNECTION **
         */

        ItemVoidPrint(printerName, tableNo, PCode, "E");
        try {
            mysql.open();
            
            String sql = "select TUser,R_Void,R_PluCode,R_Index,TCode, TCustomer, R_PName,R_Quan R_Quan,"
                    + "R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                    + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from tablefile t,balance b "
                    + "where t.tcode=b.r_table "
                    + "and r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'' "
                    + "and R_PluCode='" + PCode[0] + "' "
                    + "and R_ETD='E' "
                    + "and R_VOID<>'V' "
                    + "order by R_Index";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    printerName2 = rs.getString("R_Kic");
                    String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                    String ETD = rs.getString("R_ETD");
                    String macNo = rs.getString("macno");
                    String custCount = rs.getString("TCustomer");
                    double qty = rs.getDouble("R_Quan");
                    String TUser = ThaiUtil.ASCII2Unicode(rs.getString("R_Emp"));
                    String r_index = (rs.getString("R_Index"));

                    //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                    List<String[]> listOpt = new ArrayList<>();
                    try {
                        String sqlOpt = "select * from balance "
                                + "where r_table='" + tableNo + "' "
                                + "and r_pluCode='" + rs.getString("R_PluCode") + "' "
                                + "and r_index='" + r_index + "' "
                                + "and r_void<>'V' ";
                        try (Statement stmt2 = mysql.getConnection().createStatement(); ResultSet rsOpt = stmt2.executeQuery(sqlOpt)) {
                            while (rsOpt.next()) {
                                String Vo = rsOpt.getString("R_Void");
                                String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                if (Vo.equals("V")) {
                                    if (!RVo.equals("")) {
                                        RVo = "ยกเลิก " + RVo;
                                    }
                                }
                                String[] OPT = new String[]{
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                    RVo
                                };
                                
                                listOpt.add(OPT);
                            }
                            
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }

                    //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                    t += "align=left><font face=Angsana New size=5>" + "โต๊ะ " + rs.getString("TCode") + "</td><td align=right><font face=Angsana New size=5>" + " CC. " + "</td><td align=left><font face=Angsana New size=5>" + custCount + "_";
                    t += "colspan=3 align=center>_";
                    switch (ETD) {
                        case "E":
                            ETD = "*** ทานในร้าน ***";
                            break;
                        case "T":
                            ETD = "*** ห่อกลับ ***";
                            break;
                        case "D":
                            ETD = "*** ส่งถึงบ้าน ***";
                            break;
                        case "P":
                            ETD = "*** ปิ่นโต ***";
                            break;
                        case "W":
                            ETD = "*** ขายส่ง ***";
                            break;
                        default:
                            break;
                    }

                    t += "colspan=3 align=center><font face=Angsana New size=5>" + ETD + "_";
                    t += "colspan=3 align=center>_";
                    String product = productName;
                    if (product.length() > 16) {
                        String productSubF = product.substring(0, 16);
                        String productSubR = product.substring(16);

                        product = productSubF + "\n" + productSubR;

                    }
                    t += "colspan=3 align=left><font face=Angsana New size=5>" + (product) + "_";
                    t += "<td colspan=3 align=right><font face=Angsana New size=5>" + df.format(qty) + "_";
                    //********* พิมพ์ข้อความพิเศษ *************
                    for (int x = 0; x < listOpt.size(); x++) {
                        String[] OPT = (String[]) listOpt.get(x);
                        for (String OPT1 : OPT) {
                            if (OPT1 != null) {
                                if (!OPT1.trim().equals("")) {
                                    t += "colspan=5 align=left><font face=Angsana New size=3>" + Space + ("*** " + OPT1) + "_";
                                }
                            }
                        }
                    }
                    //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************
                    t += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                    t += "colspan=3 align=left><font face=Angsana New size=2>" + simp.format(new Date()) + "_";
                    t += "colspan=3 align=left><font face=Angsana New size=2>" + Space + "Mac" + Space + macNo + "/" + TUser + "_";

                    //add kictran data
                    String R_Que = SeekKicItemNo();
                    int TempQue = Integer.parseInt(R_Que);
                    String R_VOID = rs.getString("R_Void");
                    if (R_VOID == null) {
                        R_VOID = "";
                    }
                    //หากมีสินค้า VOID
                    try {
                        if (R_VOID.equals("V")) {
                            String SQLQuery2 = "update kictran "
                                    + "set pvoid = 'V' "
                                    + "where pindex ='" + rs.getString("R_Index") + "' "
                                    + "and ptable='" + rs.getString("R_Table") + "' "
                                    + "and pcode='" + rs.getString("R_PluCode") + "' "
                                    + "and pkic='" + rs.getString("R_Kic") + "' "
                                    + "and pflage='N'";
                            try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                stmt1.executeUpdate(SQLQuery2);
                                stmt1.close();
                            }
                        } else {
                            String sqlK = "insert into kictran "
                                    + "(pitemno,pdate,pcode,pqty,pindex,"
                                    + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                    + "values (" + TempQue + ",curdate(),"
                                    + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                    + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                    + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                    + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                    + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                            try (Statement stmt3 = mysql.getConnection().createStatement()) {
                                stmt3.executeUpdate(sqlK);
                                stmt3.close();
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    PrintDriver pd = new PrintDriver();
                    String[] strs = t.split("_");
                    for (String data1 : strs) {
                        Value.printerDriverKitChenName = "kic" + printerName;
                        pd.addTextIFont(data1);
                    }
                    pd.printHTMLKitChen(Value.printerDriverKitChenName);
                }

            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        ItemVoidPrint(printerName, tableNo, PCode, "T");
        try {
            String sql = "select TUser,R_Void,R_PluCode,R_Index,TCode, TCustomer, R_PName,sum(R_Quan) R_Quan,"
                    + "R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                    + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from tablefile t,balance b "
                    + "where t.tcode=b.r_table "
                    + "and r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'' "
                    + "and R_PluCode='" + PCode[0] + "' "
                    + "and R_ETD='T' "
                    + "and R_VOID<>'V' "
                    + "group by R_PluCode "
                    + "order by R_Index";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    printerName2 = rs.getString("R_Kic");
                    String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                    String ETD = rs.getString("R_ETD");
                    String macNo = rs.getString("macno");
                    String custCount = rs.getString("TCustomer");
                    double qty = rs.getDouble("R_Quan");
                    String TUser = ThaiUtil.ASCII2Unicode(rs.getString("R_Emp"));
                    String r_index = (rs.getString("R_Index"));
                    
                    //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                    List<String[]> listOpt = new ArrayList<>();
                    try {
                        String sqlOpt = "select * from balance "
                                + "where r_table='" + tableNo + "' "
                                + "and r_pluCode='" + rs.getString("R_PluCode") + "' "
                                + "and r_index='" + r_index + "'"
                                + "and r_void<>'V' ";
                        try (Statement stmt2 = mysql.getConnection().createStatement(); ResultSet rsOpt = stmt2.executeQuery(sqlOpt)) {
                            while (rsOpt.next()) {
                                String Vo = rsOpt.getString("R_Void");
                                String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                if (Vo.equals("V")) {
                                    if (!RVo.equals("")) {
                                        RVo = "ยกเลิก " + RVo;
                                    }
                                }
                                String[] OPT = new String[]{
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                    RVo
                                };
                                
                                listOpt.add(OPT);
                            }
                            
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    
                    //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                    tt += "align=left><font face=Angsana New size=5>" + "โต๊ะ " + rs.getString("TCode") + "</td><td align=right><font face=Angsana New size=5>" + " CC. " + "</td><td align=left><font face=Angsana New size=5>" + custCount + "_";
                    tt += "colspan=3 align=center>_";
                    switch (ETD) {
                        case "E":
                            ETD = "*** ทานในร้าน ***";
                            break;
                        case "T":
                            ETD = "*** ห่อกลับ ***";
                            break;
                        case "D":
                            ETD = "*** ส่งถึงบ้าน ***";
                            break;
                        case "P":
                            ETD = "*** ปิ่นโต ***";
                            break;
                        case "W":
                            ETD = "*** ขายส่ง ***";
                            break;
                        default:
                            break;
                    }

                    tt += "colspan=3 align=center><font face=Angsana New size=5>" + ETD + "_";
                    tt += "colspan=3 align=center>_";
                    String product = productName;
                    if (product.length() > 16) {
                        String productSubF = product.substring(0, 16);
                        String productSubR = product.substring(16);
                        product = productSubF + "\n" + productSubR;
                    }
                    tt += "colspan=3 align=left><font face=Angsana New size=5>" + (product) + "_";
                    tt += "<td colspan=3 align=right><font face=Angsana New size=5>" + df.format(qty) + "_";
                    
                    //********* พิมพ์ข้อความพิเศษ *************
                    for (int x = 0; x < listOpt.size(); x++) {
                        String[] OPT = (String[]) listOpt.get(x);
                        for (String OPT1 : OPT) {
                            if (OPT1 != null) {
                                if (!OPT1.trim().equals("")) {
                                    tt += "colspan=5 align=left><font face=Angsana New size=3>" + Space + ("*** " + OPT1) + "_";
                                }
                            }
                        }
                    }
                    //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************
                    
                    tt += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                    tt += "colspan=3 align=left><font face=Angsana New size=2>" + (simp.format(new Date()) + Space + "Mac" + Space + macNo + "/" + TUser) + "_";
                    
                    //add kictran data
                    String R_Que = SeekKicItemNo();
                    int TempQue = Integer.parseInt(R_Que);
                    String R_VOID = rs.getString("R_Void");
                    if (R_VOID == null) {
                        R_VOID = "";
                    }
                    try {
                        if (R_VOID.equals("V")) {
                            String SQLQuery2 = "update kictran "
                                    + "set pvoid = 'V' "
                                    + "where pindex ='" + rs.getString("R_Index") + "' "
                                    + "and ptable='" + rs.getString("R_Table") + "' "
                                    + "and pcode='" + rs.getString("R_PluCode") + "' "
                                    + "and pkic='" + rs.getString("R_Kic") + "' "
                                    + "and pflage='N'";
                            try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                stmt1.executeUpdate(SQLQuery2);
                            }
                        } else {
                            String sqlK = "insert into kictran "
                                    + "(pitemno,pdate,pcode,pqty,pindex,"
                                    + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                    + "values (" + TempQue + ",curdate(),"
                                    + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                    + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                    + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                    + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                    + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                            try (Statement stmt3 = mysql.getConnection().createStatement()) {
                                stmt3.executeUpdate(sqlK);
                                stmt3.close();
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        PrintDriver pd2 = new PrintDriver();
        String[] strs1 = tt.split("_");
        for (String data2 : strs1) {
            Value.printerDriverKitChenName = "kic" + printerName;
            pd2.addTextIFont(data2);
        }

        pd2.printHTMLKitChen(Value.printerDriverKitChenName);

        ItemVoidPrint(printerName, tableNo, PCode, "D");
        try {
            String sql = "select TUser,R_Void,R_PluCode,R_Index,TCode, TCustomer, R_PName,R_Quan R_Quan,"
                    + "R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                    + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from tablefile t,balance b "
                    + "where t.tcode=b.r_table "
                    + "and r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'' "
                    + "and R_PluCode='" + PCode[0] + "' "
                    + "and R_ETD='D' "
                    + "and R_VOID<>'V' "
                    + "group by R_PluCode "
                    + "order by R_Index";
            try (Statement stmt = mysql.getConnection().createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    printerName2 = rs.getString("R_Kic");
                    String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                    String ETD = rs.getString("R_ETD");
                    String macNo = rs.getString("macno");
                    String custCount = rs.getString("TCustomer");
                    double qty = rs.getDouble("R_Quan");
                    String TUser = ThaiUtil.ASCII2Unicode(rs.getString("R_Emp"));
                    String r_index = (rs.getString("R_Index"));
                    
                    //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                    List<String[]> listOpt = new ArrayList<>();
                    try {
                        String sqlOpt = "select * from balance "
                                + "where r_table='" + tableNo + "' "
                                + "and r_pluCode='" + rs.getString("R_PluCode") + "' "
                                + "and r_index='" + r_index + "'"
                                + "and r_void<>'V' ";
                        try (Statement stmt2 = mysql.getConnection().createStatement(); ResultSet rsOpt = stmt2.executeQuery(sqlOpt)) {
                            while (rsOpt.next()) {
                                String Vo = rsOpt.getString("R_Void");
                                String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                if (Vo.equals("V")) {
                                    if (!RVo.equals("")) {
                                        RVo = "ยกเลิก " + RVo;
                                    }
                                }
                                String[] OPT = new String[]{
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                    RVo
                                };
                                listOpt.add(OPT);
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    
                    //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                    td += "align=left><font face=Angsana New size=5>" + "โต๊ะ " + rs.getString("TCode") + "</td><td align=right><font face=Angsana New size=5>" + " CC. " + "</td><td align=left><font face=Angsana New size=5>" + custCount + "_";
                    td += "colspan=3 align=center>_";
                    switch (ETD) {
                        case "E":
                            ETD = "*** ทานในร้าน ***";
                            break;
                        case "T":
                            ETD = "*** ห่อกลับ ***";
                            break;
                        case "D":
                            ETD = "*** ส่งถึงบ้าน ***";
                            break;
                        case "P":
                            ETD = "*** ปิ่นโต ***";
                            break;
                        case "W":
                            ETD = "*** ขายส่ง ***";
                            break;
                        default:
                            break;
                    }

                    td += "colspan=3 align=center><font face=Angsana New size=5>" + ETD + "_";
                    td += "colspan=3 align=center>_";
                    String product = productName;
                    if (product.length() > 16) {
                        String productSubF = product.substring(0, 16);
                        String productSubR = product.substring(16);
                        
                        product = productSubF + "\n" + productSubR;
                        
                    }
                    td += "colspan=3 align=left><font face=Angsana New size=5>" + (product) + "_";
                    td += "<td colspan=3 align=right><font face=Angsana New size=5>" + df.format(qty) + "_";
                    
                    //********* พิมพ์ข้อความพิเศษ *************
                    for (int x = 0; x < listOpt.size(); x++) {
                        String[] OPT = (String[]) listOpt.get(x);
                        for (String OPT1 : OPT) {
                            if (OPT1 != null) {
                                if (!OPT1.trim().equals("")) {
                                    td += "colspan=5 align=left><font face=Angsana New size=3>" + Space + ("*** " + OPT1) + "_";
                                }
                            }
                        }
                    }
                    //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************
                    
                    td += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                    td += "colspan=3 align=left><font face=Angsana New size=2>" + (simp.format(new Date()) + Space + "Mac" + Space + macNo + "/" + TUser) + "_";
                    
                    //add kictran data
                    String R_Que = SeekKicItemNo();
                    int TempQue = Integer.parseInt(R_Que);
                    String R_VOID = rs.getString("R_Void");
                    if (R_VOID == null) {
                        R_VOID = "";
                    }
                    try {
                        if (R_VOID.equals("V")) {
                            String SQLQuery2 = "update kictran "
                                    + "set pvoid = 'V' "
                                    + "where pindex ='" + rs.getString("R_Index") + "' "
                                    + "and ptable='" + rs.getString("R_Table") + "' "
                                    + "and pcode='" + rs.getString("R_PluCode") + "' "
                                    + "and pkic='" + rs.getString("R_Kic") + "' "
                                    + "and pflage='N'";
                            Statement stmt1 = mysql.getConnection().createStatement();
                            stmt1.executeUpdate(SQLQuery2);
                            stmt1.close();
                        } else {
                            String sqlK = "insert into kictran "
                                    + "(pitemno,pdate,pcode,pqty,pindex,"
                                    + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                    + "values (" + TempQue + ",curdate(),"
                                    + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                    + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                    + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                    + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                    + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                            try (Statement stmt3 = mysql.getConnection().createStatement()) {
                                stmt3.executeUpdate(sqlK);
                                stmt3.close();
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        PrintDriver pd3 = new PrintDriver();
        String[] strs3 = td.split("_");
        for (String data3 : strs3) {
            Value.printerDriverKitChenName = "kic" + printerName;
            pd3.addTextIFont(data3);
        }
        pd3.printHTMLKitChen(Value.printerDriverKitChenName);
        String sql = "update balance "
                + "set r_kicprint='P',"
                + "r_pause='Y' "
                + "where r_table='" + tableNo + "' "
                + "and r_kicprint<>'P' "
                + "and r_printOk='Y' "
                + "and r_kic<>'' "
                + "and trantype='PDA';";
        control.execUpdate(sql);
    }

    public void KIC_FORM_3New(final String printerName, final String tableNo, final int R_Kic, final String RETD, final String ATFromPrintToKic, String macno) {
        System.out.println("KIC_FORM_3New : Start Printing....");
        //**** FORM 3 **** 
        //จะปริ้นรวมเป็นแผ่นเดียว//
        //โต๊ะ 1           C0
        //***** Eat In *****
        //น้ำลำใย            3
        //น้ำตะไคร้ใบเตย       1
        //__________________
        //28/04/2014 14:15 001/
        //***** Take Away *****
        //น้ำลำใย            3
        //น้ำตะไคร้ใบเตย       1
        //__________________
        //28/04/2014 14:15 001/

        String textToPrint = "";
        String textToPrint2 = "";
        String tableHead;

        BalanceBean bean = new BalanceBean();
        PrintService[] printerService = PrinterJob.lookupPrintServices();
        for (PrintService p : printerService) {
            String PrinterName = p.getName();
            if (PrinterName.equalsIgnoreCase(printerName)) {
                break;
            }
        }

        tableHead = "โต๊ะ " + ThaiUtil.ASCII2Unicode(tableNo);

        TableFileControl tCon = new TableFileControl();
        TableFileBean tBean = tCon.getData(tableNo);

        List<BalanceBean> listE = new ArrayList<>();
        List<BalanceBean> listT = new ArrayList<>();
        List<BalanceBean> listD = new ArrayList<>();
        List<BalanceBean> listP = new ArrayList<>();
        List<BalanceBean> listW = new ArrayList<>();
        if (ATFromPrintToKic.equals("PDA")) {
            tBean.setTPause("Y");
        }
        if (tBean.getTPause().equals("Y") && tBean.getTOnAct().equals("N")) {
            String sqlNovoid = "select R_Pause,R_Void,R_PluCode, R_PName,"
                    + "sum(R_Quan) R_Quan,R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,"
                    + "R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from balance b "
                    + "where r_table='" + ThaiUtil.Unicode2ASCII(tBean.getTcode()) + "' and r_pause='P' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Void<>'V' "
                    + "and R_Kic='" + R_Kic + "' "
                    + "group by R_Void,R_PluCode, R_PName, "
                    + "R_Price, R_ETD,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,"
                    + "R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic"
                    + " order by r_spindex";
            if (ConfigFile.getProperties("businessType").equals("steak")) {
                sqlNovoid = "select r_index,r_spindex,r_linkindex,R_Void,R_PluCode, R_PName,r_pause,"
                        + "sum(R_Quan) R_Quan,R_Price, b.Macno,R_Date, R_Time,"
                        + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,"
                        + "R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                        + "from balance b "
                        + "where r_table='" + ThaiUtil.Unicode2ASCII(tBean.getTcode()) + "' "
                        + "and r_pause='P' "
                        + "and R_PrintOK='Y' "
                        + "and R_KicPrint<>'P' "
                        + "and R_Void<>'V' "
                        + "and R_Kic='" + R_Kic + "' "
                        + "group by R_Void,R_PluCode, R_PName,R_ETD, "
                        + "R_Price,"
                        + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,"
                        + "R_ETD,b.cashier,R_EMP,R_Table,R_Kic,R_linkIndex,r_index"
                        + " order by r_spindex";
            }
            if (ATFromPrintToKic.equals("PDA")) {
                sqlNovoid = "select r_index,r_spindex,r_linkindex,R_Void,R_PluCode, R_PName,"
                        + "sum(R_Quan) R_Quan,R_Price, b.Macno,R_Date, R_Time,"
                        + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,"
                        + "R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                        + "from balance b "
                        + "where r_table='" + ThaiUtil.Unicode2ASCII(tBean.getTcode()) + "' "
                        + "and Trantype='PDA' "
                        + "and R_PrintOK='Y' "
                        + "and R_KicPrint<>'P' "
                        + "and R_Void<>'V' "
                        + "and R_Kic='" + R_Kic + "' "
                        + "and macno='" + macno + "' "
                        + "and R_Pause='P' "
                        + "group by R_Void,R_PluCode, R_PName,R_ETD, "
                        + "R_Price,"
                        + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,"
                        + "R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic";
            }
            
            //loop Novoid            
            try {
                mysql.open();
                
                try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sqlNovoid)) {
                    while (rs.next()) {
                        bean = new BalanceBean();
                        bean.setR_ETD(rs.getString("R_ETD"));
                        bean.setR_PluCode(rs.getString("R_Plucode"));
                        bean.setR_PName(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_PName").replace("(", " ").replace(")", ""))));
                        bean.setR_Quan(rs.getDouble("R_Quan"));
                        bean.setR_Price(rs.getDouble("R_Price"));
                        bean.setMacno(rs.getString("Macno"));
                        bean.setR_Emp(ThaiUtil.ASCII2Unicode(rs.getString("R_EMP")));
                        bean.setR_Void(rs.getString("R_Void"));
                        bean.setR_Opt1(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt1"))));
                        bean.setR_Opt2(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt2"))));
                        bean.setR_Opt3(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt3"))));
                        bean.setR_Opt4(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt4"))));
                        bean.setR_Opt5(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt5"))));
                        bean.setR_Opt6(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt6"))));
                        bean.setR_Opt7(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt7"))));
                        bean.setR_Opt8(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt8"))));
                        bean.setR_Opt9(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt9"))));
                        bean.setR_Kic(rs.getString("R_Kic"));

                        bean.setR_Index(rs.getString("r_index"));

                        String r_spindex = rs.getString("r_spindex");
                        String r_linkIndex = rs.getString("r_linkindex");

                        if (null == r_spindex || r_spindex.equals("") || r_spindex.equals("null")) {
                            bean.setR_SPIndex(rs.getString("r_index"));
                        } else {
                            bean.setR_SPIndex(rs.getString("r_spindex"));
                        }
                        if (null == r_linkIndex || r_linkIndex.equals("") || r_linkIndex.equals("null")) {
                            bean.setR_LinkIndex(rs.getString("r_index"));
                        } else {
                            bean.setR_LinkIndex(rs.getString("r_linkindex"));
                        }

                        String a = bean.getR_LinkIndex();
                        if (a == null || a.equals("")) {
                            bean.setR_LinkIndex("");
                        }
                        if (bean.getR_ETD().equals("E")) {
                            listE.add(bean);
                        }
                        if (bean.getR_ETD().equals("T")) {
                            listT.add(bean);
                        }
                        if (bean.getR_ETD().equals("D")) {
                            listD.add(bean);
                        }
                        if (bean.getR_ETD().equals("P")) {
                            listP.add(bean);
                        }
                        if (bean.getR_ETD().equals("W")) {
                            listW.add(bean);
                        }
                    }

                    if (!listE.isEmpty()) {
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ทานในร้าน ***") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        for (int i = 0; i < listE.size(); i++) {
                            if (i == listE.size()) {
                                break;
                            } else {
                                if (listE.get(i).getR_SPIndex().equals(listE.get(i).getR_LinkIndex()) && !listE.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listE.get(i).getR_Quan()) + Space + (listE.get(i).getR_PName()) + "_";
                                } else if (listE.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listE.get(i).getR_Quan()) + Space + (listE.get(i).getR_PName()) + "_";
                                } else {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + Space + "-" + Space + (listE.get(i).getR_PName()) + "_";
                                }
                                if (!listE.get(i).getR_Opt1().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt1()) + "_";
                                }
                                if (!listE.get(i).getR_Opt2().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt2()) + "_";
                                }
                                if (!listE.get(i).getR_Opt3().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt3()) + "_";
                                }
                                if (!listE.get(i).getR_Opt4().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt4()) + "_";
                                }
                                if (!listE.get(i).getR_Opt5().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt5()) + "_";
                                }
                                if (!listE.get(i).getR_Opt6().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt6()) + "_";
                                }
                                if (!listE.get(i).getR_Opt7().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt7()) + "_";
                                }
                                if (!listE.get(i).getR_Opt8().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listE.get(i).getR_Opt8()) + "_";
                                }
                            }
                        }
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        ThaiUtil.ASCII2Unicode(bean.getR_Emp());
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + ThaiUtil.ASCII2Unicode(bean.getR_Emp()) + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + "_";

                    }
                    if (!listT.isEmpty()) {
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ห่อกลับ ***") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        for (int i = 0; i < listT.size(); i++) {
                            if (i == listT.size()) {
                                break;
                            } else {
                                if (listT.get(i).getR_SPIndex().equals(listT.get(i).getR_LinkIndex()) && !listT.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listT.get(i).getR_Quan()) + Space + (listT.get(i).getR_PName()) + "_";
                                } else if (listT.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listT.get(i).getR_Quan()) + Space + (listT.get(i).getR_PName()) + "_";
                                } else {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + Space + "-" + Space + (listT.get(i).getR_PName()) + "_";
                                }
                                if (!listT.get(i).getR_Opt1().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt1()) + "_";
                                }
                                if (!listT.get(i).getR_Opt2().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt2()) + "_";
                                }
                                if (!listT.get(i).getR_Opt3().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt3()) + "_";
                                }
                                if (!listT.get(i).getR_Opt4().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt4()) + "_";
                                }
                                if (!listT.get(i).getR_Opt5().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt5()) + "_";
                                }
                                if (!listT.get(i).getR_Opt6().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt6()) + "_";
                                }
                                if (!listT.get(i).getR_Opt7().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt7()) + "_";
                                }
                                if (!listT.get(i).getR_Opt8().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listT.get(i).getR_Opt8()) + "_";
                                }
                            }
                        }
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        ThaiUtil.ASCII2Unicode(bean.getR_Emp());
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + ThaiUtil.ASCII2Unicode(bean.getR_Emp()) + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                    if (!listD.isEmpty()) {
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ส่งถึงบ้าน ***") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        for (int i = 0; i < listD.size(); i++) {
                            if (i == listD.size()) {
                                break;
                            } else {
                                if (listD.get(i).getR_SPIndex().equals(listD.get(i).getR_LinkIndex()) && !listD.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listD.get(i).getR_Quan()) + Space + (listD.get(i).getR_PName()) + "_";
                                } else if (listD.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listD.get(i).getR_Quan()) + Space + (listD.get(i).getR_PName()) + "_";
                                } else {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + Space + "-" + Space + (listD.get(i).getR_PName()) + "_";
                                }

                                if (!listD.get(i).getR_Opt1().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt1()) + "_";
                                }
                                if (!listD.get(i).getR_Opt2().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt2()) + "_";
                                }
                                if (!listD.get(i).getR_Opt3().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt3()) + "_";
                                }
                                if (!listD.get(i).getR_Opt4().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt4()) + "_";
                                }
                                if (!listD.get(i).getR_Opt5().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt5()) + "_";
                                }
                                if (!listD.get(i).getR_Opt6().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt6()) + "_";
                                }
                                if (!listD.get(i).getR_Opt7().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt7()) + "_";
                                }
                                if (!listD.get(i).getR_Opt8().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listD.get(i).getR_Opt8()) + "_";
                                }
                            }
                        }
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        ThaiUtil.ASCII2Unicode(bean.getR_Emp());
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + ThaiUtil.ASCII2Unicode(bean.getR_Emp()) + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                    if (!listP.isEmpty()) {
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ปิ่นโต ***") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        for (int i = 0; i < listP.size(); i++) {
                            if (i == listP.size()) {
                                break;
                            } else {
                                if (listP.get(i).getR_SPIndex().equals(listP.get(i).getR_LinkIndex()) && !listP.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listP.get(i).getR_Quan()) + Space + (listP.get(i).getR_PName()) + "_";
                                } else if (listP.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listP.get(i).getR_Quan()) + Space + (listP.get(i).getR_PName()) + "_";
                                } else {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + Space + "-" + Space + (listP.get(i).getR_PName()) + "_";
                                }
                                if (!listP.get(i).getR_Opt1().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt1()) + "_";
                                }
                                if (!listP.get(i).getR_Opt2().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt2()) + "_";
                                }
                                if (!listP.get(i).getR_Opt3().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt3()) + "_";
                                }
                                if (!listP.get(i).getR_Opt4().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt4()) + "_";
                                }
                                if (!listP.get(i).getR_Opt5().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt5()) + "_";
                                }
                                if (!listP.get(i).getR_Opt6().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt6()) + "_";
                                }
                                if (!listP.get(i).getR_Opt7().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt7()) + "_";
                                }
                                if (!listP.get(i).getR_Opt8().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listP.get(i).getR_Opt8()) + "_";
                                }
                            }
                        }
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        ThaiUtil.ASCII2Unicode(bean.getR_Emp());
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + ThaiUtil.ASCII2Unicode(bean.getR_Emp()) + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                    if (!listW.isEmpty()) {
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ค้าส่ง ***") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        for (int i = 0; i < listW.size(); i++) {
                            if (i == listW.size()) {
                                break;
                            } else {
                                if (listW.get(i).getR_SPIndex().equals(listW.get(i).getR_LinkIndex()) && !listW.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listW.get(i).getR_Quan()) + Space + (listW.get(i).getR_PName()) + "_";
                                } else if (listW.get(i).getR_LinkIndex().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + inf.format(listW.get(i).getR_Quan()) + Space + (listW.get(i).getR_PName()) + "_";
                                } else {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=4>" + Space + "-" + Space + (listW.get(i).getR_PName()) + "_";
                                }

                                if (!listW.get(i).getR_Opt1().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt1()) + "_";
                                }
                                if (!listW.get(i).getR_Opt2().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt2()) + "_";
                                }
                                if (!listW.get(i).getR_Opt3().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt3()) + "_";
                                }
                                if (!listW.get(i).getR_Opt4().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt4()) + "_";
                                }
                                if (!listW.get(i).getR_Opt5().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt5()) + "_";
                                }
                                if (!listW.get(i).getR_Opt6().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt6()) + "_";
                                }
                                if (!listW.get(i).getR_Opt7().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt7()) + "_";
                                }
                                if (!listW.get(i).getR_Opt8().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listW.get(i).getR_Opt8()) + "_";
                                }
                            }
                        }
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        ThaiUtil.ASCII2Unicode(bean.getR_Emp());
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + ThaiUtil.ASCII2Unicode(bean.getR_Emp()) + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            } finally {
                mysql.close();
            }

            //loop Void to Print
            //loop R_Indulgent =  Y : คือใส่จำนวนตามน้ำหนักมา
        }
        PrintDriver printDriver = new PrintDriver();
        String[] strs = textToPrint.split("_");
        for (String data1 : strs) {
            Value.printerDriverKitChenName = printerName;
            printDriver.addTextIFont(data1);
        }
        try {
            printDriver.printHTMLKitChen(Value.printerDriverKitChenName);
            String sql = "";
            if (!listE.isEmpty()) {
                for (int i = 0; i < listE.size(); i++) {

                    try {
                        String r_index = listE.get(i).getR_Index();
                        String r_plucode = listE.get(i).getR_PluCode();
                        String r_kic = listE.get(i).getR_Kic();

                        if (ATFromPrintToKic.equals("PDA")) {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and trantype = 'PDA'"
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='E' ;";
                        } else {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and trantype <> 'PDA'"
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='E' ;";
                        }
                        control.execUpdate(sql);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            if (!listT.isEmpty()) {
                try {
                    for (int i = 0; i < listT.size(); i++) {
                        String r_index = listT.get(i).getR_Index();
                        String r_plucode = listT.get(i).getR_PluCode();
                        String r_kic = listT.get(i).getR_Kic();
                        if (ATFromPrintToKic.equals("PDA")) {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and trantype = 'PDA'"
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='T' ;";
                        } else {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and trantype <> 'PDA'"
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='T' ;";
                        }
                        control.execUpdate(sql);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }
            if (!listD.isEmpty()) {
                try {
                    for (int i = 0; i < listD.size(); i++) {
                        String r_plucode = listD.get(i).getR_PluCode();
                        String r_kic = listD.get(i).getR_Kic();
                        if (ATFromPrintToKic.equals("PDA")) {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and trantype = 'PDA'"
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='D' ;";
                        } else {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and trantype <> 'PDA'"
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='D' ;";
                        }
                        control.execUpdate(sql);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }
            if (!listP.isEmpty()) {
                try {
                    for (int i = 0; i < listP.size(); i++) {
                        String r_plucode = listP.get(i).getR_PluCode();
                        String r_kic = listP.get(i).getR_Kic();
                        if (ATFromPrintToKic.equals("PDA")) {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and trantype = 'PDA'"
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='P' ;";
                        } else {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and trantype <> 'PDA'"
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='P' ;";
                        }
                        control.execUpdate(sql);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }
            if (!listW.isEmpty()) {
                try {
                    for (int i = 0; i < listW.size(); i++) {
                        String r_plucode = listP.get(i).getR_PluCode();
                        String r_kic = listW.get(i).getR_Kic();
                        if (ATFromPrintToKic.equals("PDA")) {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and trantype = 'PDA'"
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='W' ;";
                        } else {
                            sql = "update balance "
                                    + "set r_kicprint='P',"
                                    + "r_pause='Y' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kicprint<>'P' "
                                    + "and r_printOk='Y' "
                                    + "and r_kic<>'' "
                                    + "and macno='" + macno + "' "
                                    + "and R_Pause='P' "
                                    + "and r_plucode='" + r_plucode + "' "
                                    + "and trantype <> 'PDA'"
                                    + "and R_kic='" + r_kic + "'"
                                    + "and r_etd='W' ;";
                        }
                        control.execUpdate(sql);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        //Loop Void
        List<BalanceBean> listVE = new ArrayList<>();
        List<BalanceBean> listVT = new ArrayList<>();
        List<BalanceBean> listVD = new ArrayList<>();
        List<BalanceBean> listVP = new ArrayList<>();
        List<BalanceBean> listVW = new ArrayList<>();
        if (!ATFromPrintToKic.equals("PDA") && ATFromPrintToKic.equals("")) {
            String sqlvoid = "select R_EMP, R_Void, R_PluCode, R_PName,sum(R_Quan) R_Quan,R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from balance b "
                    + "where r_table='" + ThaiUtil.Unicode2ASCII(tBean.getTcode()) + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_ETD='" + RETD + "' "
                    + "and R_Void='V' "
                    + "group by R_EMP, R_Void, R_PluCode, R_PName, R_Price,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "order by r_etd;";
            //loop Novoid
            MySQLConnect mysql = new MySQLConnect();
            try {
                mysql.open();
                ResultSet rs;
                try (Statement stmt = mysql.getConnection().createStatement()) {
                    rs = stmt.executeQuery(sqlvoid);
                    while (rs.next()) {
                        bean = new BalanceBean();
                        bean.setR_ETD(rs.getString("R_ETD"));
                        bean.setR_PluCode(rs.getString("R_Plucode"));
                        bean.setR_PName(SubString(ThaiUtil.ASCII2Unicode(rs.getString("R_PName"))));
                        bean.setR_Emp(rs.getString("R_EMP"));
                        bean.setR_Quan(rs.getDouble("R_Quan"));
                        bean.setR_Price(rs.getDouble("R_Price"));
                        bean.setMacno(rs.getString("Macno"));
                        bean.setR_Void(rs.getString("R_Void"));
                        bean.setR_Opt1(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt1")));
                        bean.setR_Opt2(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt2")));
                        bean.setR_Opt3(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt3")));
                        bean.setR_Opt4(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt4")));
                        bean.setR_Opt5(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt5")));
                        bean.setR_Opt6(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt6")));
                        bean.setR_Opt7(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt7")));
                        bean.setR_Opt8(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt8")));
                        bean.setR_Opt9(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt9")));
                        bean.setR_Kic(rs.getString("R_Kic"));
                        if (bean.getR_ETD().equals("E")) {
                            listVE.add(bean);
                        }
                        if (bean.getR_ETD().equals("T")) {
                            listVT.add(bean);
                        }
                        if (bean.getR_ETD().equals("D")) {
                            listVD.add(bean);
                        }
                        if (bean.getR_ETD().equals("P")) {
                            listVP.add(bean);
                        }
                        if (bean.getR_ETD().equals("W")) {
                            listVW.add(bean);
                        }
                    }
                    if (!listVE.isEmpty()) {
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ยกเลิก ทานในร้าน ***") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        for (int i = 0; i < listVE.size(); i++) {
                            if (i == listVE.size()) {
                                break;
                            } else {
                                textToPrint2 += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(listVE.get(i).getR_Quan()) + Space + (listVE.get(i).getR_PName()) + "_";
                                if (!listVE.get(i).getR_Opt1().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt1()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt2().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt2()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt3().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt3()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt4().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt4()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt5().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt5()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt6().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt6()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt7().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt7()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt8().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt8()) + "_";
                                }
                                if (!listVE.get(i).getR_Opt9().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVE.get(i).getR_Opt9()) + "_";
                                }
                            }
                        }
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        ThaiUtil.ASCII2Unicode(bean.getR_Emp());
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + bean.getR_Emp() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + "_";

                    }
                    if (!listVT.isEmpty()) {
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ยกเลิก ห่อกลับ ***") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        for (int i = 0; i < listVT.size(); i++) {
                            if (i == listVT.size()) {
                                break;
                            } else {
                                textToPrint2 += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(listVT.get(i).getR_Quan()) + Space + (listVT.get(i).getR_PName()) + "_";
                                if (!listVT.get(i).getR_Opt1().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt1()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt2().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt2()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt3().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt3()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt4().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt4()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt5().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt5()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt6().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt6()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt7().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt7()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt8().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt8()) + "_";
                                }
                                if (!listVT.get(i).getR_Opt9().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVT.get(i).getR_Opt9()) + "_";
                                }
                            }
                        }
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + bean.getR_Emp() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                    if (!listVD.isEmpty()) {
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ยกเลิก ส่งถึงบ้าน ***") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        for (int i = 0; i < listVD.size(); i++) {
                            if (i == listVD.size()) {
                                break;
                            } else {
                                textToPrint2 += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(listVD.get(i).getR_Quan()) + Space + (listVD.get(i).getR_PName()) + "_";
                                if (!listVD.get(i).getR_Opt1().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt1()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt2().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt2()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt3().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt3()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt4().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt4()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt5().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt5()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt6().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt6()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt7().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt7()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt8().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt8()) + "_";
                                }
                                if (!listVD.get(i).getR_Opt9().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVD.get(i).getR_Opt9()) + "_";
                                }
                            }
                        }
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + bean.getR_Emp() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                    if (!listVP.isEmpty()) {
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ยกเลิก ปิ่นโต ***") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        for (int i = 0; i < listVP.size(); i++) {
                            if (i == listVP.size()) {
                                break;
                            } else {
                                textToPrint2 += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(listVP.get(i).getR_Quan()) + Space + (listVP.get(i).getR_PName()) + "_";
                                if (!listVP.get(i).getR_Opt1().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt1()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt2().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt2()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt3().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt3()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt4().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt4()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt5().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt5()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt6().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt6()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt7().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt7()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt8().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt8()) + "_";
                                }
                                if (!listVP.get(i).getR_Opt9().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVP.get(i).getR_Opt9()) + "_";
                                }
                            }
                        }
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + bean.getR_Emp() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                    if (!listVW.isEmpty()) {
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=5>" + (tableHead) + "</td><td colspan=2 align=right><font face=Angsana New size=5>" + "ลูกค้า : " + tBean.getTCustomer() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";

                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=5>" + ("*** ยกเลิก ค้าส่ง ***") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("") + "_";
                        for (int i = 0; i < listVW.size(); i++) {
                            if (i == listVW.size()) {
                                break;
                            } else {
                                textToPrint2 += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(listVW.get(i).getR_Quan()) + Space + (listVW.get(i).getR_PName()) + "_";
                                if (!listVW.get(i).getR_Opt1().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt1()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt2().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt2()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt3().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt3()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt4().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt4()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt5().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt5()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt6().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt6()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt7().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt7()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt8().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt8()) + "_";
                                }
                                if (!listVW.get(i).getR_Opt9().equals("")) {
                                    textToPrint2 += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + Space + "**" + (listVW.get(i).getR_Opt9()) + "_";
                                }
                            }
                        }
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + dc.dateGetToShow(dc.GetCurrentDate()) + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + dc.GetCurrentTime() + "_";
                        textToPrint2 += "colspan=1 align=left><font face=Angsana New size=3>" + "เครื่อง : " + bean.getMacno() + "</td><td colspan=2 align=right><font face=Angsana New size=3>" + "พนักงาน : " + bean.getR_Emp() + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + printerName + "_";
                        textToPrint2 += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                    }
                }
                rs.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            } finally {
                mysql.close();
            }

            textToPrint2 = changeFontSize(textToPrint2);
            PrintDriver pd1 = new PrintDriver();
            String[] strs1 = textToPrint2.split("_");
            for (String data2 : strs1) {
                Value.printerDriverKitChenName = printerName;
                pd1.addTextIFont(data2);
            }
            try {
                pd1.printHTMLKitChen(Value.printerDriverKitChenName);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            try {
                String sql = "update balance "
                        + "set r_kicprint='P',"
                        + "r_pause='Y' "
                        + "where r_table='" + tableNo + "' "
                        + "and r_kicprint<>'P' "
                        + "and r_printOk='Y' "
                        + "and r_kic<>'' "
                        + "and r_void<>'V' "
                        + "and macno='" + macno + "' "
                        + "and R_Kic='" + R_Kic + "'"
                        + ";";
                control.execUpdate(sql);
                sql = "update balance "
                        + "set r_kicprint='P',"
                        + "r_pause='Y' "
                        + "where r_table='" + tableNo + "' "
                        + "and r_kicprint<>'P' "
                        + "and r_printOk='Y' "
                        + "and r_kic<>'' "
                        + "and r_void='V' "
                        + "and macno='" + macno + "' "
                        + "and R_Kic='" + R_Kic + "'"
                        + ";";
                control.execUpdate(sql);
                mysql.close();
            } catch (Exception e) {
            }
        }
    }

    public String keepTextShow(String R_Index, int qty, String product) {
        String t = "";
        MySQLConnect mysql = new MySQLConnect();
        mysql.open();
        try {
            String sql = "select R_Index, R_LinkIndex,R_Void from balance where R_Index='" + R_Index + "' limit 1";
            Statement stmt = mysql.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String r_void = rs.getString("R_void");
                if (r_void.equals("V")) {
                    if (product.length() > 14) {
                        String productSubF = product.substring(0, 14);
                        String productSubR = product.substring(14);

                        product = productSubF + "\n" + productSubR;

                    }
                    String R_LinkIndex = rs.getString("R_LinkIndex");
                    if (R_LinkIndex.equals(R_Index)) {
                        t += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(qty) + Space + (product) + "_";
                    } else if (!R_LinkIndex.equals("") && !R_LinkIndex.equals(R_Index)) {
                        t += "colspan=3 align=center><font face=Angsana New size=3>" + ("-") + "_";
                        t += "colspan=3 align=left><font face=Angsana New size=5>" + (product) + "_";
                    } else {
                        t += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(qty) + Space + (product) + "_";
                    }
                } else {
                    if (product.length() > 14) {
                        String productSubF = product.substring(0, 14);
                        String productSubR = product.substring(14);

                        product = productSubF + "\n" + productSubR;
                    }
                    String R_LinkIndex = rs.getString("R_LinkIndex");
                    if (R_LinkIndex.equals(R_Index)) {
                        t += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(qty) + Space + (product) + "_";
                    } else if (!R_LinkIndex.equals("") && !R_LinkIndex.equals(R_Index)) {
                        t += "colspan=3 align=center><font face=Angsana New size=3>" + ("-") + "_";
                        t += "colspan=3 align=left><font face=Angsana New size=5>" + (product) + "_";
                    } else {
                        t += "colspan=3 align=left><font face=Angsana New size=5>" + inf.format(qty) + Space + (product) + "_";
                    }
                }

            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }
        return t;
    }

    public void KIC_FORM_4(final String printerName, final String tableNo) {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(() -> {
            System.out.println("KIC_FORM_4 : Start Printing....");
            final int SpaceFront = 25;
            final int PaperMaxLength = 28;

            PrinterJob pj = PrinterJob.getPrinterJob();
            PrintService[] ps = PrinterJob.lookupPrintServices();
            int prnIndex = 0;
            try {
                for (int i = 0; i < ps.length; i++) {
                    String PrinterName = ps[i].getName();
                    if (PrinterName.equalsIgnoreCase(printerName)) {
                        prnIndex = i;
                        break;
                    }
                }
                pj.setPrintService(ps[prnIndex]);

                PageFormat pf = new PageFormat();
                Paper pp = new Paper();
                pp.setSize(500, 1000);
                pp.setImageableArea(0, 0, 594, 846);
                pf.setPaper(pp);
                pj.setPrintable(new Printable() {

                    //**** FORM 4 ****
                    //จะปริ้นรวมเป็นแผ่นเดียว ไม่มีการตัดรายการ แต่จะแยกด้วย __________ //
                    //โต๊ะ 1           C0
                    //***** Eat In *****
                    //น้ำลำใย
                    //จำนวน  2 ราคา 45.00
                    //__________________
                    //28/04/2014 14:15 001/
                    //โต๊ะ 1           C0
                    //***** Eat In *****
                    //น้ำตะใคร้ใบเตย      1
                    //จำนวน  1 ราคา 45.00
                    //__________________
                    //28/04/2014 14:15 001/
                    @Override
                    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
                        Graphics2D g2 = (Graphics2D) g;
                        if (index == 0) {
                            MySQLConnect mysql = new MySQLConnect();
                            mysql.open();
                            try {
                                String sql = "select TUser, R_Void,R_Index, R_PluCode,TCode, TCustomer, R_PName,sum(R_Quan) R_Quan,"
                                        + "R_Price, b.Macno,R_Date, R_Time,"
                                        + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                                        + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                                        + "from tablefile t,balance b "
                                        + "where t.tcode=b.r_table "
                                        + "and r_table='" + tableNo + "' "
                                        + "and R_PrintOK='Y' "
                                        + "and R_KicPrint<>'P' "
                                        + "and R_Kic<>'' "
                                        + "group by R_PluCode order by R_Index";
                                try (Statement stmt = mysql.getConnection().createStatement()) {
                                    ResultSet rs = stmt.executeQuery(sql);
                                    int line = 0;
                                    String macNo = "";
                                    boolean printTable = false;
                                    String TUser = "";
                                    while (rs.next()) {
                                        String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                                        String ETD = rs.getString("R_ETD");
                                        macNo = rs.getString("macno");
                                        String custCount = rs.getString("TCustomer");
                                        int qty = rs.getInt("R_Quan");
                                        TUser = ThaiUtil.ASCII2Unicode(rs.getString("TUser"));

                                        //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                                        List<String[]> listOpt = new ArrayList<>();
                                        try {
                                            String sqlOpt = "select * from balance "
                                                    + "where r_table='" + tableNo + "' and r_pluCode='" + rs.getString("R_PluCode") + "'";
                                            try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                                ResultSet rsOpt = stmt.executeQuery(sqlOpt);
                                                while (rsOpt.next()) {
                                                    String Vo = rsOpt.getString("R_Void");
                                                    String RVo = rsOpt.getString("r_opt9");
                                                    if (Vo.equals("V")) {
                                                        if (!RVo.equals("")) {
                                                            RVo = "ยกเลิก " + RVo;
                                                        }
                                                    }

                                                    String[] OPT = new String[]{
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                                        ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                                        RVo
                                                    };

                                                    listOpt.add(OPT);

                                                }
                                                rsOpt.close();
                                            }
                                        } catch (SQLException e) {
                                            System.err.println(e.getMessage());
                                        }

                                        //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                                        line += 25;
                                        g2.setFont(new Font("AngsanaUPC", Font.PLAIN, 20));
                                        if (!printTable) {
                                            String tableHead = DataFullR("โต๊ะ " + rs.getString("TCode"), PaperMaxLength - 3);
                                            g2.drawString(tableHead + " C " + custCount, SpaceFront, line);
                                            line += 25;

                                            printTable = true;
                                        }
                                        //print ETD
                                        printG(g2, ETD, line);

                                        line += 25;
                                        g2.setFont(new Font("AngsanaUPC", Font.PLAIN, 15));
                                        g2.drawString(productName, SpaceFront, line);
                                        //********* พิมพ์ข้อความพิเศษ *************
                                        for (int x = 0; x < listOpt.size(); x++) {
                                            String[] OPT = (String[]) listOpt.get(x);
                                            for (String OPT1 : OPT) {
                                                if (OPT1 != null) {
                                                    if (!OPT1.trim().equals("")) {
                                                        line += 20;
                                                        g2.drawString("*** " + OPT1, SpaceFront, line);
                                                    }
                                                }
                                            }
                                        }
                                        //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************
                                        line += 20;
                                        g2.setFont(new Font("AngsanaUPC", Font.PLAIN, 15));
                                        g2.drawString("จำนวน    " + qty + "      ราคา " + rs.getDouble("R_Price"), SpaceFront, line);
                                        line += 20;
                                        g2.drawString("-----------------------------------------", SpaceFront, line);

                                        //add kictran data
                                        String R_Que = SeekKicItemNo();
                                        int TempQue = Integer.parseInt(R_Que);
                                        String R_VOID = rs.getString("R_Void");
                                        if (R_VOID == null) {
                                            R_VOID = "";
                                        }
                                        try {
                                            if (R_VOID.equals("V")) {
                                                String SQLQuery2 = "update kictran "
                                                        + "set pvoid = 'V' "
                                                        + "where pindex ='" + rs.getString("R_Index") + "' "
                                                        + "and ptable='" + rs.getString("R_Table") + "' "
                                                        + "and pcode='" + rs.getString("R_PluCode") + "' "
                                                        + "and pkic='" + rs.getString("R_Kic") + "' "
                                                        + "and pflage='N'";
                                                Statement stmt2 = mysql.getConnection().createStatement();
                                                stmt2.executeUpdate(SQLQuery2);
                                                stmt2.close();
                                            } else {
                                                String sqlK = "insert into kictran "
                                                        + "(pitemno,pdate,pcode,pqty,pindex,"
                                                        + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                                        + "values (" + TempQue + ",curdate(),"
                                                        + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                                        + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                                        + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                                        + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                                        + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                                                Statement stmt2 = mysql.getConnection().createStatement();
                                                stmt2.executeUpdate(sqlK);
                                                stmt2.close();
                                            }
                                        } catch (SQLException e) {
                                            System.err.println(e.getMessage());
                                        }
                                    }

                                    line += 20;
                                    g2.setFont(new Font("AngsanaUPC", Font.PLAIN, 12));
                                    g2.drawString("  " + simp.format(new Date()) + "   Mac " + macNo + "/" + TUser, SpaceFront, line);

                                    rs.close();
                                }
                            } catch (SQLException e) {
                                System.err.println(e.getMessage());
                            } finally {
                                mysql.close();
                            }

                            return PAGE_EXISTS;
                        } else {
                            return NO_SUCH_PAGE;
                        }
                    }
                }, pf);
                try {
                    pj.print();
                } catch (PrinterException e) {
                    System.err.println(e.getMessage());
                }
            } catch (PrinterException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    public void KIC_FORM_5(final String printerName, final String tableNo) {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(() -> {
            System.out.println("KIC_FORM_5 : Start Printing....");
            final int SpaceFront = 25;
            final int PaperMaxLength = 28;

            PrinterJob pj = PrinterJob.getPrinterJob();
            PrintService[] ps = PrinterJob.lookupPrintServices();
            int prnIndex = 0;
            try {
                for (int i = 0; i < ps.length; i++) {
                    String PrinterName = ps[i].getName();
                    if (PrinterName.equalsIgnoreCase(printerName)) {
                        prnIndex = i;
                        break;
                    }
                }
                pj.setPrintService(ps[prnIndex]);

                PageFormat pf = new PageFormat();
                Paper pp = new Paper();
                pp.setSize(500, 1000);
                pp.setImageableArea(0, 0, 594, 846);
                pf.setPaper(pp);
                pj.setPrintable(new Printable() {

                    //**** FORM 5 ****
                    //จะปริ้นรวมเป็นแผ่นเดียว//
                    //โต๊ะ 1           C0
                    //***** Eat In *****
                    //--- น้ำลำใย         3
                    //--- น้ำตะไคร้ใบเตย    1
                    //__________________
                    //28/04/2014 14:15 001/
                    @Override
                    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
                        Graphics2D g2 = (Graphics2D) g;
                        if (index == 0) {
                            /**
                             * * OPEN CONNECTION **
                             */
                            MySQLConnect mysql = new MySQLConnect();
                            mysql.open();
                            try {
                                String sql = "select TUser,R_Void,R_Index, R_PluCode,TCode, TCustomer, R_PName,sum(R_Quan) R_Quan,"
                                        + "R_Price, b.Macno,R_Date, R_Time,"
                                        + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                                        + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                                        + "from tablefile t,balance b "
                                        + "where t.tcode=b.r_table "
                                        + "and r_table='" + tableNo + "' "
                                        + "and R_PrintOK='Y' "
                                        + "and R_KicPrint<>'P' "
                                        + "and R_Kic<>'' "
                                        + "group by R_PluCode order by R_Index";
                                try (Statement stmt = mysql.getConnection().createStatement()) {
                                    ResultSet rs = stmt.executeQuery(sql);
                                    int line = 0;
                                    String macNo = "";
                                    String TUser = "";
                                    boolean printHeader = false;
                                    while (rs.next()) {
                                        String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                                        String ETD = rs.getString("R_ETD");
                                        macNo = rs.getString("macno");
                                        String custCount = rs.getString("TCustomer");
                                        int qty = rs.getInt("R_Quan");
                                        TUser = ThaiUtil.ASCII2Unicode(rs.getString("TUser"));

                                        //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                                        List<String[]> listOpt = new ArrayList<>();
                                        try {
                                            String sqlOpt = "select * from balance "
                                                    + "where r_table='" + tableNo + "' and r_pluCode='" + rs.getString("R_PluCode") + "'";
                                            Statement stmt1 = mysql.getConnection().createStatement();
                                            ResultSet rsOpt = stmt1.executeQuery(sqlOpt);
                                            while (rsOpt.next()) {
                                                String Vo = rsOpt.getString("R_Void");
                                                String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                                if (Vo.equals("V")) {
                                                    if (!RVo.equals("")) {
                                                        RVo = "ยกเลิก " + RVo;
                                                    }
                                                }
                                                String[] OPT = new String[]{
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                                    RVo
                                                };

                                                listOpt.add(OPT);
                                            }

                                            rsOpt.close();
                                            stmt1.close();
                                        } catch (SQLException e) {
                                            System.err.println(e.getMessage());
                                        }

                                        //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                                        if (!printHeader) {
                                            line += 25;
                                            g2.setFont(new Font("Thahoma", Font.PLAIN, 30));
                                            String tableHead = DataFullR("โต๊ะ " + rs.getString("TCode"), PaperMaxLength - 3);
                                            g2.drawString(tableHead + " C " + custCount, SpaceFront, line);
                                            line += 25;

                                            //print ETD
                                            printG(g2, ETD, line);
                                            printHeader = true;
                                        }
                                        line += 25;
                                        g2.setFont(new Font("Thahoma", Font.PLAIN, 20));
                                        String product = DataFullR(productName, PaperMaxLength);

                                        g2.drawString("---" + product, SpaceFront, line);
                                        g2.drawString("" + qty, SpaceFront + 155, line);

                                        //********* พิมพ์ข้อความพิเศษ *************
                                        for (int x = 0; x < listOpt.size(); x++) {
                                            String[] OPT = (String[]) listOpt.get(x);
                                            for (String OPT1 : OPT) {
                                                if (OPT1 != null) {
                                                    if (!OPT1.trim().equals("")) {
                                                        line += 20;
                                                        g2.drawString("*** " + OPT1, SpaceFront + 5, line);
                                                    }
                                                }
                                            }
                                        }
                                        //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************

                                        //add kictran data
                                        String R_Que = SeekKicItemNo();
                                        int TempQue = Integer.parseInt(R_Que);
                                        String R_VOID = rs.getString("R_Void");
                                        if (R_VOID == null) {
                                            R_VOID = "";
                                        }
                                        try {
                                            if (R_VOID.equals("V")) {
                                                String SQLQuery2 = "update kictran "
                                                        + "set pvoid = 'V' "
                                                        + "where pindex ='" + rs.getString("R_Index") + "' "
                                                        + "and ptable='" + rs.getString("R_Table") + "' "
                                                        + "and pcode='" + rs.getString("R_PluCode") + "' "
                                                        + "and pkic='" + rs.getString("R_Kic") + "' "
                                                        + "and pflage='N'";
                                                try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                                    stmt1.executeUpdate(SQLQuery2);
                                                }
                                            } else {
                                                String sqlK = "insert into kictran "
                                                        + "(pitemno,pdate,pcode,pqty,pindex,"
                                                        + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                                        + "values (" + TempQue + ",curdate(),"
                                                        + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                                        + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                                        + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                                        + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                                        + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                                                try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                                    stmt1.executeUpdate(sqlK);
                                                }
                                            }
                                        } catch (SQLException e) {
                                            System.err.println(e.getMessage());
                                        }
                                    }

                                    //Print header
                                    line += 20;
                                    g2.setFont(new Font("Thahoma", Font.PLAIN, 20));
                                    g2.drawString("-----------------------------------------", SpaceFront, line);
                                    line += 20;

                                    g2.setFont(new Font("Thahoma", Font.PLAIN, 20));
                                    g2.drawString("  " + simp.format(new Date()) + "   Mac " + macNo + "/" + TUser, SpaceFront, line);

                                    rs.close();
                                }
                            } catch (SQLException e) {
                                System.err.println(e.getMessage());
                            } finally {
                                mysql.close();
                            }

                            return PAGE_EXISTS;
                        } else {
                            return NO_SUCH_PAGE;
                        }
                    }
                }, pf);
                try {
                    pj.print();
                } catch (PrinterException e) {
                    System.err.println(e.getMessage());
                }
            } catch (PrinterException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    public void KIC_FORM_6(final String printerName, final String tableNo, final String R_Index, final String R_Plucode, final double QTY, final double Total) {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(() -> {
            System.out.println("KIC_FORM_6 : Start Printing....");
            String t = "";
            MySQLConnect mysql = new MySQLConnect();
            mysql.open();
            try {
                String sql = "select TUser,R_Void,R_Index, R_PluCode, TCode, TCustomer, R_PName, R_Quan,"
                        + "R_Price, b.Macno,R_Date, R_Time,"
                        + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                        + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                        + "from tablefile t,balance b "
                        + "where t.tcode=b.r_table "
                        + "and r_table='" + tableNo + "' "
                        + "and R_PrintOK='Y' "
                        + "and R_KicPrint<>'P' "
                        + "and R_Void<>'V' "
                        + "and R_Index='" + R_Index + "' "
                        + "and R_Kic<>'' "
                        + "order by R_Index";
                try (Statement stmt = mysql.getConnection().createStatement()) {
                    ResultSet rs = stmt.executeQuery(sql);
                    String TUser;
                    while (rs.next()) {
                        String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                        String ETD1 = rs.getString("R_ETD");
                        String macNo = rs.getString("macno");
                        String custCount = rs.getString("TCustomer");
                        int qty = rs.getInt("R_Quan");
                        TUser = getEmpBalance(rs.getString("R_PluCode"), rs.getString("R_Index"), rs.getString("TCode"));
                        //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                        List<String[]> listOpt = new ArrayList<>();
                        try {
                            String sqlOpt = "select * from balance "
                                    + "where r_table='" + tableNo + "' and r_pluCode='" + rs.getString("R_PluCode") + "' and R_Index='" + R_Index + "'";
                            Statement stmt1 = mysql.getConnection().createStatement();
                            ResultSet rsOpt = stmt1.executeQuery(sqlOpt);
                            while (rsOpt.next()) {
                                String Vo = rsOpt.getString("R_Void");
                                String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                if (Vo.equals("V")) {
                                    if (!RVo.equals("")) {
                                        RVo = "**ยกเลิก " + RVo;
                                    }
                                }
                                String[] OPT = new String[]{
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                    RVo
                                };

                                listOpt.add(OPT);
                            }

                            rsOpt.close();
                            stmt1.close();
                        } catch (SQLException e) {
                            System.err.println(e.getMessage());
                        }
                        //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                        String tableHead = "โต๊ะ " + rs.getString("TCode");
                        t += "colspan=3 align=left><font face=Angsana New size=5>" + (tableHead) + " C " + custCount + TAB + "T:" + tableHead + "_";
                        //print ETD
                        if (ETD1.equals("E")) {
                            t += "colspan=3 align=center><font face=Angsana New size=5>" + "นั่งในร้าน" + "_";
                        }
                        if (ETD1.equals("T")) {
                            t += "colspan=3 align=center><font face=Angsana New size=5>" + "ห่อกลับ" + "_";
                        }
                        if (ETD1.equals("D")) {
                            t += "colspan=3 align=center><font face=Angsana New size=5>" + "ส่ง" + "_";
                        }
                        t += ("colspan=3 align=centere><font face=Angsana New size=5>" + "-----------------------------------------" + "_");
                        t += "colspan=3 align=left><font face=Angsana New size=4>" + productName + "_";
                        //********* พิมพ์ข้อความพิเศษ *************
                        for (int x = 0; x < listOpt.size(); x++) {
                            String[] OPT = (String[]) listOpt.get(x);
                            for (String OPT1 : OPT) {
                                if (OPT1 != null) {
                                    if (!OPT1.trim().equals("")) {
                                        t += "colspan=4 align=left><font face=Angsana New size=4>" + OPT1 + "_";
                                    }
                                }
                            }
                        }
                        //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************
                        t += "colspan=3 align=left><font face=Angsana New size=4>" + "จำนวน" + Space + df.format(QTY) + Space + "ราคา" + Space + df.format(Total) + "_";
                        t += ("colspan=3 align=centere><font face=Angsana New size=5>" + "-----------------------------------------" + "_");
                        t += ("colspan=3 align=left><font face=Angsana New size=3>" + simp.format(new Date()) + Space + "_Mac: " + macNo + "/" + TUser + Space + printerName + "_");
                        //add kictran data
                        String R_Que = SeekKicItemNo();
                        int TempQue = Integer.parseInt(R_Que);
                        String R_VOID = rs.getString("R_Void");
                        if (R_VOID == null) {
                            R_VOID = "";
                        }
                        try {
                            if (R_VOID.equals("V")) {
                                String SQLQuery2 = "update kictran "
                                        + "set pvoid = 'V' "
                                        + "where pindex ='" + rs.getString("R_Index") + "' "
                                        + "and ptable='" + rs.getString("R_Table") + "' "
                                        + "and pcode='" + rs.getString("R_PluCode") + "' "
                                        + "and pkic='" + rs.getString("R_Kic") + "' "
                                        + "and pflage='N'";
                                try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                    stmt1.executeUpdate(SQLQuery2);
                                    stmt1.close();
                                }
                            } else {
                                String sqlK = "insert into kictran "
                                        + "(pitemno,pdate,pcode,pqty,pindex,"
                                        + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                        + "values (" + TempQue + ",curdate(),"
                                        + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                        + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                        + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                        + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                        + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                                try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                    stmt1.executeUpdate(sqlK);
                                    stmt1.close();
                                }
                            }
                        } catch (SQLException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                    try {
                        String sqlUPdate = "update balance "
                                + "set R_KicPrint='P' "
                                + "where r_table='" + tableNo + "' "
                                + "and r_index='" + R_Index + "' "
                                + "and r_plucode='" + R_Plucode + "'";
                        try (Statement stmtUpdate = mysql.getConnection().createStatement()) {
                            stmtUpdate.executeUpdate(sqlUPdate);
                            stmtUpdate.close();
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    rs.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            } finally {
                mysql.close();
            }
            System.out.println("Printting Job Kitchen" + printerName);
            t = changeFontSize(t);
            PrintDriver pd = new PrintDriver();
            String[] strs = t.split("_");
            for (String data1 : strs) {
                Value.printerDriverKitChenName = printerName;
                pd.addTextIFont(data1);
            }
            pd.printHTMLKitChen(Value.printerDriverKitChenName);
        });
    }

    public void KIC_FORM_7(final String printerName, final String tableNo) {

        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("KIC_FORM_7 : Start Printing....");
                MySQLConnect mysql = new MySQLConnect();
                //FORM 7 **** 
                //จะ CUT ปริ้นทีละสินค้าตาม Order รายการในบิล และแสดงราคาสินค้าด้วย//
                //โต๊ะ 1           C0
                //**** ทานในร้าน ****
                //..................
                //น้ำลำใย            
                //จำนวน  1 ราคา 45.00
                //__________________
                //T1 28/04/2014 14:15 001/พนักงาน
                //หากสั่งมากกว่า 1 ที่
                //โต๊ะ 1           C0
                //**** ทานในร้าน ****
                //..................
                //น้ำลำใย            
                //จำนวน  2 ราคา 90.00
                //__________________
                //T1 28/04/2014 14:15 001/พนักงาน
                /**
                 * * OPEN CONNECTION **
                 */
                mysql.open();
                try {
                    String sql = "select TUser,R_Void,R_Index, R_PluCode, TCode, TCustomer, R_PName, sum(R_Quan) R_Quan,"
                            + "sum(R_Total) R_Price, b.Macno,R_Date, R_Time,"
                            + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                            + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                            + "from tablefile t,balance b "
                            + "where t.tcode=b.r_table "
                            + "and r_table='" + tableNo + "' "
                            + "and R_PrintOK='Y' "
                            + "and R_KicPrint<>'P' "
                            + "and R_Kic<>'' "
                            + "and R_Kic='" + printerName.replace("kic", "") + "' "
                            + "group by b.R_PluCode,r_etd "
                            + "order by R_ETD,R_Index";
                    try (Statement stmt = mysql.getConnection().createStatement()) {
                        ResultSet rs = stmt.executeQuery(sql);
                        String TUser;
                        while (rs.next()) {
                            if (rs.getString("R_Kic").equals(printerName.replace("kic", ""))) {
                                String t = "";
                                String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                                String ETD = rs.getString("R_ETD");
                                String macNo = rs.getString("macno");
                                String custCount = rs.getString("TCustomer");
                                int qty = rs.getInt("R_Quan");
                                TUser = ThaiUtil.ASCII2Unicode(rs.getString("TUser"));
                                String r_index = rs.getString("R_Index");

                                //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                                List<String[]> listOpt = new ArrayList<>();
                                try {
                                    String sqlOpt = "select * from balance "
                                            + "where r_table='" + tableNo
                                            + "' and r_pluCode='"
                                            + rs.getString("R_PluCode") + "' and r_index='" + r_index + "' ";
                                    Statement stmt1 = mysql.getConnection().createStatement();
                                    ResultSet rsOpt = stmt1.executeQuery(sqlOpt);
                                    while (rsOpt.next()) {
                                        String Vo = rsOpt.getString("R_Void");
                                        String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                        if (Vo.equals("V")) {
                                            if (!RVo.equals("")) {
                                                RVo = "ยกเลิก " + RVo;
                                            }
                                        }
                                        String[] OPT = new String[]{
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                            ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                            RVo
                                        };

                                        listOpt.add(OPT);
                                    }

                                    rsOpt.close();
                                    stmt1.close();
                                } catch (SQLException e) {
                                    System.err.println(e.getMessage());
                                }

                                //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                                t += "align=left><font face=Angsana New size=3>" + printerName + "_";
                                t += "align=left><font face=Angsana New size=5>" + "โต๊ะ " + ThaiUtil.ASCII2Unicode(rs.getString("TCode")) + "</td><td align=right><font face=Angsana New size=5>" + " CC. " + "</td><td align=left><font face=Angsana New size=5>" + custCount + "_";
                                t += "colspan=3 align=center>_";
                                switch (ETD) {
                                    case "E":
                                        ETD = "*** นั่งในร้าน ***";
                                        break;
                                    case "T":
                                        ETD = "*** ห่อกลับ ***";
                                        break;
                                    case "D":
                                        ETD = "*** ส่งถึงบ้าน ***";
                                        break;
                                    case "P":
                                        ETD = "*** ปิ่นโต ***";
                                        break;
                                    case "W":
                                        ETD = "*** ขายส่ง ***";
                                        break;
                                    default:
                                        break;
                                }

                                t += "colspan=3 align=center><font face=Angsana New size=5>" + ETD + "_";
                                t += "colspan=3 align=center>" + "............................................." + "_";
                                String product = productName;
                                t += "colspan=3 align=left><font face=Angsana New size=3>" + (product) + "_";
                                t += "colspan=3 align=left><font face=Angsana New size=3>" + "จำนวน" + Space + inf.format(qty) + TAB + TAB + "ราคา" + Space + inf.format(rs.getDouble("R_Price")) + "_";

                                //********* พิมพ์ข้อความพิเศษ *************
                                for (int x = 0; x < listOpt.size(); x++) {
                                    String[] OPT = (String[]) listOpt.get(x);
                                    for (String OPT1 : OPT) {
                                        if (OPT1 != null) {
                                            if (!OPT1.trim().equals("")) {
                                                t += "colspan=3 align=left><font face=Angsana New size=3>" + TAB + "*** " + OPT1 + "_";
                                            }
                                        }
                                    }
                                }
                                //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************
                                t += "colspan=3 align=center><font face=Angsana New size=3>" + "-----------------------------------------" + "_";
                                t += "colspan=3 align=left><font face=Angsana New size=2>" + "โต๊ะ " + Space + ThaiUtil.ASCII2Unicode(rs.getString("TCode")) + TAB + simp.format(new Date()) + "_";
                                t += "colspan=3 align=left><font face=Angsana New size=2>" + "Mac " + macNo + "/" + TAB + TUser + "_";
                                t += "<svg id=\"barcode\"></svg>_";
                                t += "colspan=3 align=left><font face=Code 128 size=2>" + "A10/1234" + "_";
                                PrintDriver pd = new PrintDriver();
                                String[] strs = t.split("_");
                                for (String data1 : strs) {
                                    Value.printerDriverKitChenName = printerName;
                                    pd.addTextIFont(data1);
                                }

                                pd.printHTMLKitChen(Value.printerDriverKitChenName);
                                //add kictran data
                                String R_Que = SeekKicItemNo();
                                int TempQue = Integer.parseInt(R_Que);
                                String R_VOID = rs.getString("R_Void");
                                if (R_VOID == null) {
                                    R_VOID = "";
                                }
                                try {
                                    if (R_VOID.equals("V")) {
                                        String SQLQuery2 = "update kictran "
                                                + "set pvoid = 'V' "
                                                + "where pindex ='" + rs.getString("R_Index") + "' "
                                                + "and ptable='" + rs.getString("R_Table") + "' "
                                                + "and pcode='" + rs.getString("R_PluCode") + "' "
                                                + "and pkic='" + rs.getString("R_Kic") + "' "
                                                + "and pflage='N'";
                                        try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                            stmt1.executeUpdate(SQLQuery2);
                                            stmt1.close();
                                        }
                                    } else {
                                        String sqlK = "insert into kictran "
                                                + "(pitemno,pdate,pcode,pqty,pindex,"
                                                + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                                + "values (" + TempQue + ",curdate(),"
                                                + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                                + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                                + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                                + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                                + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                                        try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                            stmt1.executeUpdate(sqlK);
                                            stmt1.close();
                                        }
                                    }
                                } catch (SQLException e) {
                                    System.err.println(e.getMessage());
                                }
                            }
                            String sqlUpdateBalance = "update balance set R_KicPrint='P' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kic='" + printerName.replace("kic", "") + "' "
                                    + "and R_KicPrint<>'P' "
                                    + "and r_pause='P';";
                            mysql.getConnection().createStatement().executeUpdate(sqlUpdateBalance);
                        }

                        rs.close();
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                } finally {
                    mysql.close();
                }

            }
        });
    }

    public void KIC_FORM_8Qrcode(String printerName, final String tableNo, final String r_etd) {
        System.out.println("KIC_FORM_8 : Start Printing....");
        MySQLConnect mysql = new MySQLConnect();
        //FORM 7 **** 
        //จะ CUT ปริ้นทีละสินค้าตาม Order รายการในบิล และแสดงราคาสินค้าด้วย//
        //โต๊ะ 1           C0
        //**** ทานในร้าน ****
        //..................
        //น้ำลำใย            
        //จำนวน  1 ราคา 45.00
        //__________________
        //T1 28/04/2014 14:15 001/พนักงาน
        //หากสั่งมากกว่า 1 ที่
        //โต๊ะ 1           C0
        //**** ทานในร้าน ****
        //..................
        //น้ำลำใย            
        //จำนวน  2 ราคา 90.00
        //__________________
        //T1 28/04/2014 14:15 001/พนักงาน
        /**
         * * OPEN CONNECTION **
         */
        try {
            mysql.open();

            String sql = "select "
                    + "TOnact,TUser,R_Void,R_Index, R_PluCode, TCode, TCustomer, R_PName, R_Quan R_Quan,"
                    + "R_Total R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                    + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from tablefile t,balance b "
                    + "where t.tcode=b.r_table "
                    + "and r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'' "
                    + "and R_Kic='" + printerName.replace("kic", "") + "'"
                    + "and TOnact='N' "
                    + "and r_etd='" + r_etd + "' "
                    + "order by R_Index";
            Statement stmt;
            stmt = mysql.getConnection().createStatement();

            String allIndex = "";
            int countRound = 0;
            String tempPlucode;

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                countRound++;
                String TOnact = rs.getString("TOnact");
                String Macno = rs.getString("macno");
                String r_plucode = rs.getString("R_PluCode");
                String r_index = rs.getString("R_Index");
                tempPlucode = r_plucode;
                allIndex += rs.getString("R_Index");
                if (TOnact.equals("N")) {
                    PrintKicFormReport print = new PrintKicFormReport();
                    print.PrintKicForm8_Report(tableNo, printerName, Macno, r_etd, r_index);
                }
                //update ว่า พิมพ์แล้ว
                try {
                    String sqlUpdateBalance;
                    if (countRound > 1 && !tempPlucode.equals(r_plucode)) {
                        for (String data : allIndex.split(",")) {
                            sqlUpdateBalance = "update balance set R_KicPrint='P' "
                                    + "where r_table='" + tableNo + "' "
                                    + "and r_kic='" + printerName.replace("kic", "") + "' "
                                    + "and R_KicPrint<>'P' "
                                    + "and r_pause='P' "
                                    + "and r_plucode='" + r_plucode + " "
                                    + "and r_etd='" + r_etd + "' "
                                    + "and r_index='" + r_index + "' ;";
                            mysql.getConnection().createStatement().executeUpdate(sqlUpdateBalance);
                        }
                    } else {
                        sqlUpdateBalance = "update balance set R_KicPrint='P' "
                                + "where r_table='" + tableNo + "' "
                                + "and r_kic='" + printerName.replace("kic", "") + "' "
                                + "and R_KicPrint<>'P' "
                                + "and r_pause='P' "
                                + "and r_plucode='" + r_plucode + "' "
                                + "and r_etd='" + r_etd + "' "
                                + "and r_index='" + r_index + "' ;";
                        mysql.getConnection().createStatement().executeUpdate(sqlUpdateBalance);
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }

            }
            rs.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

    }

    public void KIC_FORM_SaveOrder(final String sql1, final String printerName, final String tableNo, final int R_Kic) {
        String textToPrint = "";

        try {
            mysql.open();

            String sql = "select TUser, R_Void,R_Index, R_PluCode,TCode, TCustomer, R_PName,sum(R_Quan) R_Quan,"
                    + "R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                    + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from tablefile t,balance b "
                    + "where t.tcode=b.r_table "
                    + "and r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "group by R_PluCode, R_ETD "
                    + "order by R_ETD, R_Index";

            String ETD;
            String macNo = "";
            String TUser = "";
            boolean printHeader = false;
            String tempHeader = "";
            textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + "SAVE ORDER" + "_";
            textToPrint += "colspan=2 align=left><font face=Angsana New size=5>" + ("โต๊ะ : " + ThaiUtil.ASCII2Unicode(tableNo)) + "_";

            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ETD = rs.getString("R_ETD");
                    String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                    if (tempHeader.equals("")) {
                        tempHeader = ETD;
                    }
                    if (!tempHeader.equals(ETD)) {
                        tempHeader = ETD;
                        printHeader = false;
                    }
                    macNo = rs.getString("macno");
                    int qty = rs.getInt("R_Quan");
                    TUser = ThaiUtil.ASCII2Unicode(rs.getString("R_Emp"));

                    //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                    List<String[]> listOpt = new ArrayList<>();
                    try {
                        String sqlOpt = "select * from balance "
                                + "where r_table='" + ThaiUtil.ASCII2Unicode(tableNo) + "' "
                                + "and r_pluCode='" + rs.getString("R_PluCode") + "' "
                                + "and r_index='" + rs.getString("R_Index") + "' "
                                + "and R_KicPrint<>'P'";
                        try (Statement stmt1 = mysql.getConnection().createStatement(); ResultSet rsOpt = stmt1.executeQuery(sqlOpt)) {
                            while (rsOpt.next()) {
                                String Vo = rsOpt.getString("R_Void");
                                String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                if (Vo.equals("V")) {
                                    if (!RVo.equals("")) {
                                        RVo = "ยกเลิก " + RVo;
                                    }
                                }
                                String[] OPT = new String[]{
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                    RVo
                                };
                                listOpt.add(OPT);
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
//                                    *********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                    if (!printHeader) {
                        //print ETD
                        switch (ETD) {
                            case "E":
                                ETD = "*** ทานในร้าน ***";
                                break;
                            case "T":
                                ETD = "*** ห่อกลับ ***";
                                break;
                            case "D":
                                ETD = "*** ส่งถึงบ้าน ***";
                                break;
                            case "P":
                                ETD = "*** ปิ่นโต ***";
                                break;
                            case "W":
                                ETD = "*** ขายส่ง ***";
                                break;
                            default:
                                break;
                        }
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=5>" + ETD + "_";
                        printHeader = true;
                        textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                    }

                    String product = (productName);
                    String R_Index = rs.getString("R_Index");
                    textToPrint += keepTextShow(R_Index, qty, product);

//********* พิมพ์ข้อความพิเศษ *************
                    for (int x = 0; x < listOpt.size(); x++) {
                        String[] OPT = (String[]) listOpt.get(x);
                        for (String OPT1 : OPT) {
                            if (OPT1 != null) {
                                if (!OPT1.trim().equals("")) {
                                    textToPrint += "colspan=3 align=left><font face=Angsana New size=3>" + Space + ("*** " + OPT1) + "_";
                                }
                            }
                        }
                    }
//********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************

//add kictran data
                    String R_Que = SeekKicItemNo();
                    int TempQue = Integer.parseInt(R_Que);
                    String R_VOID = rs.getString("R_Void");
                    if (R_VOID == null) {
                        R_VOID = "";
                    }
                    try {
                        if (R_VOID.equals("V")) {
                            String SQLQuery2 = "update kictran "
                                    + "set pvoid = 'V' "
                                    + "where pindex ='" + rs.getString("R_Index") + "' "
                                    + "and ptable='" + rs.getString("R_Table") + "' "
                                    + "and pcode='" + rs.getString("R_PluCode") + "' "
                                    + "and pkic='" + rs.getString("R_Kic") + "' "
                                    + "and pflage='N'";
                            try (Statement stmt2 = mysql.getConnection().createStatement()) {
                                stmt2.executeUpdate(SQLQuery2);
                                stmt2.close();
                            }
                        } else {
                            String sqlK = "insert into kictran "
                                    + "(pitemno,pdate,pcode,pqty,pindex,"
                                    + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                    + "values (" + TempQue + ",curdate(),"
                                    + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                    + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                    + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                    + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                    + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                            try (Statement stmt2 = mysql.getConnection().createStatement()) {
                                stmt2.executeUpdate(sqlK);
                                stmt2.close();
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
//END TEMP UPDATE
                }
                textToPrint += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                textToPrint += "colspan=3 align=left><font face=Angsana New size=2>" + "Terminal : " + Space + macNo + (" Name :" + TUser) + "_";
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        PrintDriver printDriver = new PrintDriver();
        String[] strs = textToPrint.split("_");
        for (String data1 : strs) {
            Value.printerDriverKitChenName = printerName;
            printDriver.addTextIFont(data1);
        }

        printDriver.printHTMLKitChen(Value.printerDriverKitChenName);

    }

    public void ItemVoidPrint(String printerName, final String tableNo, final String[] PCode, String ETD) {
        String t = "";

        try {
            mysql.open();

            String sql = "select TUser,R_Void,R_PluCode,R_Index,TCode, TCustomer, R_PName,R_Quan R_Quan,"
                    + "R_Price, b.Macno,R_Date, R_Time,"
                    + "R_Opt1,R_Opt2,R_Opt3,R_Opt4,R_Opt5,R_Opt6,"
                    + "R_Opt7,R_Opt8,R_Opt9,R_ETD,b.cashier,R_EMP,R_Table,R_ETD,R_Kic "
                    + "from tablefile t,balance b "
                    + "where t.tcode=b.r_table "
                    + "and r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'' "
                    + "and R_PluCode='" + PCode[0] + "' "
                    + "and R_ETD='" + ETD + "' "
                    + "and R_VOID='V' "
                    + "group by R_PluCode "
                    + "order by R_Index";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    printerName = rs.getString("R_Kic");
                    String productName = ThaiUtil.ASCII2Unicode(rs.getString("R_PName"));
                    String macNo = rs.getString("macno");
                    String custCount = rs.getString("TCustomer");
                    double qty = rs.getDouble("R_Quan");
                    String TUser = ThaiUtil.ASCII2Unicode(rs.getString("R_Emp"));
                    String r_index = (rs.getString("R_Index"));

                    //*********** เพิ่มมารองรับการพิมพ์ข้อความพิเศษ ***********
                    List<String[]> listOpt = new ArrayList<>();
                    try {
                        String sqlOpt = "select * from balance "
                                + "where r_table='" + tableNo + "' "
                                + "and r_pluCode='" + rs.getString("R_PluCode") + "' "
                                + "and r_index='" + r_index + "'"
                                + "and r_void='V' ";
                        try (Statement stmt2 = mysql.getConnection().createStatement(); ResultSet rsOpt = stmt2.executeQuery(sqlOpt)) {
                            while (rsOpt.next()) {
                                String Vo = rsOpt.getString("R_Void");
                                String RVo = ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt9"));
                                if (Vo.equals("V")) {
                                    if (!RVo.equals("")) {
                                        RVo = "ยกเลิก " + RVo;
                                    }
                                }
                                String[] OPT = new String[]{
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt1")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt2")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt3")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt4")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt5")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt6")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt7")),
                                    ThaiUtil.ASCII2Unicode(rsOpt.getString("r_opt8")),
                                    RVo
                                };

                                listOpt.add(OPT);
                            }

                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }

                    //*********** สิ้นสุดการตรวจสอบข้อความพิเศษ ***********
                    t += "align=left><font face=Angsana New size=5>" + "โต๊ะ " + ThaiUtil.ASCII2Unicode(rs.getString("TCode")) + "</td><td align=right><font face=Angsana New size=5>" + " CC. " + "</td><td align=left><font face=Angsana New size=5>" + custCount + "_";
                    t += "colspan=3 align=center>_";
                    switch (ETD) {
                        case "E":
                            ETD = "*** ทานในร้าน ***";
                            break;
                        case "T":
                            ETD = "*** ห่อกลับ ***";
                            break;
                        case "D":
                            ETD = "*** ส่งถึงบ้าน ***";
                            break;
                        case "P":
                            ETD = "*** ปิ่นโต ***";
                            break;
                        case "W":
                            ETD = "*** ขายส่ง ***";
                            break;
                        default:
                            break;
                    }

                    t += "colspan=3 align=center><font face=Angsana New size=5>" + ETD + "_";
                    t += "colspan=3 align=center>_";
                    String product = productName;
                    if (product.length() > 16) {
                        String productSubF = product.substring(0, 16);
                        String productSubR = product.substring(16);
                        product = productSubF + "\n" + productSubR;
                    }
                    t += "colspan=3 align=left><font face=Angsana New size=5>" + (product) + "_";
                    t += "<td colspan=3 align=right><font face=Angsana New size=5>" + "- " + df.format(qty) + "_";

                    //********* พิมพ์ข้อความพิเศษ *************
                    for (int x = 0; x < listOpt.size(); x++) {
                        String[] OPT = (String[]) listOpt.get(x);
                        for (String OPT1 : OPT) {
                            if (OPT1 != null) {
                                if (!OPT1.trim().equals("")) {
                                    t += "colspan=5 align=left><font face=Angsana New size=3>" + Space + ("*** " + OPT1) + "_";
                                }
                            }
                        }
                    }
                    //********* สิ้นสุดการพิมพ์ข้อความพิเศษ *************

                    t += "colspan=3 align=center><font face=Angsana New size=3>" + ("-----------------------------------------") + "_";
                    t += "colspan=3 align=left><font face=Angsana New size=2>" + (simp.format(new Date()) + Space + "Mac" + Space + macNo + "/" + TUser) + "_";

                    //add kictran data
                    String R_Que = SeekKicItemNo();
                    int TempQue = Integer.parseInt(R_Que);
                    String R_VOID = rs.getString("R_Void");
                    if (R_VOID == null) {
                        R_VOID = "";
                    }
                    //หากมีสินค้า VOID
                    try {
                        if (R_VOID.equals("V")) {
                            String SQLQuery2 = "update kictran "
                                    + "set pvoid = 'V' "
                                    + "where pindex ='" + rs.getString("R_Index") + "' "
                                    + "and ptable='" + rs.getString("R_Table") + "' "
                                    + "and pcode='" + rs.getString("R_PluCode") + "' "
                                    + "and pkic='" + rs.getString("R_Kic") + "' "
                                    + "and pflage='N'";
                            try (Statement stmt1 = mysql.getConnection().createStatement()) {
                                stmt1.executeUpdate(SQLQuery2);
                                stmt1.close();
                            }
                        } else {
                            String sqlK = "insert into kictran "
                                    + "(pitemno,pdate,pcode,pqty,pindex,"
                                    + "macno,cashier,emp,ptable,ptimein,pvoid,petd,pkic) "
                                    + "values (" + TempQue + ",curdate(),"
                                    + "'" + rs.getString("R_PluCode") + "'," + rs.getString("R_Quan") + ","
                                    + "'" + rs.getString("R_Index") + "','" + rs.getString("Macno") + "',"
                                    + "'" + rs.getString("Cashier") + "','" + rs.getString("R_Emp") + "',"
                                    + "'" + rs.getString("R_Table") + "',curtime(),'',"
                                    + "'" + rs.getString("R_ETD") + "','" + rs.getString("R_Kic") + "')";
                            try (Statement stmt3 = mysql.getConnection().createStatement()) {
                                stmt3.executeUpdate(sqlK);
                                stmt3.close();
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        PrintDriver pd = new PrintDriver();
        String[] strs = t.split("_");
        for (String data1 : strs) {
            Value.printerDriverKitChenName = "kic" + printerName;
            pd.addTextIFont(data1);
        }

        pd.printHTMLKitChen(Value.printerDriverKitChenName);

    }

    private static void printG(Graphics2D g2, String ETD, int line) {
        final int SpaceFront = 25;
        switch (ETD) {
            case "E":
                g2.drawString("** DINE IN **", SpaceFront, line);
                break;
            case "T":
                g2.drawString("** TAKE AWAY **", SpaceFront, line);
                break;
            case "D":
                g2.drawString("** DELIVERY **", SpaceFront, line);
                break;
            case "P":
                g2.drawString("*** PINTO ***", SpaceFront, line);
                break;
            case "W":
                g2.drawString("*** WHOLE SALE ***", SpaceFront, line);
                break;
            default:
                break;
        }
    }

    private String SeekKicItemNo() {
        DecimalFormat QtyIntFmt = new DecimalFormat("###########0");
        BranchBean branchBean = BranchControl.getData();

        try {
            mysql.open();

            try (Statement stmt = mysql.getConnection().createStatement()) {
                String SQLQuery = "update branch set kicitemno =" + branchBean.getKicItemNo();
                stmt.executeUpdate(SQLQuery);
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        return QtyIntFmt.format(branchBean.getKicItemNo());
    }

    public String changeFontSize(String text) {
        String t = text;
        String sizeHead = ConfigFile.getProperties("kicFontHeadder");
        String sizeBody = ConfigFile.getProperties("kicFontDetail");
        t = t.replace("size=5", sizeHead);
        t = t.replace("size=4", sizeBody);
        return t;
    }

    public String getEmpBalance(String Code, String r_index, String table) {
        String empName = "";
        try {
            mysql.open();

            String sql = "select r_emp from balance "
                    + "where r_table='" + table + "' and r_index='" + r_index + "' "
                    + "and r_plucode='" + Code + "' limit 1;";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    empName = ThaiUtil.ASCII2Unicode(rs.getString("r_emp"));
                }

            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        return empName;
    }

    public String SubString(String str) {
        int size = str.length();
        String text = "";
        if (size <= 20 && size > 0) {
            return str;
        } else if (size > 20 && size <= 40) {
            text = str.substring(0, 20);
            text += "\n" + str.substring(20);
        } else if (size > 40 && size <= 60) {
            text = str.substring(0, 20);
            text += "\n" + str.substring(20, 43);
            text += "\n" + str.substring(43);
        } else if (size > 60 && size <= 80) {
            text = str.substring(0, 20);
            text += "\n" + str.substring(20, 43);
            text += "\n" + str.substring(43, 66);
            text += "\n" + str.substring(66);
        } else if (size > 80 && size <= 100) {
            text = str.substring(0, 20);
            text += "\n" + str.substring(20, 43);
            text += "\n" + str.substring(43, 66);
            text += "\n" + str.substring(66, 89);
            text += "\n" + str.substring(89);
        } else if (size > 100 && size <= 120) {
            text = str.substring(0, 20);
            text += "\n" + str.substring(20, 43);
            text += "\n" + str.substring(43, 66);
            text += "\n" + str.substring(66, 89);
            text += "\n" + str.substring(89, 112);
            text += "\n" + str.substring(112);
        } else if (size > 120) {
            text = str.substring(0, 20);
            text += "\n" + str.substring(20, 43);
            text += "\n" + str.substring(43, 66);
            text += "\n" + str.substring(66, 89);
            text += "\n" + str.substring(89, 112);
            text += "\n" + str.substring(112, 122);
        }
        return text;

    }
}
