package com.ics.process;

import java.sql.SQLException;

/**
 *
 * @author nathee
 */
public class DatabaseConnection {

    public boolean execUpdate(String sql) {
        boolean isValid = false;
        MySQLConnect mysql = new MySQLConnect();
        mysql.open();
        try {
            int result = mysql.getConnection().createStatement().executeUpdate(sql);
            isValid = result > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mysql.close();
        }

        return isValid;
    }

}
