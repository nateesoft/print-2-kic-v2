package com.ics.controller;

import com.ics.model.BalanceBean;
import com.ics.model.CharactorCheck;
import com.ics.model.ProductBean;
import com.ics.model.RabbitMQOrderBean;
import com.ics.model.RabbitMQOrderBean.ItemDetail;
import com.ics.process.DatabaseConnection;
import com.ics.process.MySQLConnect;
import com.ics.util.ThaiUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class BalanceControl extends DatabaseConnection {

    public static final int TABLE_READY = 1;
    public static final int TABLE_NOT_ACTIVE = 2;
    public static final int TABLE_NOT_SETUP = 0;
    public static final int TABLE_EXIST_DATA = 3;
    public static final int TABLE_EXIST_DATA_IS_ACTIVE = 4;

    private final CharactorCheck charEngCheck = new CharactorCheck();
    private static final MySQLConnect mysql = new MySQLConnect();
    private final ProductControl productControl = new ProductControl();

    public int saveFromCloudCustomerOrder(RabbitMQOrderBean orderBean) {
        List<BalanceBean> balanceListMapping = mappingOrderBalance(orderBean);

        int resultUpdated = saveBalance(balanceListMapping);
        if (resultUpdated > 0) {
            return updateTableFileSummary(orderBean);
        }

        return 0;
    }

    private List<BalanceBean> mappingOrderBalance(RabbitMQOrderBean orderBean) {
        List<BalanceBean> listBalance = new ArrayList<>();
        for (ItemDetail itemDetail : orderBean.getItemDetails()) {
            ProductBean product = productControl.getData(itemDetail.getCode());
            
            String[] options = getMenuOptions(itemDetail.getSpecialInstructions());
            String balanceIndex = getIndexBalance(orderBean.getTableNumber());
            BalanceBean balanceBean = new BalanceBean();
            balanceBean.setTranType("PDA"); // default from web order
            balanceBean.setR_TranType("0");
            balanceBean.setR_Index(balanceIndex);
            balanceBean.setR_Table(orderBean.getTableNumber());
            balanceBean.setR_Time("");
            balanceBean.setMacno("");
            balanceBean.setCashier("");
            balanceBean.setR_Emp("");
            balanceBean.setR_PluCode(itemDetail.getCode());
            balanceBean.setR_PName(ThaiUtil.Unicode2ASCII(itemDetail.getName()));
            balanceBean.setR_Unit(ThaiUtil.Unicode2ASCII(product.getPUnit1()));
            balanceBean.setR_Group(product.getPGroup());
            balanceBean.setR_Status(product.getPStatus());
            balanceBean.setR_Normal("");
            balanceBean.setR_Discount("");
            balanceBean.setR_Service("");
            balanceBean.setR_Stock("");
            balanceBean.setR_Set("");
            balanceBean.setR_Vat("");
            balanceBean.setR_Type("");
            balanceBean.setR_ETD("");
            balanceBean.setR_Quan(itemDetail.getQuantity());
            balanceBean.setR_Price(itemDetail.getPrice());
            balanceBean.setR_Total(itemDetail.getQuantity() * itemDetail.getPrice());
            balanceBean.setR_PrType("");
            balanceBean.setR_PrCode("");
            balanceBean.setR_PrDisc(0);
            balanceBean.setR_PrBath(0);
            balanceBean.setR_PrAmt(0);
            balanceBean.setR_DiscBath(0);
            balanceBean.setR_PrCuType("");
            balanceBean.setR_PrCuQuan(0);
            balanceBean.setR_PrCuAmt(0);
            balanceBean.setR_Redule(0);
            balanceBean.setR_Kic("");
            balanceBean.setR_KicPrint("");
            balanceBean.setR_Void("");
            balanceBean.setR_VoidUser("");
            balanceBean.setR_VoidTime("");
            balanceBean.setR_Opt1(options[0]);
            balanceBean.setR_Opt2(options[1]);
            balanceBean.setR_Opt3(options[2]);
            balanceBean.setR_Opt4(options[3]);
            balanceBean.setR_Opt5(options[4]);
            balanceBean.setR_Opt6(options[5]);
            balanceBean.setR_Opt7(options[6]);
            balanceBean.setR_Opt8(options[7]);
            balanceBean.setR_Opt9("");
            balanceBean.setR_PrCuCode("");
            balanceBean.setR_Serve("");
            balanceBean.setR_PrintOK("Y");
            balanceBean.setR_KicOK("");
            balanceBean.setStkCode("");
            balanceBean.setPosStk("0");
            balanceBean.setR_PrChkType("");
            balanceBean.setR_PrQuan(0);
            balanceBean.setR_PrSubType("");
            balanceBean.setR_PrSubCode("");
            balanceBean.setR_PrSubQuan(0);
            balanceBean.setR_PrSubDisc(0);
            balanceBean.setR_PrSubBath(0);
            balanceBean.setR_PrSubAmt(0);
            balanceBean.setR_PrSubAdj(0);
            balanceBean.setR_PrCuDisc(0);
            balanceBean.setR_PrCuBath(0);
            balanceBean.setR_PrCuAdj(0);
            balanceBean.setR_QuanCanDisc(itemDetail.getQuantity());
            balanceBean.setR_Order("0");
            balanceBean.setR_PItemNo(0);
            balanceBean.setR_PKicQue(0);
            balanceBean.setR_MemSum("N");
            balanceBean.setR_MoveItem("N");
            balanceBean.setR_MoveFrom("");
            balanceBean.setR_MoveUser("");
            balanceBean.setR_MoveFlag("0");
            balanceBean.setR_MovePrint("N");
            balanceBean.setR_Pause("P");
            balanceBean.setR_Pickup("P");
            balanceBean.setR_VoidQuan(itemDetail.getQuantity());
            balanceBean.setR_PrVcAmt(0);
            balanceBean.setR_PrVcAdj(0);

            listBalance.add(balanceBean);
        }

        return listBalance;
    }

    private int saveBalance(List<BalanceBean> balanceList) {
        String sql = "INSERT INTO balance "
                + "(R_Index, R_Table, R_Date, R_Time, Macno, Cashier, R_Emp, R_PluCode, R_PName, R_Unit, "
                + "R_Group, R_Status, R_Normal, R_Discount, R_Service, R_Stock, R_Set, R_Vat, R_Type, R_ETD, "
                + "R_Quan, R_Price, R_Total, R_PrType, R_PrCode, R_PrDisc, R_PrBath, R_PrAmt, R_DiscBath, R_PrCuType, "
                + "R_PrCuQuan, R_PrCuAmt, R_Redule, R_Kic, R_KicPrint, R_Void, R_VoidUser, R_VoidTime, "
                + "R_Opt1, R_Opt2, R_Opt3, R_Opt4, R_Opt5, R_Opt6, R_Opt7, R_Opt8, R_Opt9, "
                + "R_PrCuCode, R_Serve, R_PrintOK, R_KicOK, StkCode, PosStk, R_PrChkType, R_PrQuan, "
                + "R_PrSubType, R_PrSubCode, R_PrSubQuan, R_PrSubDisc, R_PrSubBath, R_PrSubAmt, R_PrSubAdj, "
                + "R_PrCuDisc, R_PrCuBath, R_PrCuAdj, R_QuanCanDisc, R_Order, R_PItemNo, R_PKicQue, R_MemSum, "
                + "R_PrVcType, R_PrVcCode, R_PrVcAmt, R_PrVcAdj, "
                + "R_MoveItem, R_MoveFrom, R_MoveUser, R_MoveFlag, R_MovePrint, R_Pause, "
                + "R_EmpName, R_VoidQuan, R_TranType, R_FreeCode, TranType, R_SPIndex, R_LinkIndex, R_VoidPause, "
                + "R_PrintItemBill, R_CountTime, R_PEName, "
                + "R_EOpt1, R_EOpt2, R_EOpt3, R_EOpt4, R_EOpt5, R_EOpt6, R_EOpt7, R_EOpt8, R_EOpt9, "
                + "R_Course, R_Pickup, R_CallWait, R_CardPay, VoidMsg, SoneCode, R_Earn, R_EarnNo, "
                + "PDAPrintCheck, PDAEmp, PDAPrintCheckStation, PDAPrintChekItemStation) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?,?,?,?)";

        if (balanceList.isEmpty()) {
            return 0;
        }

        mysql.open();
        try (PreparedStatement ps = mysql.getConnection().prepareStatement(sql)) {
            for (BalanceBean b : balanceList) {
                ps.setString(1, b.getR_Index());
                ps.setString(2, b.getR_Table());
                if (b.getR_Date() != null) {
                    ps.setDate(3, new java.sql.Date(b.getR_Date().getTime()));
                } else {
                    ps.setNull(3, Types.DATE);
                }
                ps.setString(4, b.getR_Time());
                ps.setString(5, b.getMacno());
                ps.setString(6, b.getCashier());
                ps.setString(7, b.getR_Emp());
                ps.setString(8, b.getR_PluCode());
                ps.setString(9, b.getR_PName());
                ps.setString(10, b.getR_Unit());
                ps.setString(11, b.getR_Group());
                ps.setString(12, b.getR_Status());
                ps.setString(13, b.getR_Normal());
                ps.setString(14, b.getR_Discount());
                ps.setString(15, b.getR_Service());
                ps.setString(16, b.getR_Stock());
                ps.setString(17, b.getR_Set());
                ps.setString(18, b.getR_Vat());
                ps.setString(19, b.getR_Type());
                ps.setString(20, b.getR_ETD());
                ps.setDouble(21, b.getR_Quan());
                ps.setDouble(22, b.getR_Price());
                ps.setDouble(23, b.getR_Total());
                ps.setString(24, b.getR_PrType());
                ps.setString(25, b.getR_PrCode());
                ps.setDouble(26, b.getR_PrDisc());
                ps.setDouble(27, b.getR_PrBath());
                ps.setDouble(28, b.getR_PrAmt());
                ps.setDouble(29, b.getR_DiscBath());
                ps.setString(30, b.getR_PrCuType());
                ps.setDouble(31, b.getR_PrCuQuan());
                ps.setDouble(32, b.getR_PrCuAmt());
                ps.setDouble(33, b.getR_Redule());
                ps.setString(34, b.getR_Kic());
                ps.setString(35, b.getR_KicPrint());
                ps.setString(36, b.getR_Void());
                ps.setString(37, b.getR_VoidUser());
                ps.setString(38, b.getR_VoidTime());
                ps.setString(39, b.getR_Opt1());
                ps.setString(40, b.getR_Opt2());
                ps.setString(41, b.getR_Opt3());
                ps.setString(42, b.getR_Opt4());
                ps.setString(43, b.getR_Opt5());
                ps.setString(44, b.getR_Opt6());
                ps.setString(45, b.getR_Opt7());
                ps.setString(46, b.getR_Opt8());
                ps.setString(47, b.getR_Opt9());
                ps.setString(48, b.getR_PrCuCode());
                ps.setString(49, b.getR_Serve());
                ps.setString(50, b.getR_PrintOK());
                ps.setString(51, b.getR_KicOK());
                ps.setString(52, b.getStkCode());
                ps.setString(53, b.getPosStk());
                ps.setString(54, b.getR_PrChkType());
                ps.setDouble(55, b.getR_PrQuan());
                ps.setString(56, b.getR_PrSubType());
                ps.setString(57, b.getR_PrSubCode());
                ps.setDouble(58, b.getR_PrSubQuan());
                ps.setDouble(59, b.getR_PrSubDisc());
                ps.setDouble(60, b.getR_PrSubBath());
                ps.setDouble(61, b.getR_PrSubAmt());
                ps.setDouble(62, b.getR_PrSubAdj());
                ps.setDouble(63, b.getR_PrCuDisc());
                ps.setDouble(64, b.getR_PrCuBath());
                ps.setDouble(65, b.getR_PrCuAdj());
                ps.setDouble(66, b.getR_QuanCanDisc());
                ps.setString(67, b.getR_Order());
                ps.setInt(68, b.getR_PItemNo());
                ps.setInt(69, b.getR_PKicQue());
                ps.setString(70, b.getR_MemSum());
                ps.setString(71, b.getR_PrVcType());
                ps.setString(72, b.getR_PrVcCode());
                ps.setDouble(73, b.getR_PrVcAmt());
                ps.setDouble(74, b.getR_PrVcAdj());
                ps.setString(75, b.getR_MoveItem());
                ps.setString(76, b.getR_MoveFrom());
                ps.setString(77, b.getR_MoveUser());
                ps.setString(78, b.getR_MoveFlag());
                ps.setString(79, b.getR_MovePrint());
                ps.setString(80, b.getR_Pause());
                ps.setString(81, b.getR_EmpName());
                ps.setDouble(82, b.getR_VoidQuan());
                ps.setString(83, b.getR_TranType());
                ps.setString(84, b.getR_FreeCode());
                ps.setString(85, b.getTranType());
                ps.setString(86, b.getR_SPIndex());
                ps.setString(87, b.getR_LinkIndex());
                ps.setString(88, b.getR_VoidPause());
                ps.setString(89, b.getR_PrintItemBill());
                ps.setString(90, b.getR_CountTime());
                ps.setString(91, b.getR_PEName());
                ps.setString(92, b.getR_EOpt1());
                ps.setString(93, b.getR_EOpt2());
                ps.setString(94, b.getR_EOpt3());
                ps.setString(95, b.getR_EOpt4());
                ps.setString(96, b.getR_EOpt5());
                ps.setString(97, b.getR_EOpt6());
                ps.setString(98, b.getR_EOpt7());
                ps.setString(99, b.getR_EOpt8());
                ps.setString(100, b.getR_EOpt9());
                ps.setString(101, b.getR_Course());
                ps.setString(102, b.getR_Pickup());
                ps.setString(103, b.getR_CallWait());
                ps.setString(104, b.getR_CardPay());
                ps.setString(105, b.getVoidMsg());
                ps.setString(106, b.getSoneCode());
                ps.setString(107, b.getR_Earn());
                ps.setString(108, b.getR_EarnNo());
                ps.setString(109, b.getPDAPrintCheck());
                ps.setString(110, b.getPDAEmp());
                ps.setString(111, b.getPDAPrintCheckStation());
                ps.setString(112, b.getPDAPrintChekItemStation());
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            int total = 0;
            for (int r : results) {
                if (r > 0) {
                    total += r;
                }
            }
            return total;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        } finally {
            mysql.close();
        }
    }

    private String getIndexBalance(String tableCode) {
        String prefix = charEngCheck.charEngCheck(tableCode);
        String sql = "SELECT MAX(R_Index) AS R_Index FROM balance WHERE R_Table = ?";
        mysql.open();
        try (PreparedStatement ps = mysql.getConnection().prepareStatement(sql)) {
            ps.setString(1, tableCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String currentIndex = rs.getString("R_Index");
                    if (currentIndex != null) {
                        String[] parts = currentIndex.split("/");
                        if (parts.length >= 2) {
                            try {
                                int nextId = Integer.parseInt(parts[1]) + 1;
                                return prefix + "/" + String.format("%03d", nextId);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid R_Index format: " + currentIndex);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }
        return prefix + "/001";
    }

    private int updateTableFileSummary(RabbitMQOrderBean orderBean) {
        String sql = "UPDATE tablefile "
                + "SET TItem=?, TAmount=?, TOnAct=?, NetTotal=?, "
                + "PrintChkBill=?, ChkBill=?, TActive=? "
                + "WHERE Tcode=?";
        mysql.open();
        try (PreparedStatement ps = mysql.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderBean.getItemCount());
            ps.setDouble(2, orderBean.getTotalAmount());
            ps.setString(3, "N");
            ps.setDouble(4, orderBean.getTotalAmount());
            ps.setString(5, "N");
            ps.setString(6, "N");
            ps.setString(7, "Y");
            ps.setString(8, orderBean.getTableNumber());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        } finally {
            mysql.close();
        }
    }

    private String[] getMenuOptions(String specialInstructions) {
        String options[] = new String[8];
        options[0] = "";
        options[1] = "";
        options[2] = "";
        options[3] = "";
        options[4] = "";
        options[5] = "";
        options[6] = "";
        options[7] = "";

        String opts[] = ThaiUtil.Unicode2ASCII(specialInstructions).split(",");
        System.arraycopy(opts, 0, options, 0, opts.length);
        return options;
    }

}
