package gcy.com.knowledge.mvp.interf;


import gcy.com.knowledge.mvp.other.NewsDetail;

/**
 * fragment or activity need to implement this to show news detail.
 */
public interface NewsDetailView<T extends NewsDetail> {
    void showProgress();
    void showDetail(T detailNews);
    void hideProgress();
    void showLoadFailed(String msg);
}
