package com.ics.constant;

public class OSValidator{

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        //windows
        return (os.contains("win"));
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        //Mac
        return (os.contains("mac"));
    }

    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.contains("nix"));
    }
    
    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.contains("nux"));
    }
}
