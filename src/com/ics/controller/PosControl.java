package com.ics.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.ics.model.CompanyBean;
import com.ics.process.MySQLConnect;
import com.ics.model.POSConfigSetup;
import com.ics.model.POSHWSetup;
import com.ics.model.PosUserBean;
import com.ics.constant.PublicVar;
import com.ics.util.ThaiUtil;
import com.ics.constant.Value;

public class PosControl {

    private static CompanyBean companyBean = null;
    private static POSConfigSetup posConfigSetup = null;
    private static POSHWSetup poshwsetup = null;
    private static PosUserBean posUser = null;

    public static void resetPosHwSetup() {
        poshwsetup = null;
    }

    public static void resetPOSConfigSetup() {
        posConfigSetup = null;
    }

    public static void resetDataCompany() {
        companyBean = null;
    }

    public static void resetPosUser() {
        posUser = null;
    }

    public static void logout() {
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(PosControl.class);
        try {
            try (Statement stmt = mysql.getConnection().createStatement()) {
                String sql1 = "update posuser set onact='N',macno=''where (username='" + PublicVar._RealUser + "')";
                stmt.executeUpdate(sql1);

                String sql2 = "update poshwsetup set onact='N' where (terminal='" + Value.MACNO + "')";
                if (stmt.executeUpdate(sql2) > 0) {
                    // reset load poshwsetup
                    PosControl.resetPosHwSetup();
                }
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        } finally {
            mysql.closeConnection(PosControl.class);
        }
    }

    public CompanyBean getCompany() {
        return getDataCompany();
    }

    public static PosUserBean getPosUser(String username) {
        if (posUser != null) {
            return posUser;
        }
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(PosControl.class);
        try {
            String sql = "select * from posuser where username='" + username + "' limit 1";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    posUser = new PosUserBean();
                    posUser.setUserName(rs.getString("username"));
                    posUser.setPassword(rs.getString("password"));
                    posUser.setName(rs.getString("name"));
                    posUser.setUserGroup(rs.getString("UserGroup"));
                    posUser.setOnACT(rs.getString("OnACT"));
                    posUser.setMacNo(rs.getString("MacNo"));
                    posUser.setSale1(rs.getString("sale1"));
                    posUser.setSale2(rs.getString("sale2"));
                    posUser.setSale3(rs.getString("sale3"));
                    posUser.setSale4(rs.getString("sale4"));
                    posUser.setSale5(rs.getString("sale5"));
                    posUser.setSale6(rs.getString("sale6"));
                    posUser.setSale7(rs.getString("sale7"));
                    posUser.setSale8(rs.getString("sale8"));
                    posUser.setSale9(rs.getString("sale9"));
                    posUser.setSale10(rs.getString("sale10"));
                    posUser.setSale11(rs.getString("sale11"));
                    posUser.setSale12(rs.getString("sale12"));
                    posUser.setSale13(rs.getString("sale13"));
                    posUser.setSale14(rs.getString("sale14"));
                    posUser.setSale15(rs.getString("sale15"));
                    posUser.setSale16(rs.getString("sale16"));
                    posUser.setSale17(rs.getString("sale17"));
                    posUser.setSale18(rs.getString("sale18"));
                    posUser.setSale19(rs.getString("sale19"));
                    posUser.setSale20(rs.getString("sale20"));
                    posUser.setSale21(rs.getString("sale21"));
                    posUser.setSale22(rs.getString("sale22"));
                    posUser.setSale23(rs.getString("sale23"));
                    posUser.setSale24(rs.getString("sale24"));
                    posUser.setSale25(rs.getString("sale25"));
                    posUser.setSale26(rs.getString("sale26"));
                    posUser.setSale27(rs.getString("sale27"));
                    posUser.setSale28(rs.getString("sale28"));
                    posUser.setSale29(rs.getString("sale29"));
                    posUser.setSale30(rs.getString("sale30"));
                    posUser.setSale31(rs.getString("sale31"));
                    posUser.setSale32(rs.getString("sale32"));
                    posUser.setSale33(rs.getString("sale33"));
                    posUser.setSale34(rs.getString("sale34"));
                    posUser.setSale35(rs.getString("sale35"));
                    posUser.setSale36(rs.getString("sale36"));
                    posUser.setSale36(rs.getString("sale37"));
                    posUser.setSale36(rs.getString("sale38"));
                    posUser.setCont1(rs.getString("cont1"));
                    posUser.setCont2(rs.getString("cont2"));
                    posUser.setCont3(rs.getString("cont3"));
                    posUser.setCont4(rs.getString("cont4"));
                    posUser.setCont5(rs.getString("cont5"));
                    posUser.setCont6(rs.getString("cont6"));
                    posUser.setCont7(rs.getString("cont7"));
                    posUser.setCont8(rs.getString("cont8"));
                    posUser.setCont9(rs.getString("cont9"));
                    posUser.setCont10(rs.getString("cont10"));
                    posUser.setCont11(rs.getString("cont11"));
                    posUser.setCont12(rs.getString("cont12"));
                    posUser.setCont13(rs.getString("cont13"));
                    posUser.setCont14(rs.getString("cont14"));
                    posUser.setCont15(rs.getString("cont15"));
                    posUser.setCont16(rs.getString("cont16"));
                    posUser.setCont17(rs.getString("cont17"));
                    posUser.setCont18(rs.getString("cont18"));
                    posUser.setCont19(rs.getString("cont19"));
                    posUser.setCont20(rs.getString("cont20"));
                    posUser.setCont21(rs.getString("cont21"));
                    posUser.setCont22(rs.getString("cont22"));
                    posUser.setCont23(rs.getString("cont23"));
                    posUser.setCont24(rs.getString("cont24"));
                    posUser.setCont25(rs.getString("cont25"));
                    posUser.setCont26(rs.getString("cont26"));
                    posUser.setCont27(rs.getString("cont27"));
                    posUser.setCont28(rs.getString("cont28"));
                    posUser.setCont29(rs.getString("cont29"));
                    posUser.setCont30(rs.getString("cont30"));
                    posUser.setCont31(rs.getString("cont31"));
                    posUser.setCont32(rs.getString("cont32"));
                    posUser.setCont33(rs.getString("cont33"));
                    posUser.setCont34(rs.getString("cont34"));
                    posUser.setCont35(rs.getString("cont35"));
                    posUser.setCont36(rs.getString("cont36"));
                    posUser.setCont37(rs.getString("cont37"));
                    posUser.setCont38(rs.getString("cont38"));
                    posUser.setCont39(rs.getString("cont39"));
                    posUser.setCont40(rs.getString("cont40"));
                    posUser.setCont41(rs.getString("cont41"));
                    posUser.setCont42(rs.getString("cont42"));
                    posUser.setCont43(rs.getString("cont43"));
                    posUser.setCont44(rs.getString("cont44"));
                    posUser.setCont45(rs.getString("cont45"));
                    posUser.setCont46(rs.getString("cont46"));
                    posUser.setCont47(rs.getString("cont47"));
                    posUser.setStock1(rs.getString("Stock0"));
                    posUser.setStock1(rs.getString("Stock0_1"));
                    posUser.setStock1(rs.getString("Stock1"));
                    posUser.setStock2(rs.getString("Stock2"));
                    posUser.setStock3(rs.getString("Stock3"));
                    posUser.setStock4(rs.getString("Stock4"));
                    posUser.setStock5(rs.getString("Stock5"));
                    posUser.setStock6(rs.getString("Stock6"));
                    posUser.setStock7(rs.getString("Stock7"));
                    posUser.setStock8(rs.getString("Stock8"));
                    posUser.setStock9(rs.getString("Stock9"));
                    posUser.setStock10(rs.getString("Stock10"));
                    posUser.setStock11(rs.getString("Stock11"));
                    posUser.setStock12(rs.getString("Stock12"));
                    posUser.setStock13(rs.getString("Stock13"));
                    posUser.setStock14(rs.getString("Stock14"));
                    posUser.setStock15(rs.getString("Stock15"));
                    posUser.setStock16(rs.getString("Stock16"));
                    posUser.setStock17(rs.getString("Stock17"));
                    posUser.setStock18(rs.getString("Stock18"));
                    posUser.setStock19(rs.getString("Stock19"));
                    posUser.setStock20(rs.getString("Stock20"));
                    posUser.setStock21(rs.getString("Stock21"));
                    posUser.setStock22(rs.getString("Stock22"));
                    posUser.setStock23(rs.getString("Stock23"));
                    posUser.setStock24(rs.getString("Stock24"));
                    posUser.setStock25(rs.getString("Stock25"));
                    posUser.setStock26(rs.getString("Stock26"));
                    posUser.setStock27(rs.getString("Stock27"));
                    posUser.setStock28(rs.getString("Stock28"));
                    posUser.setStock29(rs.getString("Stock29"));
                    posUser.setStock30(rs.getString("Stock30"));
                    posUser.setStock31(rs.getString("Stock31"));
                    posUser.setStock32(rs.getString("Stock32"));
                    posUser.setStock33(rs.getString("Stock33"));
                    posUser.setStock34(rs.getString("Stock34"));
                    posUser.setStock35(rs.getString("Stock35"));
                    posUser.setStock36(rs.getString("Stock36"));
                    posUser.setStock37(rs.getString("Stock37"));
                    posUser.setStock38(rs.getString("Stock38"));
                    posUser.setStock39(rs.getString("Stock39"));
                    posUser.setStock40(rs.getString("Stock40"));
                    posUser.setStock41(rs.getString("Stock41"));
                    posUser.setStock42(rs.getString("Stock42"));
                    posUser.setStock43(rs.getString("Stock43"));
                    posUser.setStock44(rs.getString("Stock44"));
                    posUser.setStock45(rs.getString("Stock45"));
                    posUser.setStock46(rs.getString("Stock46"));
                    posUser.setStock47(rs.getString("Stock47"));
                    posUser.setStock48(rs.getString("Stock48"));
                    posUser.setStock49(rs.getString("Stock49"));
                    posUser.setStock50(rs.getString("Stock50"));
                    posUser.setStock51(rs.getString("Stock51"));
                    posUser.setStock52(rs.getString("Stock52"));
                    posUser.setStock53(rs.getString("Stock53"));
                    posUser.setStock54(rs.getString("Stock54"));
                    posUser.setStock55(rs.getString("Stock55"));
                    posUser.setStock56(rs.getString("Stock56"));
                    posUser.setStock57(rs.getString("Stock57"));
                    posUser.setStock58(rs.getString("Stock58"));
                    posUser.setStock59(rs.getString("Stock59"));
                    posUser.setStock60(rs.getString("Stock60"));
                    posUser.setStock61(rs.getString("Stock61"));
                    posUser.setStock62(rs.getString("Stock62"));
                    posUser.setStock63(rs.getString("Stock63"));
                    posUser.setStock64(rs.getString("Stock64"));
                    posUser.setStock65(rs.getString("Stock65"));
                    posUser.setStock66(rs.getString("Stock66"));
                    posUser.setStock67(rs.getString("Stock67"));
                    posUser.setStock68(rs.getString("Stock68"));
                    posUser.setStock69(rs.getString("Stock69"));
                    posUser.setStock70(rs.getString("Stock70"));
                    posUser.setStock71(rs.getString("Stock71"));
                    posUser.setStock72(rs.getString("Stock72"));
                    posUser.setStock73(rs.getString("Stock73"));
                    posUser.setStock74(rs.getString("Stock74"));
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(PosControl.class);
        }

        return posUser;
    }

    public static PosUserBean getPosUserToVoid(String username) {
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(PosControl.class);
        try {
            String sql = "select * from posuser where username='" + username + "' limit 1";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    posUser = new PosUserBean();
                    posUser.setUserName(rs.getString("username"));
                    posUser.setPassword(rs.getString("password"));
                    posUser.setName(rs.getString("name"));
                    posUser.setUserGroup(rs.getString("UserGroup"));
                    posUser.setOnACT(rs.getString("OnACT"));
                    posUser.setMacNo(rs.getString("MacNo"));
                    posUser.setSale1(rs.getString("sale1"));
                    posUser.setSale2(rs.getString("sale2"));
                    posUser.setSale3(rs.getString("sale3"));
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(PosControl.class);
        }

        return posUser;
    }

    public static CompanyBean getDataCompany() {
        if (companyBean != null) {
            return companyBean;
        }
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(PosControl.class);
        try {
            String sql = "select * from company limit 1";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    companyBean = new CompanyBean();
                    companyBean.setCode(rs.getString("Code"));
                    companyBean.setName(ThaiUtil.ASCII2Unicode(rs.getString("Name")));
                    companyBean.setAddress(ThaiUtil.ASCII2Unicode(rs.getString("Address")));
                    companyBean.setSubprovince(ThaiUtil.ASCII2Unicode(rs.getString("Subprovince")));
                    companyBean.setProvince(ThaiUtil.ASCII2Unicode(rs.getString("Province")));
                    companyBean.setCity(ThaiUtil.ASCII2Unicode(rs.getString("City")));
                    companyBean.setPOST(rs.getString("POST"));
                    companyBean.setTel(rs.getString("Tel"));
                    companyBean.setFax(rs.getString("Fax"));
                    companyBean.setEmailaddress(rs.getString("emailaddress"));
                    companyBean.setTax(rs.getString("Tax"));
                    companyBean.setAccterm(rs.getDate("Accterm"));
                    companyBean.setPosStock(rs.getString("PosStock"));
                    companyBean.setRecCost(rs.getString("RecCost"));
                    companyBean.setTriCost(rs.getString("TriCost"));
                    companyBean.setTroCost(rs.getString("TroCost"));
                    companyBean.setLosCost(rs.getString("LosCost"));
                    companyBean.setFreCost(rs.getString("FreCost"));
                    companyBean.setTri_Cost(rs.getString("Tri_Cost"));
                    companyBean.setAdjCost(rs.getString("AdjCost"));
                    companyBean.setRecAvgCost(rs.getString("RecAvgCost"));
                    companyBean.setTriAvgCost(rs.getString("TriAvgCost"));
                    companyBean.setTroAvgCost(rs.getString("TroAvgCost"));
                    companyBean.setLosAvgCost(rs.getString("LosAvgCost"));
                    companyBean.setFreAvgCost(rs.getString("FreAvgCost"));
                    companyBean.setTri_AvgCost(rs.getString("Tri_AvgCost"));
                    companyBean.setAdjAvgCost(rs.getString("AdjAvgCost"));
                    companyBean.setUsePSetCost(rs.getString("UsePSetCost"));
                    companyBean.setUsePIngredentCost(rs.getString("UsePIngredentCost"));
                    companyBean.setHead1(rs.getString("Head1"));
                    companyBean.setHead2(rs.getString("Head2"));
                    companyBean.setHead3(rs.getString("Head3"));
                    companyBean.setHead4(rs.getString("Head4"));
                    companyBean.setPdahead1(rs.getString("pdahead1"));
                    companyBean.setPdahead2(rs.getString("pdahead2"));
                    companyBean.setDisplayTextInfo(rs.getString("DisplayTextInfo"));
                    companyBean.setFloorTab1(rs.getString("FloorTab1"));
                    companyBean.setFloorTab2(rs.getString("FloorTab2"));
                    companyBean.setFloorTab3(rs.getString("FloorTab3"));
                    companyBean.setFloorTab4(rs.getString("FloorTab4"));
                    companyBean.setFloorTab5(rs.getString("FloorTab5"));
                    companyBean.setFloorTab6(rs.getString("FloorTab6"));
                    companyBean.setFloorTab7(rs.getString("FloorTab7"));
                    companyBean.setPdahead3(rs.getString("pdahead3"));
                    companyBean.setPdahead4(rs.getString("pdahead4"));
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(PosControl.class);
        }

        return companyBean;
    }

    public static POSConfigSetup getData() {
        if (posConfigSetup != null) {
            return posConfigSetup;
        }
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(PosControl.class);
        try {
            String sql = "select * from posconfigsetup limit 1";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    posConfigSetup = new POSConfigSetup();
                    posConfigSetup.setP_Terminal(rs.getString("P_Terminal"));
                    posConfigSetup.setP_Vat(rs.getFloat("P_Vat"));
                    posConfigSetup.setP_Service(rs.getFloat("P_Service"));
                    posConfigSetup.setP_ServiceType(rs.getString("P_ServiceType"));
                    posConfigSetup.setP_VatPrn(rs.getString("P_VatPrn"));
                    posConfigSetup.setP_VatType(rs.getString("P_VatType"));
                    posConfigSetup.setP_BillCopy(rs.getInt("P_BillCopy"));
                    posConfigSetup.setP_BillCopyOne(rs.getString("P_BillCopyOne"));
                    posConfigSetup.setP_DefaultSaleType(rs.getString("P_DefaultSaleType"));
                    posConfigSetup.setP_EmpUse(rs.getString("P_EmpUse"));
                    posConfigSetup.setP_CodePrn(rs.getString("P_CodePrn"));
                    posConfigSetup.setP_CheckBillBeforCash(rs.getString("P_CheckBillBeforCash"));
                    posConfigSetup.setP_PrintDetailOnRecp(rs.getString("P_PrintDetailOnRecp"));
                    posConfigSetup.setP_PrintSum(rs.getString("P_PrintSum"));
                    posConfigSetup.setP_PrintRecpMessage(ThaiUtil.ASCII2Unicode(rs.getString("P_PrintRecpMessage")));
                    posConfigSetup.setP_MemDisc(rs.getString("P_MemDisc"));
                    posConfigSetup.setP_MemDiscChk(rs.getString("P_MemDiscChk"));
                    posConfigSetup.setP_MemDiscGet(rs.getString("P_MemDiscGet"));
                    posConfigSetup.setP_MemDiscMax(rs.getString("P_MemDiscMax"));
                    posConfigSetup.setP_FastDisc(rs.getString("P_FastDisc"));
                    posConfigSetup.setP_FastDiscChk(rs.getString("P_FastDiscChk"));
                    posConfigSetup.setP_FastDiscGet(rs.getString("P_FastDiscGet"));
                    posConfigSetup.setP_FastDiscMax(rs.getString("P_FastDiscMax"));
                    posConfigSetup.setP_EmpDisc(rs.getString("P_EmpDisc"));
                    posConfigSetup.setP_EmpDiscChk(rs.getString("P_EmpDiscChk"));
                    posConfigSetup.setP_EmpDiscGet(rs.getString("P_EmpDiscGet"));
                    posConfigSetup.setP_EmpDiscMax(rs.getString("P_EmpDiscMax"));
                    posConfigSetup.setP_TrainDisc(rs.getString("P_TrainDisc"));
                    posConfigSetup.setP_TrainDiscChk(rs.getString("P_TrainDiscChk"));
                    posConfigSetup.setP_TrainDiscGet(rs.getString("P_TrainDiscGet"));
                    posConfigSetup.setP_TrainDiscMax(rs.getString("P_TrainDiscMax"));
                    posConfigSetup.setP_SubDisc(rs.getString("P_SubDisc"));
                    posConfigSetup.setP_SubDiscChk(rs.getString("P_SubDiscChk"));
                    posConfigSetup.setP_SubDiscGet(rs.getString("P_SubDiscGet"));
                    posConfigSetup.setP_SubDiscMax(rs.getString("P_SubDiscMax"));
                    posConfigSetup.setP_DiscBathChk(rs.getString("P_DiscBathChk"));
                    posConfigSetup.setP_DiscBathMax(rs.getInt("P_DiscBathMax"));
                    posConfigSetup.setP_PromotionChk(rs.getString("P_PromotionChk"));
                    posConfigSetup.setP_SpacialChk(rs.getString("P_SpacialChk"));
                    posConfigSetup.setP_DiscRound(rs.getString("P_DiscRound"));
                    posConfigSetup.setP_ServiceRound(rs.getString("P_ServiceRound"));
                    posConfigSetup.setP_SerChkBySaleType(rs.getString("P_SerChkBySaleType"));
                    posConfigSetup.setP_DiscChkBySaleType(rs.getString("P_DiscChkBySaleType"));
                    posConfigSetup.setP_MemberSystem(rs.getString("P_MemberSystem"));
                    posConfigSetup.setKicSum(rs.getString("KicSum"));
                    posConfigSetup.setKicCopy(rs.getString("KicCopy"));
                    posConfigSetup.setP_PrintByItemType(rs.getString("P_PrintByItemType"));
                    posConfigSetup.setP_PrintTotalSumItemType(rs.getString("P_PrintTotalSumItemType"));
                    posConfigSetup.setP_PrintTotalSumNormalType(rs.getString("P_PrintTotalSumNormalType"));
                    posConfigSetup.setP_PrintTotalSumGroup(rs.getString("P_PrintTotalSumGroup"));
                    posConfigSetup.setWTime(rs.getString("WTime"));
                    posConfigSetup.setLTime(rs.getString("LTime"));
                    posConfigSetup.setP_PrintProductValue(rs.getString("P_PrintProductValue"));
//                    posConfigSetup.setP_LimitTime(rs.getInt("P_LimitTime"));

                    posConfigSetup.setP_RefreshTime(rs.getInt("P_RefreshTime"));
                    posConfigSetup.setP_SaleDecimal(rs.getString("P_SaleDecimal"));
                    posConfigSetup.setP_PayBahtRound(rs.getString("P_PayBahtRound"));
                }
                rs.close();
                stmt.close();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(PosControl.class);
        }

        return posConfigSetup;
    }

    public static POSHWSetup getData(String macno) {
        if (poshwsetup != null) {
            return poshwsetup;
        }
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(PosControl.class);
        try {
            String sql = "select * from poshwsetup where terminal='" + macno + "' limit 1";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    poshwsetup = new POSHWSetup();
                    poshwsetup.setTerminal(rs.getString("macno"));
                    poshwsetup.setOnAct(rs.getString("OnAct"));
                    poshwsetup.setMacNo(macno);
                    poshwsetup.setReceNo1(rs.getDouble("ReceNo1"));
                    poshwsetup.setSaleType(rs.getString("SaleType"));
                    poshwsetup.setTStock(rs.getString("TStock"));
                    poshwsetup.setTSone(rs.getString("TSone"));
                    poshwsetup.setHeading1(ThaiUtil.ASCII2Unicode(rs.getString("Heading1")));
                    poshwsetup.setHeading2(ThaiUtil.ASCII2Unicode(rs.getString("Heading2")));
                    poshwsetup.setHeading3(ThaiUtil.ASCII2Unicode(rs.getString("Heading3")));
                    poshwsetup.setHeading4(ThaiUtil.ASCII2Unicode(rs.getString("Heading4")));
                    poshwsetup.setFootting1(ThaiUtil.ASCII2Unicode(rs.getString("Footting1")));
                    poshwsetup.setFootting2(ThaiUtil.ASCII2Unicode(rs.getString("Footting2")));
                    poshwsetup.setFootting3(ThaiUtil.ASCII2Unicode(rs.getString("Footting3")));
                    poshwsetup.setDRPort(rs.getString("DRPort"));
                    poshwsetup.setDRType(rs.getString("DRType"));
                    poshwsetup.setDRCOM(rs.getString("DRCOM"));
                    poshwsetup.setDISPort(rs.getString("DISPort"));
                    poshwsetup.setDISType(rs.getString("DISType"));
                    poshwsetup.setDISCOM(rs.getString("DISCOM"));
                    poshwsetup.setPRNPort(rs.getString("PRNPort"));
                    poshwsetup.setPRNTYPE(rs.getString("PRNTYPE"));
                    poshwsetup.setPRNCOM(rs.getString("PRNCOM"));
                    poshwsetup.setPRNThaiLevel(rs.getString("PRNThaiLevel"));
                    poshwsetup.setKIC1Port(rs.getString("KIC1Port"));
                    poshwsetup.setKIC1Type(rs.getString("KIC1Type"));
                    poshwsetup.setKIC1Com(rs.getString("KIC1Com"));
                    poshwsetup.setKIC1ThaiLevel(rs.getString("KIC1ThaiLevel"));
                    poshwsetup.setKIC2Port(rs.getString("KIC2Port"));
                    poshwsetup.setKIC2Type(rs.getString("KIC2Type"));
                    poshwsetup.setKIC2Com(rs.getString("KIC2Com"));
                    poshwsetup.setKIC2ThaiLevel(rs.getString("KIC2ThaiLevel"));
                    poshwsetup.setKIC3Port(rs.getString("KIC3Port"));
                    poshwsetup.setKIC3Type(rs.getString("KIC3Type"));
                    poshwsetup.setKIC3Com(rs.getString("KIC3Com"));
                    poshwsetup.setKIC3ThaiLevel(rs.getString("KIC3ThaiLevel"));
                    poshwsetup.setKIC4Port(rs.getString("KIC4Port"));
                    poshwsetup.setKIC4Type(rs.getString("KIC4Type"));
                    poshwsetup.setKIC4Com(rs.getString("KIC4Com"));
                    poshwsetup.setKIC4ThaiLevel(rs.getString("KIC4ThaiLevel"));
                    poshwsetup.setEJounal(rs.getString("EJounal"));
                    poshwsetup.setEJDailyPath(rs.getString("EJDailyPath"));
                    poshwsetup.setEJBackupPath(rs.getString("EJBackupPath"));
                    poshwsetup.setPrnRate(rs.getInt("PrnRate"));
                    poshwsetup.setDrRate(rs.getInt("DrRate"));
                    poshwsetup.setDisRate(rs.getInt("DisRate"));
                    poshwsetup.setEDCPort(rs.getString("EDCPort"));
                    poshwsetup.setSMPBank(rs.getString("SMPBank"));
                    poshwsetup.setMenuItemList(rs.getString("MenuItemList"));
                    poshwsetup.setUseFloorPlan(rs.getString("UseFloorPlan"));
                    poshwsetup.setTakeOrderChk(rs.getString("TakeOrderChk"));
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(PosControl.class);
        }

        return poshwsetup;
    }

    private String[] getETDPW(String data) {
        String[] d = data.split("/");
        String str = "";
        if (d[0].equals("Y")) {
            str += "E,";
        }
        if (d[1].equals("Y")) {
            str += "T,";
        }
        if (d[2].equals("Y")) {
            str += "D,";
        }
        if (d[3].equals("Y")) {
            str += "P,";
        }
        if (d[4].equals("Y")) {
            str += "W,";
        }
        return str.split(",");
    }

    public boolean getETDPW_Active(String ETD, String data) {
        if (ETD == null || data == null) {
            return false;
        }
        String[] d = getETDPW(data);
        for (String dd : d) {
            if (ETD.equals(dd)) {
                return true;
            }
        }
        return false;
    }

    public static int getRefreshTime() {
        return getData().getP_RefreshTime();
    }

    public void posHwSetupOnAct(String Onact) {
        MySQLConnect mysql = new MySQLConnect();
        try {
            mysql.open(PosControl.class);
            String sql = "update poshwsetup set onact='" + Onact + "' where terminal='" + Value.MACNO + "';";
            Statement stmt = mysql.getConnection().createStatement();
            if (stmt.executeUpdate(sql) > 0) {
                // reset load poshwsetup
                PosControl.resetPosHwSetup();
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(PosControl.class);
        }
    }

}
