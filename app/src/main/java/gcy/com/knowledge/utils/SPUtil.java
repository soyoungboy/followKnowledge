package gcy.com.knowledge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import gcy.com.knowledge.KnowledgeApp;

/**
 * ============================================================
 * <p>
 * 版     权 ： keyboard3 所有
 * <p>
 * 作     者  :  甘春雨
 * <p>
 * 版     本 ： 1.0
 * <p>
 * 创 建日期 ： 2016/3/1
 * <p>
 * 描     述 ：
 * <p>
 * <p>
 * 修 订 历史：
 * <p>
 * ============================================================
 */
public  class SPUtil {
    private static Context context= KnowledgeApp.context;
    private static SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

    public static String get(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }
    public static String getString(String key) {
        return sp.getString(key, "");
    }

    public static void save(String splash, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(splash,value);
        editor.apply();
    }
    public static void save(String splash, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(splash,value);
        editor.apply();
    }
    public static void save(String splash, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(splash,value);
        editor.apply();
    }
    public static int getInt(String key) {
        return sp.getInt(key, 0);
    }
    public static boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }
}
