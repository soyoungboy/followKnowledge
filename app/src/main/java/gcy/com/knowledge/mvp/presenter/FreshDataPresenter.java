package gcy.com.knowledge.mvp.presenter;

import android.content.Context;

import gcy.com.knowledge.mvp.interf.NewsModel;
import gcy.com.knowledge.mvp.interf.NewsPresenter;
import gcy.com.knowledge.mvp.interf.NewsView;
import gcy.com.knowledge.mvp.interf.OnLoadDataListener;
import gcy.com.knowledge.mvp.model.FreshDetailJson;
import gcy.com.knowledge.mvp.model.FreshJson;
import gcy.com.knowledge.mvp.model.FreshModel;
import gcy.com.knowledge.mvp.model.FreshPost;
import gcy.com.knowledge.net.API;

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
 * 描     述 ：负责监听不同获取处理的不同方法对页面进行交互
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
/**
 * helps to present fresh news list
 */
public class FreshDataPresenter implements NewsPresenter, OnLoadDataListener<FreshJson> {
    private NewsView<FreshJson> mNewsView;
    private NewsModel<FreshPost, FreshJson, FreshDetailJson> mNewsModel;

    public FreshDataPresenter(NewsView<FreshJson> newsView) {
        this.mNewsView = newsView;
        mNewsModel = new FreshModel();
    }

    @Override
    public void loadNews() {
        mNewsView.showProgress();
        mNewsModel.getNews(FreshModel.TYPE_FRESH, this);
    }

    @Override
    public void loadBefore() {
        mNewsView.showProgress();
        mNewsModel.getNews(FreshModel.TYPE_CONTINUOUS, this);

    }

    @Override
    public void onSuccess(FreshJson news) {
        mNewsView.addNews(news);
        mNewsView.hideProgress();
    }

    @Override
    public void onFailure(String msg, Exception e) {
        mNewsView.hideProgress();
        mNewsView.loadFailed(msg);
    }
}
