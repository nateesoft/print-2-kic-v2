package com.ics.process;

import java.sql.SQLException;

/**
 *
 * @author Dell-Softpos
 */
public class ControlPrintCheckBill {

    private final MySQLConnect mysqlLocal = new MySQLConnect();

    public void setPrintCheckBillItemAfterSendKic(String tableNO) {
        try {
            String sql = "update balance set "
                    + "PDAPrintChekItemStation='Y' "
                    + "where PDAPrintChekItemStation='N' and r_table='" + tableNO + "'";
            mysqlLocal.open();
            mysqlLocal.getConnection().createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            mysqlLocal.close();
        }
    }
}
