package com.ics.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.ics.process.MySQLConnect;
import com.ics.model.ProductBean;
import com.ics.util.ThaiUtil;

public class ProductControl {

    private List<ProductBean> dataProduct = null;
    public static final int PRODUCT_NOT_FOUND = 0;
    public static final int PRODUCT_ACTIVE = 1;
    public static final int PRODUCT_NOT_ACTIVE = 2;

    private List<ProductBean> listAll = null;

    public ProductControl() {
        dataProduct = new ArrayList<>();
    }

    public ProductBean getProductCodeArray(String pCode) {
        if (pCode == null) {
            return new ProductBean();
        }
        if (listAll != null) {
            for (int i = 0; i < listAll.size(); i++) {
                ProductBean bean = listAll.get(i);
                if (pCode.trim().equals(bean.getPCode())) {
                    return bean;
                }
            }
        }

        return new ProductBean();
    }

    public ProductBean getProductBarCodeArray(String barCode) {
        if (barCode == null) {
            return new ProductBean();
        }
        if (listAll != null) {
            for (int i = 0; i < listAll.size(); i++) {
                ProductBean bean = listAll.get(i);
                if (barCode.trim().equals(bean.getPBarCode())) {
                    return bean;
                }
            }
        }

        return new ProductBean();
    }

    public List<ProductBean> initLoadProductActive() {
        if (listAll == null) {
            listAll = new ArrayList<>();
            MySQLConnect mysql = new MySQLConnect();
            try {
                mysql.open(ProductControl.class);
                ResultSet rs = mysql.getConnection().createStatement().executeQuery("select * from product where pactive='Y'");
                while (rs.next()) {
                    ProductBean bean = new ProductBean();
                    bean.setPCode(rs.getString("PCode"));
                    bean.setPFix(rs.getString("PFix"));
                    bean.setPReferent(rs.getString("PReferent"));
                    bean.setPAccNo(rs.getString("PAccNo"));
                    bean.setPGroup(rs.getString("PGroup"));
                    bean.setPVender(rs.getString("PVender"));
                    bean.setPType(rs.getString("PType"));
                    bean.setPNormal(rs.getString("PNormal"));
                    bean.setPRemark(rs.getString("PRemark"));
                    bean.setPDiscount(rs.getString("PDiscount"));
                    bean.setPService(rs.getString("PService"));
                    bean.setPStatus(rs.getString("PStatus"));
                    bean.setPStock(rs.getString("PStock"));
                    bean.setPSet(rs.getString("PSet"));
                    bean.setPVat(rs.getString("PVat"));
                    bean.setPDesc(ThaiUtil.ASCII2Unicode(rs.getString("PDesc")));
                    bean.setPUnit1(ThaiUtil.ASCII2Unicode(rs.getString("PUnit1")));
                    bean.setPPack1(rs.getInt("PPack1"));
                    bean.setPArea(rs.getString("PArea"));
                    bean.setPKic(rs.getString("PKic"));
                    bean.setPPrice11(rs.getFloat("PPrice11"));
                    bean.setPPrice12(rs.getFloat("PPrice12"));
                    bean.setPPrice13(rs.getFloat("PPrice13"));
                    bean.setPPrice14(rs.getFloat("PPrice14"));
                    bean.setPPrice15(rs.getFloat("PPrice15"));
                    bean.setPPromotion1(rs.getString("PPromotion1"));
                    bean.setPPromotion2(rs.getString("PPromotion2"));
                    bean.setPPromotion3(rs.getString("PPromotion3"));
                    bean.setPMax(rs.getFloat("PMax"));
                    bean.setPMin(rs.getFloat("PMin"));
                    bean.setPBUnit(rs.getString("PBUnit"));
                    bean.setPBPack(rs.getFloat("PBPack"));
                    bean.setPLCost(rs.getFloat("PLCost"));
                    bean.setPSCost(rs.getFloat("PSCost"));
                    bean.setPACost(rs.getFloat("PACost"));
                    bean.setPLink1(rs.getString("PLink1"));
                    bean.setPLink2(rs.getString("PLink2"));
                    bean.setPLastTime(rs.getString("PLastTime"));
                    bean.setPUserUpdate(rs.getString("PUserUpdate"));
                    bean.setPBarCode(rs.getString("PBarCode"));
                    bean.setPActive(rs.getString("PActive"));
                    bean.setPSPVat(rs.getString("PSPVat"));
                    bean.setPSPVatAmt(rs.getFloat("PSPVatAmt"));
                    bean.setPOSStk(rs.getString("POSStk"));
                    bean.setMSStk(rs.getString("MSStk"));
                    bean.setPTimeCharge(rs.getFloat("PTimeCharge"));
                    bean.setPOrder(rs.getString("POrder"));
                    bean.setPFoodType(rs.getString("PFoodType"));
                    bean.setPPackOld(rs.getInt("PPackOld"));
                    bean.setPDesc2(rs.getString("PDesc2"));
                    bean.setPselectItem(rs.getString("PselectItem"));
                    bean.setPselectNum(rs.getFloat("PselectNum"));
                    bean.setPShowOption(rs.getString("PShowOption"));
                    bean.setPEDesc(rs.getString("PEDesc"));

                    listAll.add(bean);
                }
                rs.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            } finally {
                mysql.closeConnection(this.getClass());
            }
        }

        return listAll;
    }

    public ProductBean getData(String PCode) {
        String sql = "select * from product where PCode='" + PCode + "' limit 1";
        ProductBean productBean = new ProductBean();
        /**
         * * OPEN CONNECTION **
         */
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(ProductControl.class);
        try {
            ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql);
            if (rs.next()) {
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
                productBean.setPEDesc(rs.getString("PEDesc"));
            }
            rs.close();
//            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return productBean;
    }

    public List<ProductBean> searchAllProductBy(String PCode) {
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(ProductControl.class);
        try {
            PCode = ThaiUtil.Unicode2ASCII(PCode);
            String sql = "select * from product "
                    + "where PCode like '%" + PCode + "%' "
                    + "or PDesc like '%" + PCode + "%'";
            ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql);
            ProductBean p;
            while (rs.next()) {
                p = new ProductBean();
                p.setPCode(rs.getString("PCode"));
                p.setPFix(rs.getString("PFix"));
                p.setPReferent(rs.getString("PReferent"));
                p.setPAccNo(rs.getString("PAccNo"));
                p.setPGroup(rs.getString("PGroup"));
                p.setPVender(rs.getString("PVender"));
                p.setPType(rs.getString("PType"));
                p.setPNormal(rs.getString("PNormal"));
                p.setPRemark(rs.getString("PRemark"));
                p.setPDiscount(rs.getString("PDiscount"));
                p.setPService(rs.getString("PService"));
                p.setPStatus(rs.getString("PStatus"));
                p.setPStock(rs.getString("PStock"));
                p.setPSet(rs.getString("PSet"));
                p.setPVat(rs.getString("PVat"));
                p.setPDesc(ThaiUtil.ASCII2Unicode(rs.getString("PDesc")));
                p.setPUnit1(ThaiUtil.ASCII2Unicode(rs.getString("PUnit1")));
                p.setPPack1(rs.getInt("PPack1"));
                p.setPArea(rs.getString("PArea"));
                p.setPKic(rs.getString("PKic"));
                p.setPPrice11(rs.getFloat("PPrice11"));
                p.setPPrice12(rs.getFloat("PPrice12"));
                p.setPPrice13(rs.getFloat("PPrice13"));
                p.setPPrice14(rs.getFloat("PPrice14"));
                p.setPPrice15(rs.getFloat("PPrice15"));
                p.setPPromotion1(rs.getString("PPromotion1"));
                p.setPPromotion2(rs.getString("PPromotion2"));
                p.setPPromotion3(rs.getString("PPromotion3"));
                p.setPMax(rs.getFloat("PMax"));
                p.setPMin(rs.getFloat("PMin"));
                p.setPBUnit(rs.getString("PBUnit"));
                p.setPBPack(rs.getFloat("PBPack"));
                p.setPLCost(rs.getFloat("PLCost"));
                p.setPSCost(rs.getFloat("PSCost"));
                p.setPACost(rs.getFloat("PACost"));
                p.setPLink1(rs.getString("PLink1"));
                p.setPLink2(rs.getString("PLink2"));
                p.setPLastTime(rs.getString("PLastTime"));
                p.setPUserUpdate(rs.getString("PUserUpdate"));
                p.setPBarCode(rs.getString("PBarCode"));
                p.setPActive(rs.getString("PActive"));
                p.setPSPVat(rs.getString("PSPVat"));
                p.setPSPVatAmt(rs.getFloat("PSPVatAmt"));
                p.setPOSStk(rs.getString("POSStk"));
                p.setMSStk(rs.getString("MSStk"));
                p.setPTimeCharge(rs.getFloat("PTimeCharge"));
                p.setPOrder(rs.getString("POrder"));
                p.setPFoodType(rs.getString("PFoodType"));
                p.setPPackOld(rs.getInt("PPackOld"));
                p.setPDesc2(rs.getString("PDesc2"));
                p.setPselectItem(rs.getString("PselectItem"));
                p.setPselectNum(rs.getFloat("PselectNum"));
                p.setPShowOption(rs.getString("PShowOption"));
                p.setPEDesc(rs.getString("PEName"));

                dataProduct.add(p);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return dataProduct;
    }

    public List<ProductBean> searchAllProductBy3(String key) {
        List<ProductBean> arrList = new ArrayList<>();
        if (key.trim().equals("")) {
            return arrList;
        }

        MySQLConnect mysql = new MySQLConnect();
        mysql.open(ProductControl.class);
        try {
            key = ThaiUtil.Unicode2ASCII(key);
            String sql = "select MenuItem, PCode, PGroup, PDesc, PUnit1, "
                    + "PPrice11, PPrice12, PPrice13, PPrice14, PPrice15 "
                    + "from product p, menulist m "
                    + "where p.PCode=m.PLUCode "
                    + "and PActive='Y' "
                    + "and PFix = 'F' "
                    + "and MenuActive='Y' "
                    + "and MenuItem like '%" + key + "%' "
                    + "order by MenuItem "
                    + "limit 0,20";
            Statement stmt = mysql.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ProductBean product = new ProductBean();
                product.setPCode(rs.getString("PCode"));
                product.setPGroup(rs.getString("PGroup"));
                product.setPDesc(ThaiUtil.ASCII2Unicode(rs.getString("PDesc")));
                product.setPUnit1(ThaiUtil.ASCII2Unicode(rs.getString("PUnit1")));
                product.setPPrice11(rs.getDouble("PPrice11"));
                product.setPPrice12(rs.getDouble("PPrice12"));
                product.setPPrice13(rs.getDouble("PPrice13"));
                product.setPPrice14(rs.getDouble("PPrice14"));
                product.setPPrice15(rs.getDouble("PPrice15"));
                product.setPPathName("");
                product.setPDesc2(rs.getString("MenuItem"));

                arrList.add(product);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return arrList;
    }

    public List<ProductBean> getAllProductByGroup(String PGroup) {
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(ProductControl.class);
        try {
            String sql = "select * from product where PGroup ='" + PGroup + "'";
            ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql);
            ProductBean p;
            while (rs.next()) {
                p = new ProductBean();
                p.setPCode(rs.getString("PCode"));
                p.setPFix(rs.getString("PFix"));
                p.setPReferent(rs.getString("PReferent"));
                p.setPAccNo(rs.getString("PAccNo"));
                p.setPGroup(rs.getString("PGroup"));
                p.setPVender(rs.getString("PVender"));
                p.setPType(rs.getString("PType"));
                p.setPNormal(rs.getString("PNormal"));
                p.setPRemark(rs.getString("PRemark"));
                p.setPDiscount(rs.getString("PDiscount"));
                p.setPService(rs.getString("PService"));
                p.setPStatus(rs.getString("PStatus"));
                p.setPStock(rs.getString("PStock"));
                p.setPSet(rs.getString("PSet"));
                p.setPVat(rs.getString("PVat"));
                p.setPDesc(ThaiUtil.ASCII2Unicode(rs.getString("PDesc")));
                p.setPUnit1(ThaiUtil.ASCII2Unicode(rs.getString("PUnit1")));
                p.setPPack1(rs.getInt("PPack1"));
                p.setPArea(rs.getString("PArea"));
                p.setPKic(rs.getString("PKic"));
                p.setPPrice11(rs.getFloat("PPrice11"));
                p.setPPrice12(rs.getFloat("PPrice12"));
                p.setPPrice13(rs.getFloat("PPrice13"));
                p.setPPrice14(rs.getFloat("PPrice14"));
                p.setPPrice15(rs.getFloat("PPrice15"));
                p.setPPromotion1(rs.getString("PPromotion1"));
                p.setPPromotion2(rs.getString("PPromotion2"));
                p.setPPromotion3(rs.getString("PPromotion3"));
                p.setPMax(rs.getFloat("PMax"));
                p.setPMin(rs.getFloat("PMin"));
                p.setPBUnit(rs.getString("PBUnit"));
                p.setPBPack(rs.getFloat("PBPack"));
                p.setPLCost(rs.getFloat("PLCost"));
                p.setPSCost(rs.getFloat("PSCost"));
                p.setPACost(rs.getFloat("PACost"));
                p.setPLink1(rs.getString("PLink1"));
                p.setPLink2(rs.getString("PLink2"));
                p.setPLastTime(rs.getString("PLastTime"));
                p.setPUserUpdate(rs.getString("PUserUpdate"));
                p.setPBarCode(rs.getString("PBarCode"));
                p.setPActive(rs.getString("PActive"));
                p.setPSPVat(rs.getString("PSPVat"));
                p.setPSPVatAmt(rs.getFloat("PSPVatAmt"));
                p.setPOSStk(rs.getString("POSStk"));
                p.setMSStk(rs.getString("MSStk"));
                p.setPTimeCharge(rs.getFloat("PTimeCharge"));
                p.setPOrder(rs.getString("POrder"));
                p.setPFoodType(rs.getString("PFoodType"));
                p.setPPackOld(rs.getInt("PPackOld"));
                p.setPDesc2(rs.getString("PDesc2"));
                p.setPselectItem(rs.getString("PselectItem"));
                p.setPselectNum(rs.getFloat("PselectNum"));
                p.setPShowOption(rs.getString("PShowOption"));

                dataProduct.add(p);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return dataProduct;
    }

    public boolean productExist(String PCode) {
        boolean isExist = false;
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(ProductControl.class);
        try {
            String sql = "select PCode from product where PCode='" + PCode + "' limit 1";
            ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql);
            if (rs.next()) {
                isExist = true;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return isExist;
    }

    public boolean productOutStock(String PCode) {
        boolean isExist = false;
        /**
         * * OPEN CONNECTION **
         */
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(ProductControl.class);
        try {
            String sql = "select PCode from outstocklist where PCode='" + PCode + "' limit 1";
            ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql);
            if (rs.next()) {
                isExist = true;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(this.getClass());
        }

        return isExist;
    }

    public static boolean checkProductItem(String menuCode) {
        boolean isProduct = false;
        MySQLConnect mysql = new MySQLConnect();
        mysql.open(ProductControl.class);
        try {
            String sql = "select pcode from soft_menusetup where menucode='" + menuCode + "' "
                    + "and pcode<>'' limit 1;";
            try (ResultSet rs = mysql.getConnection().createStatement().executeQuery(sql)) {
                if (rs.next()) {
                    isProduct = true;
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.closeConnection(ProductControl.class);
        }

        return isProduct;
    }

}
