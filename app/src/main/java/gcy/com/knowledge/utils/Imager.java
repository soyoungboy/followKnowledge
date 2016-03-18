package gcy.com.knowledge.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
public class Imager {
    public static void load(Context context, String url, ImageView view) {
        Glide.with(context.getApplicationContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(view);
    }

    public static void load( String url, int animationId, ImageView view) {
        Glide.with(KnowledgeApp.context)
                .load(url)
                .animate(animationId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

}
