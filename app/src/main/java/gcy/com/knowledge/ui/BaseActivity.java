package gcy.com.knowledge.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import butterknife.ButterKnife;
import gcy.com.knowledge.R;
import gcy.com.knowledge.net.DB;
import io.realm.Realm;

/**
 * ============================================================
 * <p/>
 * 版     权 ： keyboard3 所有
 * <p/>
 * 作     者  :  甘春雨
 * <p/>
 * 版     本 ： 1.0
 * <p/>
 * 创 建日期 ： 2016/3/2
 * <p/>
 * 描     述 ：
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }
    public abstract int initLayoutId();
    protected void initViews(){
        int layoutId=initLayoutId();
        setContentView(layoutId);
        initAppBar();
        ButterKnife.bind(this);
        DB.realm = Realm.getDefaultInstance();
    }
    private void initAppBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            assert getSupportActionBar() != null;//断言语法  不知道何意
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//加一个返回图标
        }
    }

    /**
     * 将Activity中都FreamLayout替换为Fragment
     * @param fragment
     * @param tag
     */
    public void replaceFragment(Fragment fragment, String tag) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.content_main, fragment, tag);
        transaction.commit();
    }
    protected boolean isShowToolbar=true;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void hideToolbar() {
        if (toolbar!=null){
            isShowToolbar = false;
            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        }
    }

    public void showToolbar() {
        if (toolbar!=null){
            isShowToolbar = true;
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
}
