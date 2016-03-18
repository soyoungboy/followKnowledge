package gcy.com.knowledge.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import gcy.com.knowledge.KnowledgeApp;
import gcy.com.knowledge.MainActivity;
import gcy.com.knowledge.R;
import gcy.com.knowledge.net.API;
import gcy.com.knowledge.net.Net;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.Dater;
import gcy.com.knowledge.utils.Imager;
import gcy.com.knowledge.utils.SPUtil;
import okhttp3.Call;

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_DURATION = 3000;
    public static final String SPLASH = "splash";
    private ImageView splash;
    private String today;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash = (ImageView) findViewById(R.id.splash);

        fullScreen();
        if(settingIsOpen()){
            startAppDelay();
        }
        initSplash();
    }

    /**
     * 设置是否开闪屏页
     * @return
     */
    private boolean settingIsOpen() {
        return true;
    }

    /**
     * 加载图片到ImageView缩放动画显示
     */
    private void loadImageFile() {
        String url = SPUtil.get(SPLASH, "");
        if ("".equals(url)) {
            //没有图片显示本地的R.drawable.splash，加载完显示到splash
            Glide.with(this).load(R.drawable.splash).crossFade(SPLASH_DURATION).into(splash);
        } else {//有图片地址就加载  内部有三级缓存
            Imager.load(url, R.anim.scale_anim, splash);
        }
    }
    /**
     * 全屏相关处理
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        Glide.with(this).load(R.drawable.splash).crossFade(SPLASH_DURATION).into(splash);
    }

    /**
     * 加载图片并保存，伴随动画
     */
    private void initSplash() {
        loadImageFile();
        if(needUpdate()){
            getSplash();
        }
    }

    /**
     * 是否需要更新闪屏页图片
     * does need to update splash?
     * @return
     */
    private boolean needUpdate() {
        today = Dater.parseStandardDate(new Date());
        return !today.equals(SPUtil.getString(Constants.DATE));//not the same day
    }

    private void getSplash(){
        if(!Net.isOnline(KnowledgeApp.context)){
            return;
        }
        Net.get(API.SPLASH,new StringCallback(){
            @Override
            public void onError(Call call, Exception e) {
                startApp();//获取图片失败直接正常启动App
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String Url = jsonObject.getString("img");
                    SPUtil.save(SPLASH,Url);
                    SPUtil.save(Constants.DATE,today);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },API.TAG_SPLASH);
    }

    /**
     * 延迟启动主页面
     * delay start app
     */
    private void startAppDelay() {
        splash.postDelayed(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        }, SPLASH_DURATION);
    }

    /**
     * 启动主页面
     */
    private void startApp() {
        startActivity(new Intent(this, MainActivity.class));
        //无动画效果
        finish();
    }
}
