/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysql.testconnection;

/**
 *
 * @author nathee
 */
public class Test {
    public static void main(String[] args) {
        for(int i=1;i<=74;i++){
//            System.out.println("posUser.setSale"+i+"(rs.getString(\"sale"+i+"\"));");
//            System.out.println("posUser.setCont"+i+"(rs.getString(\"cont"+i+"\"));");
            System.out.println("posUser.setStock"+i+"(rs.getString(\"Stock"+i+"\"));");
        }
    }
}
