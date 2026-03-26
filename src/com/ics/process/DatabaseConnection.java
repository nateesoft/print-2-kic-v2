package com.ics.process;

import java.sql.SQLException;

/**
 *
 * @author nathee
 */
public class DatabaseConnection {

    private final MySQLConnect mysqlLocal = new MySQLConnect();

    public boolean execUpdate(String sql) {
        boolean isValid = false;
        mysqlLocal.open();
        try {
            int result = mysqlLocal.getConnection().createStatement().executeUpdate(sql);
            isValid = result > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysqlLocal.close();
        }

        return isValid;
    }

}
