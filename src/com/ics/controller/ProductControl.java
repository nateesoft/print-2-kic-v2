package com.ics.controller;

import com.ics.model.ProductBean;
import java.sql.ResultSet;
import com.ics.process.MySQLConnect;
import com.ics.util.ThaiUtil;
import java.sql.SQLException;

/**
 *
 * @author donjai
 */
public class ProductControl {

    private final MySQLConnect mysql = new MySQLConnect();

    public ProductBean getData(String PCode) {
        String sql = "select * "
                + "from product "
                + "where PCode='" + PCode + "'";
        ProductBean productBean = new ProductBean();

        try {
            mysql.open();
            try (ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql)) {
                while (rs.next()) {
                    productBean.setPCode(rs.getString("PCode"));
                    productBean.setPFix(rs.getString("PFix"));
                    productBean.setPReferent(rs.getString("PReferent"));
                    productBean.setPAccNo(rs.getString("PAccNo"));
                    productBean.setPGroup(rs.getString("PGroup"));
                    productBean.setPVender(rs.getString("PVender"));
                    productBean.setPType(rs.getString("PType"));
                    productBean.setPNormal(rs.getString("PNormal"));
                    productBean.setPRemark(rs.getString("PRemark"));
                    productBean.setPDiscount(rs.getString("PDiscount"));
                    productBean.setPService(rs.getString("PService"));
                    productBean.setPStatus(rs.getString("PStatus"));
                    productBean.setPStock(rs.getString("PStock"));
                    productBean.setPSet(rs.getString("PSet"));
                    productBean.setPVat(rs.getString("PVat"));
                    productBean.setPDesc(ThaiUtil.ASCII2Unicode(rs.getString("PDesc")));
                    productBean.setPUnit1(ThaiUtil.ASCII2Unicode(rs.getString("PUnit1")));
                    productBean.setPPack1(rs.getInt("PPack1"));
                    productBean.setPArea(rs.getString("PArea"));
                    productBean.setPKic(rs.getString("PKic"));
                    productBean.setPPrice11(rs.getFloat("PPrice11"));
                    productBean.setPPrice12(rs.getFloat("PPrice12"));
                    productBean.setPPrice13(rs.getFloat("PPrice13"));
                    productBean.setPPrice14(rs.getFloat("PPrice14"));
                    productBean.setPPrice15(rs.getFloat("PPrice15"));
                    productBean.setPPromotion1(rs.getString("PPromotion1"));
                    productBean.setPPromotion2(rs.getString("PPromotion2"));
                    productBean.setPPromotion3(rs.getString("PPromotion3"));
                    productBean.setPMax(rs.getFloat("PMax"));
                    productBean.setPMin(rs.getFloat("PMin"));
                    productBean.setPBUnit(rs.getString("PBUnit"));
                    productBean.setPBPack(rs.getFloat("PBPack"));
                    productBean.setPLCost(rs.getFloat("PLCost"));
                    productBean.setPSCost(rs.getFloat("PSCost"));
                    productBean.setPACost(rs.getFloat("PACost"));
                    productBean.setPLink1(rs.getString("PLink1"));
                    productBean.setPLink2(rs.getString("PLink2"));
                    productBean.setPLastTime(rs.getString("PLastTime"));
                    productBean.setPUserUpdate(rs.getString("PUserUpdate"));
                    productBean.setPBarCode(rs.getString("PBarCode"));
                    productBean.setPActive(rs.getString("PActive"));
                    productBean.setPSPVat(rs.getString("PSPVat"));
                    productBean.setPSPVatAmt(rs.getFloat("PSPVatAmt"));
                    productBean.setPOSStk(rs.getString("POSStk"));
                    productBean.setMSStk(rs.getString("MSStk"));
                    productBean.setPTimeCharge(rs.getFloat("PTimeCharge"));
                    productBean.setPOrder(rs.getString("POrder"));
                    productBean.setPFoodType(rs.getString("PFoodType"));
                    productBean.setPPackOld(rs.getInt("PPackOld"));
                    productBean.setPDesc2(rs.getString("PDesc2"));
                    productBean.setPselectItem(rs.getString("PselectItem"));
                    productBean.setPselectNum(rs.getFloat("PselectNum"));
                    productBean.setPShowOption(rs.getString("PShowOption"));

                }
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        return productBean;
    }

}
