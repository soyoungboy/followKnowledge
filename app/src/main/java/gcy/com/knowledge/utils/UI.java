package gcy.com.knowledge.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import gcy.com.knowledge.KnowledgeApp;

/**
 * ============================================================
 * <p/>
 * 版     权 ： keyboard3 所有
 * <p/>
 * 作     者  :  甘春雨
 * <p/>
 * 版     本 ： 1.0
 * <p/>
 * 创 建日期 ： 2016/3/4
 * <p/>
 * 描     述 ：
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public class UI {
    private static Context context= KnowledgeApp.context;
    public static void showSnack(View rootView,int textId){
        Snackbar.make(rootView,context.getString(textId),Snackbar.LENGTH_SHORT).show();
    }
    public static void showSnackLong(View rootView,int textId){
        Snackbar.make(rootView,context.getString(textId),Snackbar.LENGTH_LONG).show();
    }
}
