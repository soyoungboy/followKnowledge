package gcy.com.knowledge;

import android.app.Application;
import android.content.Context;

import gcy.com.knowledge.net.DB;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
public class KnowledgeApp extends Application {
    /**
     * 全局的上下文变量
     */
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        setupRealm();
    }
    private void setupRealm() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
