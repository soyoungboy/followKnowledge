package gcy.com.knowledge.mvp.presenter;

import gcy.com.knowledge.mvp.interf.NewsDetailPresenter;
import gcy.com.knowledge.mvp.interf.NewsDetailView;
import gcy.com.knowledge.mvp.interf.NewsModel;
import gcy.com.knowledge.mvp.interf.OnLoadDetailListener;
import gcy.com.knowledge.mvp.model.FreshDetailJson;
import gcy.com.knowledge.mvp.model.FreshJson;
import gcy.com.knowledge.mvp.model.FreshModel;
import gcy.com.knowledge.mvp.model.FreshPost;

/**
 * helps to present fresh news detail page
 */
public class FreshDetailPresenter implements NewsDetailPresenter<FreshPost>, OnLoadDetailListener<FreshDetailJson> {

    private NewsModel<FreshPost, FreshJson, FreshDetailJson> mNewsModel;
    private NewsDetailView<FreshDetailJson> newsDetailView;

    public FreshDetailPresenter(NewsDetailView<FreshDetailJson> newsDetailView) {
        this.mNewsModel = new FreshModel();
        this.newsDetailView = newsDetailView;
    }

    @Override
    public void loadNewsDetail(FreshPost freshPost) {
        newsDetailView.showProgress();
        mNewsModel.getNewsDetail(freshPost, this);
    }

    @Override
    public void onDetailSuccess(FreshDetailJson detailNews) {
        newsDetailView.showDetail(detailNews);

    }

    @Override
    public void onFailure(String msg, Exception e) {
        newsDetailView.showLoadFailed(msg);
        newsDetailView.hideProgress();
    }
}
