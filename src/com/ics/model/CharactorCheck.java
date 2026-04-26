package com.ics.model;

/**
 *
 * @author Dell-Softpos
 */
public class CharactorCheck {

    public String charEngCheck(String table) {
        if (table == null) {
            return table;
        }
        
        if (table.matches(".*[a-z].*")) {
            table = table.toUpperCase();
            System.out.println(table);
        }
        return table;
    }

}
