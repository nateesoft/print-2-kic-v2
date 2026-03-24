package com.ics.process;

import com.ics.util.ThaiUtil;
import java.sql.SQLException;

/**
 *
 * @author Dell-Softpos
 */
public class ControlPrintCheckBill {

    public void PrintCheckBill(String tableNO, boolean CheckBill, String emp, String PrinterName, String Macno) {

        if (CheckBill == true) {
            MySQLConnect mysql = new MySQLConnect();
            try {
                mysql.open();
                String sql = "update balance set PDAPrintCheck='Y',"
                        + "pdaemp='" + ThaiUtil.Unicode2ASCII(emp) + "',"
                        + "PDAPrintCheckStation='" + PrinterName + "' "
                        + "where r_table='" + tableNO.toUpperCase() + "' "
                        + "and trantype ='PDA';";
                mysql.getConnection().createStatement().executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println(e.toString());
            } finally {
                mysql.close();
            }
        }
    }

    public void setPrintCheckBillItemAfterSendKic(String tableNO) {
        MySQLConnect mysql = new MySQLConnect();
        try {
            String sql = "update balance set PDAPrintChekItemStation='Y' "
                    + "where PDAPrintChekItemStation='N' and r_table='" + tableNO + "'";
            mysql.open();
            mysql.getConnection().createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            mysql.close();
        }
    }

    public void PrintUrgentFood(String tableNO) {
        MySQLConnect mysql = new MySQLConnect();
        try {
            String sql = "update kictran "
                    + "set R_FoodUrgent='Y',R_AlertKitChen='Y' "
                    + "where PTable='" + tableNO + "' "
                    + "and PFlage='N';";
            mysql.open();
            mysql.getConnection().createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

    }
}
