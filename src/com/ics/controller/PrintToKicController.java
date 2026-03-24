package com.ics.controller;

import com.ics.main.ui.PrintToKic;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ics.model.BalanceBean;
import com.ics.process.DatabaseConnection;
import com.ics.process.MySQLConnect;

/**
 *
 * @author nathee
 */
public class PrintToKicController extends DatabaseConnection {

    private final MySQLConnect mysqlLocal = new MySQLConnect();

    public BalanceBean getBalaneForPDA() {
        BalanceBean bean = null;

        mysqlLocal.open();
        try {
            String sql = "select r_table,macno,"
                    + "sum(b.r_quan) qty,sum(b.r_total) total from balance b "
                    + "where trantype='PDA' "
                    + "and r_kicprint<>'P' and r_void<>'V' "
                    + "and r_kic<>'0' and r_printOK='Y' and r_pause='P' "
                    + "group by r_table order by r_etd,r_index;";
            System.out.println(sql);
            try (ResultSet rs = mysqlLocal.getConnection().createStatement().executeQuery(sql)) {
                if (rs.next()) {
                    PrintToKic.kicPrintting = true;
                    bean = new BalanceBean();
                    bean.setR_Table(rs.getString("r_table"));
                    bean.setR_Total(rs.getDouble("total"));
                    bean.setMacno(rs.getString("macno"));
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysqlLocal.close();
        }

        return bean;
    }

    public List<BalanceBean> getBalaneForPDAByTableNo(String tableNo, String macno) {
        List<BalanceBean> listBalance = new ArrayList<>();

        mysqlLocal.open();
        try {
            String sql = "select r_kic,r_etd from balance "
                    + "where r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and TranType='PDA' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'0' "
                    + "and R_Void<>'V' "
                    + "and R_Pause='P' "
                    + "and macno='" + macno + "' "
                    + "group by r_kic,r_etd "
                    + "order by r_kic, r_etd";
            try (ResultSet rs = mysqlLocal.getConnection().createStatement().executeQuery(sql)) {
                while (rs.next()) {
                    BalanceBean bean = new BalanceBean();
                    bean.setR_Kic(rs.getString("r_kic"));
                    bean.setR_ETD(rs.getString("r_etd"));

                    listBalance.add(bean);
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysqlLocal.close();
        }

        return listBalance;
    }

    public List<BalanceBean> getBalancePrintForm1(String tableNo, String rKic) {
        List<BalanceBean> listBalance = new ArrayList<>();

        mysqlLocal.open();
        try {
            String sql = "select * from balance "
                    + "where trantype='PDA' "
                    + "and r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'' "
                    + "and R_Void<>'V' "
                    + "and R_kic='" + rKic + "' "
                    + "group by r_plucode";
            try (ResultSet rs = mysqlLocal.getConnection().createStatement().executeQuery(sql)) {
                if (rs.next()) {
                    BalanceBean bean = new BalanceBean();
                    bean.setR_PluCode(rs.getString("R_PluCode"));

                    listBalance.add(bean);
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysqlLocal.close();
        }

        return listBalance;
    }

    public List<BalanceBean> getBalancePrintForm6(String tableNo, String rKic) {
        List<BalanceBean> listBalance = new ArrayList<>();

        mysqlLocal.open();
        try {
            String sql = "select * from balance "
                    + "where r_table='" + tableNo + "' "
                    + "and R_PrintOK='Y' "
                    + "and R_KicPrint<>'P' "
                    + "and R_Kic<>'' "
                    + "and R_Void<>'V' "
                    + "and R_Kic='" + rKic + "'";
            try (ResultSet rs = mysqlLocal.getConnection().createStatement().executeQuery(sql)) {
                if (rs.next()) {
                    BalanceBean bean = new BalanceBean();
                    bean.setR_PluCode(rs.getString("R_PluCode"));
                    bean.setR_Quan(rs.getDouble("R_Quan"));
                    bean.setR_Total(rs.getDouble("R_Total"));
                    bean.setR_Index(rs.getString("R_Index"));

                    listBalance.add(bean);
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysqlLocal.close();
        }

        return listBalance;
    }
}
