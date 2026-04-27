package com.ics.util;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppLogUtil {

    private static final Logger LOGGER = Logger.getLogger("AppLogger");
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String LOG_DIR = "logs";

    private static FileHandler fh = null;
    private static String currentDate = null;

    static {
        // ไม่ส่งซ้ำไปที่ root console handler เพื่อหลีกเลี่ยง duplicate output
        LOGGER.setUseParentHandlers(false);
    }

    private static synchronized void ensureHandler() {
        String today = DATE_FMT.format(new Date());
        if (fh != null && today.equals(currentDate)) {
            return;
        }
        // rotate: ปิด handler วันเก่าแล้วเปิดใหม่
        if (fh != null) {
            LOGGER.removeHandler(fh);
            fh.close();
            fh = null;
        }
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fh = new FileHandler(LOG_DIR + File.separator + "application-" + today + ".log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            currentDate = today;
        } catch (IOException ex) {
            System.err.println("AppLogUtil: cannot open log file — " + ex.getMessage());
        }
    }

    public static void log(Class<?> t, String type, Exception e) {
        ensureHandler();
        String msg = (e.getMessage() != null ? e.getMessage() : e.getClass().getName());
        int line = e.getStackTrace().length > 0 ? e.getStackTrace()[0].getLineNumber() : -1;
        String formatted = "[" + t.getSimpleName() + ":" + line + "] " + msg;
        switch (type) {
            case "error":   LOGGER.severe(formatted);  break;
            case "warning": LOGGER.warning(formatted); break;
            default:        LOGGER.info(formatted);    break;
        }
    }

    public static void log(Class<?> t, String type, String message) {
        ensureHandler();
        String formatted = "[" + t.getSimpleName() + "] " + message;
        switch (type) {
            case "error":   LOGGER.severe(formatted);  break;
            case "warning": LOGGER.warning(formatted); break;
            default:        LOGGER.info(formatted);    break;
        }
    }

    public static void info(Class<?> t, String message)  { log(t, "info",    message); }
    public static void warn(Class<?> t, String message)  { log(t, "warning", message); }
    public static void error(Class<?> t, String message) { log(t, "error",   message); }
    public static void error(Class<?> t, Exception e)    { log(t, "error",   e);       }

    public static void htmlFile(String htmlText) {
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File f = new File(LOG_DIR + File.separator + "source_" + timestamp + ".html");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(htmlText);
                bw.flush();
            }
        } catch (IOException e) {
            System.err.println("AppLogUtil: cannot write html file — " + e.getMessage());
        }
    }
}
