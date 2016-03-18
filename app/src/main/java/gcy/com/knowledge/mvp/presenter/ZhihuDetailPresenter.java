package gcy.com.knowledge.mvp.presenter;


import gcy.com.knowledge.mvp.interf.NewsDetailPresenter;
import gcy.com.knowledge.mvp.interf.NewsDetailView;
import gcy.com.knowledge.mvp.interf.NewsModel;
import gcy.com.knowledge.mvp.interf.OnLoadDetailListener;
import gcy.com.knowledge.mvp.model.ZhihuDetail;
import gcy.com.knowledge.mvp.model.ZhihuJson;
import gcy.com.knowledge.mvp.model.ZhihuModel;
import gcy.com.knowledge.mvp.model.ZhihuStory;

/**
 * helps to present zhihu news detail page
 */
public class ZhihuDetailPresenter implements NewsDetailPresenter<ZhihuStory>, OnLoadDetailListener<ZhihuDetail> {

    private NewsModel<ZhihuStory, ZhihuJson, ZhihuDetail> newsModel;
    private NewsDetailView<ZhihuDetail> newsDetailView;

    public ZhihuDetailPresenter(NewsDetailView<ZhihuDetail> newsDetailView) {
        this.newsModel = new ZhihuModel();
        this.newsDetailView = newsDetailView;
    }

    @Override
    public void loadNewsDetail(ZhihuStory zhihuStory) {
        newsDetailView.showProgress();
        newsModel.getNewsDetail(zhihuStory, this);
    }

    @Override
    public void onDetailSuccess(ZhihuDetail detailNews) {
        newsDetailView.showDetail(detailNews);
    }

    @Override
    public void onFailure(String msg, Exception e) {
        newsDetailView.showLoadFailed(msg);
        newsDetailView.hideProgress();
    }
}
