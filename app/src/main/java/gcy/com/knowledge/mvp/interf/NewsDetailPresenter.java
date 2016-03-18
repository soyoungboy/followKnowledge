package gcy.com.knowledge.mvp.interf;


import gcy.com.knowledge.mvp.other.NewsItem;

/**
 * helps to present news detail page
 */
public interface NewsDetailPresenter<T extends NewsItem> {
    void loadNewsDetail(T newsItem);
}
