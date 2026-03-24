package com.ics.controller;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.ics.model.BalanceBean;
import com.ics.process.DatabaseConnection;
import com.ics.util.DateConvert;
import com.ics.process.MySQLConnect;
import com.ics.util.NumberUtil;
import com.ics.model.POSConfigSetup;
import com.ics.model.TableFileBean;

public class ServiceControl extends DatabaseConnection {

    private POSConfigSetup CONFIG;
    private final PosControl posControl;
    private SimpleDateFormat Timefmt = new SimpleDateFormat("HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("##.00");

    public ServiceControl() {
        posControl = new PosControl();
        CONFIG = POSConfigSetup.Bean();
    }

    public void updateService(String table) {
        BalanceControl balanceControl = new BalanceControl();
        List<BalanceBean> dataBalance = balanceControl.getAllBalanceNoVoidSum(table);
        double ServiceTotal = 0.00;
        double ServicePercent = POSConfigSetup.Bean().getP_Service();
        double ServiceAmt = 0.00;
        double tAmount = 0;
        double totalDiscount = 0.00;
        for (int i = 0; i < dataBalance.size(); i++) {
            double ServiceLine = 0.00;
            BalanceBean balance = (BalanceBean) dataBalance.get(i);

            //ตรวจสอบสินค้าที่ Void ไปแล้ว
            if ("V".equals(balance.getR_Void())) {
                continue;
            }
            totalDiscount
                    += balance.getR_PrAmt()
                    + balance.getR_PrSubAmt()
                    + balance.getR_PrSubBath()
                    + balance.getR_PrCuAmt()
                    + balance.getR_DiscBath();
            totalDiscount = (totalDiscount);
            if (posControl.getETDPW_Active(balance.getR_ETD(), POSConfigSetup.Bean().getP_SerChkBySaleType())) {

                //ตรวจสอบการให้ส่วนลด
                if ("Y".equals(balance.getR_Service())) {
                    String a = POSConfigSetup.Bean().getP_SerChkBySaleType();
                    //คิดค่าบริการแบบยอด Net
                    if (POSConfigSetup.Bean().getP_ServiceType().equals("N")) {
                        String[] strs = POSConfigSetup.Bean().getP_SerChkBySaleType().split("/");
                        String serviceSaleTypeE = strs[0];
                        String serviceSaleTypeT = strs[1];
                        String serviceSaleTypeD = strs[2];
                        String serviceSaleTypeP = strs[3];
                        String serviceSaleTypeW = strs[4];

                        if (balance.getR_ETD().equals("E") && serviceSaleTypeE.equals("Y")) {
                            ServiceLine = balance.getR_Total();
                        } else if (balance.getR_ETD().equals("T") && serviceSaleTypeT.equals("Y")) {
                            ServiceLine = balance.getR_Total();
                        } else if (balance.getR_ETD().equals("D") && serviceSaleTypeD.equals("Y")) {
                            ServiceLine = balance.getR_Total();
                        } else if (balance.getR_ETD().equals("P") && serviceSaleTypeP.equals("Y")) {
                            ServiceLine = balance.getR_Total();
                        } else if (balance.getR_ETD().equals("W") && serviceSaleTypeW.equals("Y")) {
                            ServiceLine = balance.getR_Total();
                        } else if (balance.getR_ETD().equals("E") && serviceSaleTypeE.equals("N")) {
                            ServiceLine = 0;
                        } else if (balance.getR_ETD().equals("T") && serviceSaleTypeT.equals("N")) {
                            ServiceLine = 0;
                        } else if (balance.getR_ETD().equals("D") && serviceSaleTypeD.equals("N")) {
                            ServiceLine = 0;
                        } else if (balance.getR_ETD().equals("P") && serviceSaleTypeP.equals("N")) {
                            ServiceLine = 0;
                        } else if (balance.getR_ETD().equals("W") && serviceSaleTypeW.equals("N")) {
                            ServiceLine = 0;
                        }

                        ServiceTotal += ServiceLine;
                    }
                }

                if (ServiceTotal > 0 && POSConfigSetup.Bean().getP_ServiceType().equals("N")) {
                    ServiceAmt = (ServiceTotal - totalDiscount) * ServicePercent / 100;
                }
//                    คิดภาษีหรือไม่ ?
                if ("V".equals(balance.getR_Vat())) {
                    if (POSConfigSetup.Bean().getP_VatType().equals("I")) {
                        //คิดภาษีแบบ Include Vat
                    }
                    if (POSConfigSetup.Bean().getP_VatType().equals("E")) {
                        //คิดภาษีแบบ Exclude Vat
                    }
                }
            }
            tAmount += balance.getR_Total();
        }

        DateConvert dc = new DateConvert();
        MySQLConnect mysql = new MySQLConnect();
        try {
            TableFileControl tfc = new TableFileControl();
            TableFileBean tBean = tfc.getData(table);

            /**
             * * OPEN CONNECTION **
             */
            mysql.open(this.getClass());

            ServiceAmt = getDouble(ServiceAmt, "SERVICE");
            totalDiscount = getDouble(totalDiscount, "DISCOUNT");
            double NetTotal = tAmount - totalDiscount + ServiceAmt;
            String sqlUpd = "update tablefile "
                    + "set ServiceAmt = '" + ServiceAmt + "',"
                    + "TAmount='" + tAmount + "',"
                    + "NetTotal = " + NetTotal + " "
                    + "where Tcode = '" + table + "'";
            Statement stmt3 = mysql.getConnection().createStatement();
            stmt3.executeUpdate(sqlUpd);
            stmt3.close();
            // update all discount
            if (tAmount > 0 && ("00:00:00".equals(tBean.getTLoginTime()) || tBean.getTLoginTime() == null)) {
                String sqlUpdateDate = "update tablefile set "
                        + "tlogindate='" + dc.GetCurrentDate() + "', "
                        + "tlogintime='" + Timefmt.format(new Date()) + "' "
                        + "where tcode='" + table + "'";
                Statement stmt5 = mysql.getConnection().createStatement();
                stmt5.executeUpdate(sqlUpdateDate);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }
    }

    public static double getDouble(double db, String type) {
        if (db > 0) {
            switch (type) {
                case "SERVICE":
                    if (POSConfigSetup.Bean().getP_ServiceRound().equalsIgnoreCase("U")) {
                        db = NumberUtil.UP_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_ServiceRound().equalsIgnoreCase("D")) {
                        db = NumberUtil.DOWN_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_ServiceRound().equalsIgnoreCase("O")) {
                        return db;
                    }
                    if (POSConfigSetup.Bean().getP_ServiceRound().equalsIgnoreCase("N")) {
                        db = NumberUtil.UP_DOWN_NATURAL_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_ServiceRound().equalsIgnoreCase("F")) {
                        db = NumberUtil.UP_DOWN_25NewTotal(db);
                    } else {
                        return db;
                    }
                    break;
                case "DISCOUNT":
                    if (POSConfigSetup.Bean().getP_DiscRound().equalsIgnoreCase("U")) {
                        db = NumberUtil.UP_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_DiscRound().equalsIgnoreCase("D")) {
                        db = NumberUtil.DOWN_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_DiscRound().equalsIgnoreCase("O")) {
                        return db;
                    }
                    if (POSConfigSetup.Bean().getP_DiscRound().equalsIgnoreCase("N")) {
                        db = NumberUtil.UP_DOWN_NATURAL_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_DiscRound().equalsIgnoreCase("F")) {
                        db = NumberUtil.UP_DOWN_25(db);
                    } else {
                        return db;
                    }
                    break;
                case "PAYMENT":
                    if (POSConfigSetup.Bean().getP_PayBahtRound().equalsIgnoreCase("U")) {
                        db = NumberUtil.UP_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_PayBahtRound().equalsIgnoreCase("D")) {
                        db = NumberUtil.DOWN_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_PayBahtRound().equalsIgnoreCase("O")) {
                        return db;
                    }
                    if (POSConfigSetup.Bean().getP_PayBahtRound().equalsIgnoreCase("N")) {
                        db = NumberUtil.UP_DOWN_NATURAL_BAHT(db);
                    }
                    if (POSConfigSetup.Bean().getP_PayBahtRound().equalsIgnoreCase("F")) {
                        db = NumberUtil.UP_DOWN_25(db);
                    } else {
                        return db;
                    }
                    break;
            }
        }

        return db;
    }

}
