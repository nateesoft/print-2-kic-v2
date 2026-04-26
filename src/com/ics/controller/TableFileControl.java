package com.ics.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.ics.process.DatabaseConnection;
import com.ics.process.MySQLConnect;
import com.ics.model.TableFileBean;
import com.ics.util.ThaiUtil;

public class TableFileControl extends DatabaseConnection {

    public static final int TABLE_READY = 1;
    public static final int TABLE_NOT_ACTIVE = 2;
    public static final int TABLE_NOT_SETUP = 0;
    public static final int TABLE_EXIST_DATA = 3;
    public static final int TABLE_EXIST_DATA_IS_ACTIVE = 4;
    
    private static final MySQLConnect mysql = new MySQLConnect();

    public TableFileBean getData(String table) {
        TableFileBean bean = new TableFileBean();
        try {
            mysql.open();
            String sql = "select * from tablefile where Tcode='" + table + "' limit 1";
            try (Statement stmt = mysql.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    bean.setTcode(rs.getString("Tcode"));
                    bean.setSoneCode(rs.getString("SoneCode"));
                    bean.setMacNo(rs.getString("MacNo"));
                    bean.setCashier(rs.getString("Cashier"));
                    bean.setTLoginTime(rs.getString("TLoginTime"));
                    bean.setTCurTime(rs.getString("TCurTime"));
                    bean.setTCustomer(rs.getInt("TCustomer"));
                    bean.setTItem(rs.getInt("TItem"));
                    bean.setTAmount(rs.getFloat("TAmount"));
                    bean.setTOnAct(rs.getString("TOnAct"));
                    bean.setService(rs.getFloat("Service"));
                    bean.setServiceAmt(rs.getFloat("ServiceAmt"));
                    bean.setEmpDisc(rs.getString("EmpDisc"));
                    bean.setEmpDiscAmt(rs.getFloat("EmpDiscAmt"));
                    bean.setFastDisc(rs.getString("FastDisc"));
                    bean.setFastDiscAmt(rs.getFloat("FastDiscAmt"));
                    bean.setTrainDisc(rs.getString("TrainDisc"));
                    bean.setTrainDiscAmt(rs.getFloat("TrainDiscAmt"));
                    bean.setMemDisc(rs.getString("MemDisc"));
                    bean.setMemDiscAmt(rs.getFloat("MemDiscAmt"));
                    bean.setSubDisc(rs.getString("SubDisc"));
                    bean.setSubDiscAmt(rs.getFloat("SubDiscAmt"));
                    bean.setDiscBath(rs.getFloat("DiscBath"));
                    bean.setProDiscAmt(rs.getFloat("ProDiscAmt"));
                    bean.setSpaDiscAmt(rs.getFloat("SpaDiscAmt"));
                    bean.setCuponDiscAmt(rs.getFloat("CuponDiscAmt"));
                    bean.setItemDiscAmt(rs.getFloat("ItemDiscAmt"));
                    bean.setMemCode(rs.getString("MemCode"));
                    if (bean.getMemCode() == null) {
                        bean.setMemCode("");
                    }
                    bean.setMemCurAmt(rs.getFloat("MemCurAmt"));
                    bean.setMemName(ThaiUtil.ASCII2Unicode(rs.getString("MemName")));
                    if (bean.getMemName() == null) {
                        bean.setMemName("");
                    }
                    bean.setFood(rs.getFloat("Food"));
                    bean.setDrink(rs.getFloat("Drink"));
                    bean.setProduct(rs.getFloat("Product"));

                    bean.setNetTotal(rs.getFloat("NetTotal"));
                    bean.setPrintTotal(rs.getFloat("PrintTotal"));
                    bean.setPrintChkBill(rs.getString("PrintChkBill"));
                    bean.setPrintCnt(rs.getInt("PrintCnt"));
                    bean.setPrintTime1(rs.getString("PrintTime1"));
                    bean.setPrintTime2(rs.getString("PrintTime2"));
                    bean.setChkBill(rs.getString("ChkBill"));
                    bean.setChkBillTime(rs.getString("ChkBillTime"));
                    bean.setStkCode1(rs.getString("StkCode1"));
                    bean.setStkCode2(rs.getString("StkCode2"));
                    bean.setTDesk(rs.getInt("TDesk"));
                    bean.setTUser(rs.getString("TUser"));
                    bean.setTPause(rs.getString("TPause"));
                } else {
                    bean.setTcode("null");
                }
                stmt.close();
                rs.close();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        return bean;
    }

}
