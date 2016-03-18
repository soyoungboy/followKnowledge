package gcy.com.knowledge.mvp.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.interf.NewsDetailPresenter;
import gcy.com.knowledge.mvp.interf.NewsDetailView;
import gcy.com.knowledge.mvp.model.FreshDetailJson;
import gcy.com.knowledge.mvp.model.FreshJson;
import gcy.com.knowledge.mvp.model.FreshPost;
import gcy.com.knowledge.mvp.presenter.FreshDetailPresenter;
import gcy.com.knowledge.net.DB;
import gcy.com.knowledge.ui.BaseFragment;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.Share;
import gcy.com.knowledge.utils.UI;

public class FreshDetailFragment extends BaseFragment implements NewsDetailView<FreshDetailJson>{
    private static final String FRESH_ITEM = "fresh_news";
    private static final String FRESH_PREVIOUS_ITEM = "previous_news";

    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.web_container)
    FrameLayout webContainer;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private int position;
    private List<FreshPost> freshPosts;
    private FreshPost curPost;
    private ShareActionProvider mShareActionProvider;
    private WebView webView;
    private NewsDetailPresenter presenter;

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_fresh_detail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){
            freshPosts = DB.findAllDateSorted(FreshPost.class);
            position = getArguments().getInt(Constants.POSITION);
            curPost = freshPosts.get(position);
        }
        setHasOptionsMenu(true);
        //Log.i("tag","FreshDetailFragment-onCreate-setHasOptionsMenu(true)");
    }

    @Override
    protected void initViews() {
        presenter = new FreshDetailPresenter(this);
        initWebView();
    }
    private void initWebView(){
        webView = new WebView(getActivity());
        webContainer.addView(webView);
        WebSettings settings = webView.getSettings();
        settings.setTextZoom(110);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(final WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100){
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.VISIBLE);
                            hideProgress();
                        }
                    },200);
                }
            }
        });

    }
    public static FreshDetailFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(Constants.POSITION,position);
        FreshDetailFragment fragment = new FreshDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        presenter.loadNewsDetail(curPost);
        toolbar.setTitle(curPost.getTitle());
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
    }


    private void setShareIntent() {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(
                    Share.getShareIntent(curPost.getUrl()));
           // Log.i("tag","FreshDetailFragment-onCreate-setShareIntent)");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.share_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
       // Log.i("tag","FreshDetailFragment-onCreate-onCreateOptionsMenu)");
    }

    @Override
    public void showProgress() {
        if(null!=progress){
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showDetail(FreshDetailJson detailNews) {
        setShareIntent();
        //通过数据加载
        webView.loadDataWithBaseURL("x-data://base", detailNews.getPost().getContent(), "text/html", "UTF-8", null);
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void hideProgress() {
        if(null!=progress){
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadFailed(String msg) {
        UI.showSnackLong(rootView, R.string.load_fail);
    }
}
