package com.ics.process;

import com.ics.constant.Value;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLConnect {

    private Connection con = null;
    public static String HostName = null;
    public static String DbName = null;
    public static String UserName = null;
    public static String Password = null;
    public static String PortNumber = null;
    public static String CharSet = "utf-8";

    private String msgError = "พบการเชื่อมต่อมีปัญหา ไม่สามารถดำเนินการต่อได้\nท่านต้องการปิดโปรแกรมอัตโนมัติหรือไม่ ?";
    private Class clazz = null;

    static {
        try {
            FileInputStream fs = new FileInputStream(Value.FILE_CONFIG);
            DataInputStream ds = new DataInputStream(fs);
            BufferedReader br = new BufferedReader(new InputStreamReader(ds));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                String[] data = tmp.split(",", tmp.length());
                if (data[0].equalsIgnoreCase("server")) {
                    HostName = data[1];
                } else if (data[0].equalsIgnoreCase("database")) {
                    DbName = data[1];
                    Value.DATABASE = data[1];
                } else if (data[0].equalsIgnoreCase("user")) {
                    UserName = data[1];
                } else if (data[0].equalsIgnoreCase("pass")) {
                    Password = data[1];
                } else if (data[0].equalsIgnoreCase("port")) {
                    PortNumber = data[1];
                } else if (data[0].equalsIgnoreCase("charset")) {
                    CharSet = data[1];
                } else if (data[0].equalsIgnoreCase("macno")) {
                    Value.MACNO = data[1];
                } else if (data[0].equalsIgnoreCase("language")) {
                    Value.LANG = data[1];
                } else if (data[0].equalsIgnoreCase("db_member")) {
                    Value.db_member = data[1];
                } else if (data[0].equalsIgnoreCase("useprint")) {
                    try {
                        Value.useprint = Boolean.parseBoolean(data[1]);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        Value.useprint = false;
                    }
                } else if (data[0].equalsIgnoreCase("printkic")) {
                    try {
                        Value.printkic = Boolean.parseBoolean(data[1]);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        Value.printkic = false;
                    }
                } else if (data[0].equalsIgnoreCase("autoqty")) {
                    try {
                        Value.autoqty = Boolean.parseBoolean(data[1]);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        Value.autoqty = false;
                    }
                } else if (data[0].equalsIgnoreCase("printdriver")) {
                    try {
                        Value.printdriver = Boolean.parseBoolean(data[1]);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        Value.printdriver = false;
                    }
                } else if (data[0].equalsIgnoreCase("printerName")) {
                    Value.printerDriverName = data[1];
                }
            }
            br.close();
            ds.close();
            fs.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public String getMsgError() {
        return msgError;
    }

    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }

    public void open() {
        try {
            close();
            Thread.sleep(2);
        } catch (InterruptedException e) {
        }
        if (!MySQLConstants.MYSQL_CONNECT.isEmpty()) {
            close();
        }

        if (con == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://" + HostName + ":" + PortNumber + "/" + DbName + "?characterEncoding=utf-8", UserName, Password);
                MySQLConstants.MYSQL_CONNECT.put(con.hashCode(), this.clazz);
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println(e.getMessage());
                System.exit(0);
            }
        }
    }

    public Connection getConnection() {
        return con;
    }

    public void close() {
        if (con != null) {
            try {
                con.close();
                MySQLConstants.MYSQL_CONNECT.remove(con.hashCode());
            } catch (SQLException ex) {
                Logger.getLogger(MySQLConnect.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                con = null; // Fix: reset เสมอ เพื่อให้ open() สามารถเปิด connection ใหม่ได้ถูกต้อง
            }
        }
    }

    /**
     * ตรวจสอบว่า connection ปัจจุบันยังใช้งานได้อยู่หรือไม่
     * ใช้ isValid() ของ JDBC ซึ่งส่ง ping ไปยัง DB จริง (timeout 2 วินาที)
     * @return 
     */
    public boolean isAlive() {
        if (con == null) {
            return false;
        }
        try {
            return !con.isClosed() && con.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * พยายาม connect ใหม่โดยไม่เรียก System.exit() เมื่อล้มเหลว
     * คืนค่า true ถ้า connect สำเร็จ, false ถ้าล้มเหลว
     * ใช้ใน loop หลักเพื่อตรวจสอบ DB ก่อนดำเนินการอื่น
     * @return 
     */
    public boolean tryOpen() {
        try {
            if (!MySQLConstants.MYSQL_CONNECT.isEmpty()) {
                close();
            }
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://" + HostName + ":" + PortNumber + "/" + DbName + "?characterEncoding=utf-8",
                UserName, Password
            );
            MySQLConstants.MYSQL_CONNECT.put(con.hashCode(), clazz);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("DB tryOpen failed: " + e.getMessage());
            con = null;
            return false;
        }
    }

}
