package com.ics.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.ics.model.BalanceBean;
import com.ics.process.DatabaseConnection;
import com.ics.process.MySQLConnect;
import com.ics.constant.PublicVar;
import com.ics.util.ThaiUtil;

public class BalanceControl extends DatabaseConnection {

    private BalanceBean balanceCurrent = new BalanceBean();

    public List<BalanceBean> getAllBalance(String table) {
        List<BalanceBean> beanData = new ArrayList<>();
        beanData.clear();
        MySQLConnect mysql = new MySQLConnect();
        String sql = "";
        try {
            mysql.open(this.getClass());
            sql = "select * from balance "
                    + "where R_Table='" + table + "' "
                    + "order by R_Index";
            try (ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql)) {
                while (rs.next()) {
                    BalanceBean balanceBean = new BalanceBean();
                    balanceBean.setR_Index(rs.getString("R_Index"));
                    balanceBean.setR_Table(rs.getString("R_Table"));
                    balanceBean.setR_Time(rs.getString("R_Time"));
                    balanceBean.setMacno(rs.getString("Macno"));
                    balanceBean.setCashier(rs.getString("Cashier"));
                    balanceBean.setR_Emp(rs.getString("R_Emp"));
                    balanceBean.setR_PluCode(rs.getString("R_PluCode"));
                    balanceBean.setR_PName(ThaiUtil.ASCII2Unicode(rs.getString("R_PName")));
                    balanceBean.setR_Unit(rs.getString("R_Unit"));
                    balanceBean.setR_Group(rs.getString("R_Group"));
                    balanceBean.setR_Status(rs.getString("R_Status"));
                    balanceBean.setR_Normal(rs.getString("R_Normal"));
                    balanceBean.setR_Discount(rs.getString("R_Discount"));
                    balanceBean.setR_Service(rs.getString("R_Service"));
                    balanceBean.setR_Stock(rs.getString("R_Stock"));
                    balanceBean.setR_Set(rs.getString("R_Set"));
                    balanceBean.setR_Vat(rs.getString("R_Vat"));
                    balanceBean.setR_Type(rs.getString("R_Type"));
                    balanceBean.setR_ETD(rs.getString("R_ETD"));
                    balanceBean.setR_Quan(rs.getFloat("R_Quan"));
                    balanceBean.setR_Price(rs.getFloat("R_Price"));
                    balanceBean.setR_Total(rs.getFloat("R_Total"));
                    String R_PrType = rs.getString("R_PrType");
                    if (R_PrType == null) {
                        R_PrType = "";
                    }
                    balanceBean.setR_PrType(R_PrType);

                    balanceBean.setR_PrCode(rs.getString("R_PrCode"));
                    balanceBean.setR_PrDisc(rs.getFloat("R_PrDisc"));
                    balanceBean.setR_PrBath(rs.getFloat("R_PrBath"));
                    balanceBean.setR_PrAmt(rs.getFloat("R_PrAmt"));
                    balanceBean.setR_DiscBath(rs.getFloat("R_DiscBath"));
                    balanceBean.setR_PrCuType(rs.getString("R_PrCuType"));
                    balanceBean.setR_PrCuQuan(rs.getFloat("R_PrCuQuan"));
                    balanceBean.setR_PrCuAmt(rs.getFloat("R_PrCuAmt"));
                    balanceBean.setR_Redule(rs.getFloat("R_Redule"));
                    balanceBean.setR_Kic(rs.getString("R_Kic"));
                    balanceBean.setR_KicPrint(rs.getString("R_KicPrint"));
                    balanceBean.setR_Void(rs.getString("R_Void"));
                    balanceBean.setR_VoidUser(rs.getString("R_VoidUser"));
                    balanceBean.setR_VoidTime(rs.getString("R_VoidTime"));
                    balanceBean.setR_Opt1(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt1")));
                    balanceBean.setR_Opt2(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt2")));
                    balanceBean.setR_Opt3(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt3")));
                    balanceBean.setR_Opt4(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt4")));
                    balanceBean.setR_Opt5(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt5")));
                    balanceBean.setR_Opt6(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt6")));
                    balanceBean.setR_Opt7(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt7")));
                    balanceBean.setR_Opt8(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt8")));
                    balanceBean.setR_Opt9(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt9")));
                    balanceBean.setVoidMSG(ThaiUtil.ASCII2Unicode(rs.getString("VoidMSG")));
                    balanceBean.setR_PrCuCode(rs.getString("R_PrCuCode"));
                    balanceBean.setR_Serve(rs.getString("R_Serve"));
                    balanceBean.setR_PrintOK(rs.getString("R_PrintOK"));
                    balanceBean.setR_KicOK(rs.getString("R_KicOK"));
                    balanceBean.setStkCode(rs.getString("StkCode"));
                    balanceBean.setPosStk(rs.getString("PosStk"));
                    balanceBean.setR_PrChkType(rs.getString("R_PrChkType"));
                    balanceBean.setR_PrQuan(rs.getFloat("R_PrQuan"));
                    balanceBean.setR_PrSubType(rs.getString("R_PrSubType"));
                    balanceBean.setR_PrSubCode(rs.getString("R_PrSubCode"));
                    balanceBean.setR_PrSubQuan(rs.getFloat("R_PrSubQuan"));
                    balanceBean.setR_PrSubDisc(rs.getFloat("R_PrSubDisc"));
                    balanceBean.setR_PrSubBath(rs.getFloat("R_PrSubBath"));
                    balanceBean.setR_PrSubAmt(rs.getFloat("R_PrSubAmt"));
                    balanceBean.setR_PrSubAdj(rs.getFloat("R_PrSubAdj"));
                    balanceBean.setR_PrCuDisc(rs.getFloat("R_PrCuDisc"));
                    balanceBean.setR_PrCuBath(rs.getFloat("R_PrCuBath"));
                    balanceBean.setR_PrCuAdj(rs.getFloat("R_PrCuAdj"));
                    balanceBean.setR_QuanCanDisc(rs.getFloat("R_QuanCanDisc"));
                    balanceBean.setR_Order(rs.getString("R_Order"));
                    balanceBean.setR_PItemNo(rs.getInt("R_PItemNo"));
                    balanceBean.setR_PKicQue(rs.getInt("R_PKicQue"));
                    balanceBean.setR_MemSum(rs.getString("R_MemSum"));
                    balanceBean.setR_MoveItem(rs.getString("R_MoveItem"));
                    balanceBean.setR_MoveFrom(rs.getString("R_MoveFrom"));
                    balanceBean.setR_MoveUser(rs.getString("R_MoveUser"));
                    balanceBean.setR_MoveFlag(rs.getString("R_MoveFlag"));
                    balanceBean.setR_MovePrint(rs.getString("R_MovePrint"));
                    balanceBean.setR_Pause(rs.getString("R_Pause"));
                    String r_linkIndex = rs.getString("R_LinkIndex");
                    if (r_linkIndex == null || r_linkIndex.equals("null")) {
                        r_linkIndex = "";
                    }
                    balanceBean.setR_LinkIndex(r_linkIndex);
                    try {
                        balanceBean.setR_Date(rs.getDate("R_Date"));
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    beanData.add(balanceBean);
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return beanData;
    }

    public static String GetDiscount(String table) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        double discount = 0.00;
        MySQLConnect mysql = new MySQLConnect();
        try {
            mysql.open(BalanceControl.class);
            String sql = "select sum(R_PrAmt) + sum(R_Discbath) discount from balance where r_table='" + table + "' and r_void<>'V'";
            try (ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql)) {
                if (rs.next()) {
                    discount = rs.getDouble("discount");
                } else {
                    return 0.00 + "";
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(BalanceControl.class);
        }

        return df.format(discount);
    }

    public List<BalanceBean> getAllBalancePromotion(String table) {
        List<BalanceBean> beanData = new ArrayList<>();
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(this.getClass());
        try {
            String sql = "select * from balance "
                    + "where R_Table='" + table + "' "
                    + "and R_Discount='Y' "
                    + "and R_Void <> 'V' "
                    + "group by R_PRType "
                    + "order by R_PluCode, R_Index";
            ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) {
                BalanceBean balanceBean = new BalanceBean();
                balanceBean.setR_Index(rs.getString("R_Index"));
                balanceBean.setR_Table(rs.getString("R_Table"));
                balanceBean.setR_Time(rs.getString("R_Time"));
                balanceBean.setMacno(rs.getString("Macno"));
                balanceBean.setCashier(rs.getString("Cashier"));
                balanceBean.setR_Emp(rs.getString("R_Emp"));
                balanceBean.setR_PluCode(rs.getString("R_PluCode"));
                balanceBean.setR_PName(ThaiUtil.ASCII2Unicode(rs.getString("R_PName")));
                balanceBean.setR_Unit(rs.getString("R_Unit"));
                balanceBean.setR_Group(rs.getString("R_Group"));
                balanceBean.setR_Status(rs.getString("R_Status"));
                balanceBean.setR_Normal(rs.getString("R_Normal"));
                balanceBean.setR_Discount(rs.getString("R_Discount"));
                balanceBean.setR_Service(rs.getString("R_Service"));
                balanceBean.setR_Stock(rs.getString("R_Stock"));
                balanceBean.setR_Set(rs.getString("R_Set"));
                balanceBean.setR_Vat(rs.getString("R_Vat"));
                balanceBean.setR_Type(rs.getString("R_Type"));
                balanceBean.setR_ETD(rs.getString("R_ETD"));
                balanceBean.setR_Quan(rs.getFloat("R_Quan"));
                balanceBean.setR_Price(rs.getFloat("R_Price"));
                balanceBean.setR_Total(rs.getFloat("R_Total"));
                String R_PrType = rs.getString("R_PrType");
                if (R_PrType == null) {
                    R_PrType = "";
                }
                balanceBean.setR_PrType(R_PrType);

                balanceBean.setR_PrCode(rs.getString("R_PrCode"));
                balanceBean.setR_PrDisc(rs.getFloat("R_PrDisc"));
                balanceBean.setR_PrBath(rs.getFloat("R_PrBath"));
                balanceBean.setR_PrAmt(rs.getFloat("R_PrAmt"));
                balanceBean.setR_DiscBath(rs.getFloat("R_DiscBath"));
                balanceBean.setR_PrCuType(rs.getString("R_PrCuType"));
                balanceBean.setR_PrCuQuan(rs.getFloat("R_PrCuQuan"));
                balanceBean.setR_PrCuAmt(rs.getFloat("R_PrCuAmt"));
                balanceBean.setR_Redule(rs.getFloat("R_Redule"));
                balanceBean.setR_Kic(rs.getString("R_Kic"));
                balanceBean.setR_KicPrint(rs.getString("R_KicPrint"));
                balanceBean.setR_Void(rs.getString("R_Void"));
                balanceBean.setR_VoidUser(rs.getString("R_VoidUser"));
                balanceBean.setR_VoidTime(rs.getString("R_VoidTime"));
                balanceBean.setR_Opt1(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt1")));
                balanceBean.setR_Opt2(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt2")));
                balanceBean.setR_Opt3(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt3")));
                balanceBean.setR_Opt4(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt4")));
                balanceBean.setR_Opt5(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt5")));
                balanceBean.setR_Opt6(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt6")));
                balanceBean.setR_Opt7(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt7")));
                balanceBean.setR_Opt8(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt8")));
                balanceBean.setR_Opt9(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt9")));
                balanceBean.setVoidMSG(ThaiUtil.ASCII2Unicode(rs.getString("VoidMSG")));
                balanceBean.setR_PrCuCode(rs.getString("R_PrCuCode"));
                balanceBean.setR_Serve(rs.getString("R_Serve"));
                balanceBean.setR_PrintOK(rs.getString("R_PrintOK"));
                balanceBean.setR_KicOK(rs.getString("R_KicOK"));
                balanceBean.setStkCode(rs.getString("StkCode"));
                balanceBean.setPosStk(rs.getString("PosStk"));
                balanceBean.setR_PrChkType(rs.getString("R_PrChkType"));
                balanceBean.setR_PrQuan(rs.getFloat("R_PrQuan"));
                balanceBean.setR_PrSubType(rs.getString("R_PrSubType"));
                balanceBean.setR_PrSubCode(rs.getString("R_PrSubCode"));
                balanceBean.setR_PrSubQuan(rs.getFloat("R_PrSubQuan"));
                balanceBean.setR_PrSubDisc(rs.getFloat("R_PrSubDisc"));
                balanceBean.setR_PrSubBath(rs.getFloat("R_PrSubBath"));
                balanceBean.setR_PrSubAmt(rs.getFloat("R_PrSubAmt"));
                balanceBean.setR_PrSubAdj(rs.getFloat("R_PrSubAdj"));
                balanceBean.setR_PrCuDisc(rs.getFloat("R_PrCuDisc"));
                balanceBean.setR_PrCuBath(rs.getFloat("R_PrCuBath"));
                balanceBean.setR_PrCuAdj(rs.getFloat("R_PrCuAdj"));
                balanceBean.setR_QuanCanDisc(rs.getFloat("R_QuanCanDisc"));
                balanceBean.setR_Order(rs.getString("R_Order"));
                balanceBean.setR_PItemNo(rs.getInt("R_PItemNo"));
                balanceBean.setR_PKicQue(rs.getInt("R_PKicQue"));
                balanceBean.setR_MemSum(rs.getString("R_MemSum"));
                balanceBean.setR_MoveItem(rs.getString("R_MoveItem"));
                balanceBean.setR_MoveFrom(rs.getString("R_MoveFrom"));
                balanceBean.setR_MoveUser(rs.getString("R_MoveUser"));
                balanceBean.setR_MoveFlag(rs.getString("R_MoveFlag"));
                balanceBean.setR_MovePrint(rs.getString("R_MovePrint"));
                balanceBean.setR_Pause(rs.getString("R_Pause"));

                try {
                    balanceBean.setR_Date(rs.getDate("R_Date"));
                } catch (SQLException e) {
                }

                //balanceBean.setR_CashCard(""+rs.getFloat("R_CashCard"));
                beanData.add(balanceBean);
            }

            rs.close();
//            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return beanData;
    }

    public List<BalanceBean> getAllBalanceNoVoidSum(String table) {
        List<BalanceBean> beanData = new ArrayList<>();
        beanData.clear();
        MySQLConnect mysql = new MySQLConnect();
        String sql = "";
        try {
            mysql.open(this.getClass());

            for (int i = 0; i <= 2; i++) {
                if (i == 0) {
                    sql = "select "
                            + "sum(b.r_quan) sumr_quan,"
                            + "sum(b.r_total) sumr_total,"
                            + "sum(b.r_pramt) sumr_pramt,"
                            + "sum(b.R_PrCuAmt) sumr_prcuamt,"
                            + "r_etd,"
                            + "b.* "
                            + "from balance b "
                            + "where r_table='" + table + "' "
                            + "and r_void<>'V' "
                            + "and r_plucode<>'8899' "
                            + "and r_normal='N' "
                            + "group by r_plucode,r_pname,r_etd "
                            + "order by r_etd,r_normal,r_type,r_index,"
                            + "r_time";
                }
                if (i == 1) {
                    sql = "select "
                            + "sum(b.r_quan) sumr_quan,"
                            + "sum(b.r_total) sumr_total,"
                            + "sum(b.r_pramt) sumr_pramt,"
                            + "sum(b.R_PrCuAmt) sumr_prcuamt,"
                            + "r_etd,"
                            + "b.* "
                            + "from balance b "
                            + "where r_table='" + table + "' "
                            + "and r_void<>'V' "
                            + "and r_plucode<>'8899' "
                            + "and r_normal='C' "
                            + "group by r_plucode,r_pname,r_etd "
                            + "order by r_etd,r_normal,r_type,r_index,"
                            + "r_time";
                }
                if (i == 2) {
                    sql = "select "
                            + "sum(b.r_quan) sumr_quan,"
                            + "sum(b.r_total) sumr_total"
                            + ",sum(b.r_pramt) sumr_pramt,"
                            + "sum(b.R_PrCuAmt) sumr_prcuamt"
                            + ",r_etd,"
                            + "b.* "
                            + "from balance b "
                            + "where r_table='" + table + "' "
                            + "and r_void<>'V' "
                            + "and r_plucode<>'8899' "
                            + "and r_normal='S' "
                            + "group by r_plucode,r_pname,r_etd "
                            + "order by r_etd,r_normal,r_type,r_index,"
                            + "r_time";
                }
                ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql);
                double totalVat = 0;
                while (rs.next()) {
                    BalanceBean balanceBean = new BalanceBean();
                    balanceBean.setR_Index(rs.getString("R_Index"));
                    balanceBean.setR_Table(rs.getString("R_Table"));
                    balanceBean.setR_Time(rs.getString("R_Time"));
                    balanceBean.setMacno(rs.getString("Macno"));
                    balanceBean.setCashier(rs.getString("Cashier"));
                    balanceBean.setR_Emp(rs.getString("R_Emp"));
                    balanceBean.setR_PluCode(rs.getString("R_PluCode"));
                    if (PublicVar.languagePrint.equals("EN")) {
                        balanceBean.setR_PName(ThaiUtil.ASCII2Unicode(rs.getString("R_PEName")));
                    } else {
                        balanceBean.setR_PName(ThaiUtil.ASCII2Unicode(rs.getString("R_PName")));
                    }
                    balanceBean.setR_Unit(rs.getString("R_Unit"));
                    balanceBean.setR_Group(rs.getString("R_Group"));
                    balanceBean.setR_Status(rs.getString("R_Status"));
                    balanceBean.setR_Normal(rs.getString("R_Normal"));
                    balanceBean.setR_Discount(rs.getString("R_Discount"));
                    balanceBean.setR_Service(rs.getString("R_Service"));
                    balanceBean.setR_Stock(rs.getString("R_Stock"));
                    balanceBean.setR_Set(rs.getString("R_Set"));
                    balanceBean.setR_Vat(rs.getString("R_Vat"));
                    balanceBean.setR_Total(rs.getFloat("sumr_total"));
                    if (balanceBean.getR_Vat().equals("V")) {
                        totalVat += balanceBean.getR_Total();
                        balanceBean.setR_totalVAT(totalVat);
                    }
                    balanceBean.setR_Type(rs.getString("R_Type"));
                    balanceBean.setR_ETD(rs.getString("R_ETD"));
                    balanceBean.setR_Quan(rs.getFloat("sumr_quan"));
                    balanceBean.setR_Price(rs.getFloat("R_Price"));

                    String R_PrType = rs.getString("R_PrType");
                    if (R_PrType == null) {
                        R_PrType = "";
                    }
                    balanceBean.setR_PrType(R_PrType);
                    balanceBean.setR_PrCode(rs.getString("R_PrCode"));
                    balanceBean.setR_PrDisc(rs.getFloat("R_PrDisc"));
                    balanceBean.setR_PrBath(rs.getFloat("R_PrBath"));
                    balanceBean.setR_PrAmt(rs.getFloat("sumr_pramt"));
                    balanceBean.setR_DiscBath(rs.getFloat("R_DiscBath"));
                    balanceBean.setR_PrCuType(rs.getString("R_PrCuType"));
                    balanceBean.setR_PrCuQuan(rs.getFloat("R_PrCuQuan"));
                    balanceBean.setR_PrCuAmt(rs.getFloat("sumr_prcuamt"));
                    balanceBean.setR_Redule(rs.getFloat("R_Redule"));
                    balanceBean.setR_Kic(rs.getString("R_Kic"));
                    balanceBean.setR_KicPrint(rs.getString("R_KicPrint"));
                    balanceBean.setR_Void(rs.getString("R_Void"));
                    balanceBean.setR_VoidUser(rs.getString("R_VoidUser"));
                    balanceBean.setR_VoidTime(rs.getString("R_VoidTime"));
                    balanceBean.setR_Opt1(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt1")));
                    balanceBean.setR_Opt2(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt2")));
                    balanceBean.setR_Opt3(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt3")));
                    balanceBean.setR_Opt4(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt4")));
                    balanceBean.setR_Opt5(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt5")));
                    balanceBean.setR_Opt6(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt6")));
                    balanceBean.setR_Opt7(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt7")));
                    balanceBean.setR_Opt8(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt8")));
                    balanceBean.setR_Opt9(ThaiUtil.ASCII2Unicode(rs.getString("R_Opt9")));
                    balanceBean.setVoidMSG(ThaiUtil.ASCII2Unicode(rs.getString("VoidMSG")));
                    balanceBean.setR_PrCuCode(rs.getString("R_PrCuCode"));
                    balanceBean.setR_Serve(rs.getString("R_Serve"));
                    balanceBean.setR_PrintOK(rs.getString("R_PrintOK"));
                    balanceBean.setR_KicOK(rs.getString("R_KicOK"));
                    balanceBean.setStkCode(rs.getString("StkCode"));
                    balanceBean.setPosStk(rs.getString("PosStk"));
                    balanceBean.setR_PrChkType(rs.getString("R_PrChkType"));
                    balanceBean.setR_PrQuan(rs.getFloat("R_PrQuan"));
                    balanceBean.setR_PrSubType(rs.getString("R_PrSubType"));
                    balanceBean.setR_PrSubCode(rs.getString("R_PrSubCode"));
                    balanceBean.setR_PrSubQuan(rs.getFloat("R_PrSubQuan"));
                    balanceBean.setR_PrSubDisc(rs.getFloat("R_PrSubDisc"));
                    balanceBean.setR_PrSubBath(rs.getFloat("R_PrSubBath"));
                    balanceBean.setR_PrSubAmt(rs.getFloat("R_PrSubAmt"));
                    balanceBean.setR_PrSubAdj(rs.getFloat("R_PrSubAdj"));
                    balanceBean.setR_PrCuDisc(rs.getFloat("R_PrCuDisc"));
                    balanceBean.setR_PrCuBath(rs.getFloat("R_PrCuBath"));
                    balanceBean.setR_PrCuAdj(rs.getFloat("R_PrCuAdj"));
                    balanceBean.setR_QuanCanDisc(rs.getFloat("R_QuanCanDisc"));
                    balanceBean.setR_Order(rs.getString("R_Order"));
                    balanceBean.setR_PItemNo(rs.getInt("R_PItemNo"));
                    balanceBean.setR_PKicQue(rs.getInt("R_PKicQue"));
                    balanceBean.setR_MemSum(rs.getString("R_MemSum"));
                    balanceBean.setR_MoveItem(rs.getString("R_MoveItem"));
                    balanceBean.setR_MoveFrom(rs.getString("R_MoveFrom"));
                    balanceBean.setR_MoveUser(rs.getString("R_MoveUser"));
                    balanceBean.setR_MoveFlag(rs.getString("R_MoveFlag"));
                    balanceBean.setR_MovePrint(rs.getString("R_MovePrint"));
                    balanceBean.setR_Pause(rs.getString("R_Pause"));
                    balanceBean.setR_LinkIndex(rs.getString("R_LinkIndex"));
                    if (!balanceBean.getR_Opt1().equals("")) {
                        balanceBean.setR_Opt1("    *** " + rs.getString("R_Opt1"));
                    }
                    try {
                        balanceBean.setR_Date(rs.getDate("R_Date"));
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }

                    beanData.add(balanceBean);
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        MySQLConnect mysql2 = new MySQLConnect();
        mysql2.open(BalanceControl.class);
        try {
            String sql1 = "select * from balance "
                    + "where r_table='" + table + "' "
                    + "and r_void<>'V' "
                    + "and r_plucode='8899' "
                    + "order by r_index,"
                    + "r_time";
            ResultSet rs1 = mysql2.getConnection().createStatement().executeQuery(sql1);
            while (rs1.next()) {
                BalanceBean balanceBean = new BalanceBean();
                balanceBean.setR_Index(rs1.getString("R_Index"));
                balanceBean.setR_Table(rs1.getString("R_Table"));
                balanceBean.setR_Time(rs1.getString("R_Time"));
                balanceBean.setMacno(rs1.getString("Macno"));
                balanceBean.setCashier(rs1.getString("Cashier"));
                balanceBean.setR_Emp(rs1.getString("R_Emp"));
                balanceBean.setR_PluCode(rs1.getString("R_PluCode"));
                if (PublicVar.languagePrint.equals("EN")) {
                    balanceBean.setR_PName(ThaiUtil.ASCII2Unicode(rs1.getString("R_PEName")));
                } else {
                    balanceBean.setR_PName(ThaiUtil.ASCII2Unicode(rs1.getString("R_PName")));
                }
                balanceBean.setR_Unit(rs1.getString("R_Unit"));
                balanceBean.setR_Group(rs1.getString("R_Group"));
                balanceBean.setR_Status(rs1.getString("R_Status"));
                balanceBean.setR_Normal(rs1.getString("R_Normal"));
                balanceBean.setR_Discount(rs1.getString("R_Discount"));
                balanceBean.setR_Service(rs1.getString("R_Service"));
                balanceBean.setR_Stock(rs1.getString("R_Stock"));
                balanceBean.setR_Set(rs1.getString("R_Set"));
                balanceBean.setR_Vat(rs1.getString("R_Vat"));
                balanceBean.setR_Type(rs1.getString("R_Type"));
                balanceBean.setR_ETD(rs1.getString("R_ETD"));
                balanceBean.setR_Quan(rs1.getFloat("R_Quan"));
                balanceBean.setR_Price(rs1.getFloat("R_Price"));
                balanceBean.setR_Total(rs1.getFloat("r_total"));
                String R_PrType = rs1.getString("R_PrType");
                if (R_PrType == null) {
                    R_PrType = "";
                }
                balanceBean.setR_PrType(R_PrType);

                balanceBean.setR_PrCode(rs1.getString("R_PrCode"));
                balanceBean.setR_PrDisc(rs1.getFloat("R_PrDisc"));
                balanceBean.setR_PrBath(rs1.getFloat("R_PrBath"));
                balanceBean.setR_PrAmt(rs1.getFloat("R_PrAmt"));
                balanceBean.setR_DiscBath(rs1.getFloat("R_DiscBath"));
                balanceBean.setR_PrCuType(rs1.getString("R_PrCuType"));
                balanceBean.setR_PrCuQuan(rs1.getFloat("R_PrCuQuan"));
                balanceBean.setR_PrCuAmt(rs1.getFloat("R_PrCuAmt"));
                balanceBean.setR_Redule(rs1.getFloat("R_Redule"));
                balanceBean.setR_Kic(rs1.getString("R_Kic"));
                balanceBean.setR_KicPrint(rs1.getString("R_KicPrint"));
                balanceBean.setR_Void(rs1.getString("R_Void"));
                balanceBean.setR_VoidUser(rs1.getString("R_VoidUser"));
                balanceBean.setR_VoidTime(rs1.getString("R_VoidTime"));
                balanceBean.setR_Opt1(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt1")));
                balanceBean.setR_Opt2(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt2")));
                balanceBean.setR_Opt3(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt3")));
                balanceBean.setR_Opt4(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt4")));
                balanceBean.setR_Opt5(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt5")));
                balanceBean.setR_Opt6(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt6")));
                balanceBean.setR_Opt7(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt7")));
                balanceBean.setR_Opt8(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt8")));
                balanceBean.setR_Opt9(ThaiUtil.ASCII2Unicode(rs1.getString("R_Opt9")));
                balanceBean.setVoidMSG(ThaiUtil.ASCII2Unicode(rs1.getString("VoidMSG")));
                balanceBean.setR_PrCuCode(rs1.getString("R_PrCuCode"));
                balanceBean.setR_Serve(rs1.getString("R_Serve"));
                balanceBean.setR_PrintOK(rs1.getString("R_PrintOK"));
                balanceBean.setR_KicOK(rs1.getString("R_KicOK"));
                balanceBean.setStkCode(rs1.getString("StkCode"));
                balanceBean.setPosStk(rs1.getString("PosStk"));
                balanceBean.setR_PrChkType(rs1.getString("R_PrChkType"));
                balanceBean.setR_PrQuan(rs1.getFloat("R_PrQuan"));
                balanceBean.setR_PrSubType(rs1.getString("R_PrSubType"));
                balanceBean.setR_PrSubCode(rs1.getString("R_PrSubCode"));
                balanceBean.setR_PrSubQuan(rs1.getFloat("R_PrSubQuan"));
                balanceBean.setR_PrSubDisc(rs1.getFloat("R_PrSubDisc"));
                balanceBean.setR_PrSubBath(rs1.getFloat("R_PrSubBath"));
                balanceBean.setR_PrSubAmt(rs1.getFloat("R_PrSubAmt"));
                balanceBean.setR_PrSubAdj(rs1.getFloat("R_PrSubAdj"));
                balanceBean.setR_PrCuDisc(rs1.getFloat("R_PrCuDisc"));
                balanceBean.setR_PrCuBath(rs1.getFloat("R_PrCuBath"));
                balanceBean.setR_PrCuAdj(rs1.getFloat("R_PrCuAdj"));
                balanceBean.setR_QuanCanDisc(rs1.getFloat("R_QuanCanDisc"));
                balanceBean.setR_Order(rs1.getString("R_Order"));
                balanceBean.setR_PItemNo(rs1.getInt("R_PItemNo"));
                balanceBean.setR_PKicQue(rs1.getInt("R_PKicQue"));
                balanceBean.setR_MemSum(rs1.getString("R_MemSum"));
                balanceBean.setR_MoveItem(rs1.getString("R_MoveItem"));
                balanceBean.setR_MoveFrom(rs1.getString("R_MoveFrom"));
                balanceBean.setR_MoveUser(rs1.getString("R_MoveUser"));
                balanceBean.setR_MoveFlag(rs1.getString("R_MoveFlag"));
                balanceBean.setR_MovePrint(rs1.getString("R_MovePrint"));
                balanceBean.setR_Pause(rs1.getString("R_Pause"));
                balanceBean.setR_LinkIndex(rs1.getString("R_LinkIndex"));
                if (!balanceBean.getR_Opt1().equals("")) {
                    balanceBean.setR_Opt1("    *** " + rs1.getString("R_Opt1"));
                }
                try {
                    balanceBean.setR_Date(rs1.getDate("R_Date"));
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
                beanData.add(balanceBean);
            }
            rs1.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql2.closeConnection(BalanceControl.class);
        }

        return beanData;
    }

}
