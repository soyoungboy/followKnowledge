package gcy.com.knowledge.mvp.view;

import android.annotation.SuppressLint;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.Bind;
import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.interf.NewsDetailPresenter;
import gcy.com.knowledge.mvp.interf.NewsDetailView;
import gcy.com.knowledge.mvp.model.ZhihuDetail;
import gcy.com.knowledge.mvp.model.ZhihuStory;
import gcy.com.knowledge.mvp.model.ZhihuTop;
import gcy.com.knowledge.mvp.presenter.ZhihuDetailPresenter;
import gcy.com.knowledge.net.DB;
import gcy.com.knowledge.ui.BaseActivity;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.Imager;
import gcy.com.knowledge.utils.Share;
import gcy.com.knowledge.utils.UI;

public class ZhihuDetailActivity extends BaseActivity implements NewsDetailView<ZhihuDetail> {
    @Bind(R.id.detail_img)
    ImageView detailImg;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.web_container)
    FrameLayout webContainer;
    private WebView webView;
    private int id;
    private ZhihuStory story;
    private ZhihuDetail zhihuDetail;
    private NewsDetailPresenter<ZhihuStory> presenter;

    @Override
    public int initLayoutId() {
        return  R.layout.activity_zhihu_detail;
    }

    @Override
    protected void initViews() {
        super.initViews();
        id = getIntent().getIntExtra(Constants.ID, 0);
        story = DB.getById(id, ZhihuStory.class);
        zhihuDetail = DB.getById(id, ZhihuDetail.class);
        if (story == null) {
            //can't find zhihuItem, so this id is passed by Zhihutop
            toolbarLayout.setTitle(DB.getById(id, ZhihuTop.class).getTitle());
        } else {
            toolbarLayout.setTitle(story.getTitle());
        }
        presenter = new ZhihuDetailPresenter(this);
        initWebView();
        if (zhihuDetail == null) {
            presenter.loadNewsDetail(story);
        } else {
            showDetail(zhihuDetail);
        }
        initFAB();
    }

    private void initFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share.shareText(ZhihuDetailActivity.this, zhihuDetail.getShare_url());
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView = new WebView(this);
        webContainer.addView(webView);
        webView.setVisibility(View.INVISIBLE);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(final WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.VISIBLE);
                            hideProgress();
                        }
                    }, 300);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        webView.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }


    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDetail(ZhihuDetail detailNews) {
        zhihuDetail = detailNews;
        Imager.load(this, detailNews.getImage(), detailImg);
        //add css style to webView
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + detailNews.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        webView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void showLoadFailed(String msg) {
        UI.showSnackLong(webContainer, R.string.load_fail);

    }
}