package com.crittermap.backcountrynavigator.xe.share;

import android.annotation.SuppressLint;

import com.crittermap.backcountrynavigator.xe.BuildConfig;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thutrang.dao on 4/24/18.
 */

public class Logger {
    private static final String FileName = "myLog.txt";


    // private static Tracker mTracker;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static final int FATAL = 8;
    private static final int ASSERT = 7;
    private static final int ERROR = 6;
    private static final int WARN = 5;
    private static final int INFO = 4;
    private static final int DEBUG = 3;
    private static final int VERBOSE = 2;
    private static final boolean IN_DEVELOPMENT = true;
    private static final String TAG = "bcnavxe";

    private static void write(String Tag, int debugLevel, String message, Throwable ex) {
        if (!BuildConfig.DEBUG) {
            if (debugLevel < ERROR) {
                return;
            }
        }
        FileWriter filewriter = null;
        try {
            BC_Helper.createFolder("LOGS");
            File file = new File(BC_Helper.getSubFolderPath("LOGS"), FileName);

            // if (file.canWrite()) {
            filewriter = new FileWriter(file, true);

            StringBuffer sb = new StringBuffer();

            Date now = new Date();

            sb.append(dateFormat.format(now)).append(",  ");

            switch (debugLevel) {
                case VERBOSE:
                    sb.append("VERBOSE,");
                    break;
                case DEBUG:
                    sb.append("DEBUG,");
                    break;
                case INFO:
                    sb.append("INFO,");
                    break;
                case WARN:
                    sb.append("WARN,");
                    break;
                case ERROR:
                    sb.append("ERROR,");
                    break;
                case ASSERT:
                    sb.append("ASSERT,");
                    break;
                case FATAL:
                    sb.append("FATAL,");
                    break;

            }

            if (Tag != null) {
                if (Tag.contains(",")) {
                    sb.append("\"").append(Tag).append("\",");
                } else {
                    sb.append(Tag).append(",");
                }
            } else {
                sb.append(",");
            }
            if (message != null) {
                if (message.contains(",")) {
                    sb.append("\"").append(message).append("\",");
                } else {
                    sb.append(message).append(",");
                }
            } else {
                sb.append(",");
            }
            if (ex != null) {
                sb.append("\"").append(android.util.Log.getStackTraceString(ex)).append("\"");
            } else {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("\r\n");
            filewriter.write(sb.toString());
            // }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Could not write file " + e.getMessage(), e);
        } finally {
            if (filewriter != null) {
                try {
                    filewriter.close();
                } catch (IOException e) {
                    android.util.Log.v(TAG, e.getMessage(), e);
                }
            }
        }
    }

    public static void v(String Tag, String message, Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(Tag, message, ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(Tag, VERBOSE, message, ex);
    }

    public static void v(String Tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(Tag, message);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(Tag, VERBOSE, message, null);
    }

    public static void v(String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, message);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, VERBOSE, message, null);
    }

    public static void v(Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, ex.getMessage(), ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, VERBOSE, ex.getMessage(), ex);
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag, msg);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, WARN, msg, null);
    }

    public static void w(String tag, String msg, Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag, msg, ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, WARN, msg, ex);
    }

    public static void w(Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v("bcnavxe", ex.getMessage(), ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, WARN, ex.getMessage(), ex);
    }

    public static void e(String tag, String msg, Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, msg, ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, ERROR, msg, ex);
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, msg);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, ERROR, msg, null);
    }

    public static void e(Throwable ex) {
        /*
         * if(mTracker!= null) mTracker.send(new HitBuilders.ExceptionBuilder()
         * .setDescription(ex.getMessage()) .setFatal(false) .build());
         */
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, ex.getMessage(), ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, ERROR, ex.getMessage(), ex);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, msg);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, DEBUG, msg, null);

    }

    public static void d(String tag, String msg, Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, msg, ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, DEBUG, msg, ex);
    }

    public static void d(Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, ex.getMessage(), ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, DEBUG, ex.getMessage(), ex);
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, msg, null);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, INFO, msg, null);

    }

    public static void i(String tag, String msg, Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, msg, null);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(tag, INFO, msg, ex);

    }

    public static void i(Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, ex.getMessage(), ex);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, INFO, ex.getMessage(), ex);
    }

    public static String getStackTraceString(Exception ex) {
        // TODO Auto-generated method stub
        return android.util.Log.getStackTraceString(ex);
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(TAG, msg);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, INFO, msg, null);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(TAG, msg);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, DEBUG, msg, null);
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(TAG, msg);
        }
        if (!Config.DEBUG_ENABLE)
            return;

        write(TAG, ERROR, msg, null);
    }

    private static class Config {
        private static boolean DEBUG_ENABLE = true;
    }
}
