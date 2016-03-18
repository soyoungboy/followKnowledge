package gcy.com.knowledge.mvp.interf;

import gcy.com.knowledge.mvp.interf.OnLoadDataListener;
import gcy.com.knowledge.mvp.interf.OnLoadDetailListener;
import gcy.com.knowledge.mvp.other.Data;
import gcy.com.knowledge.mvp.other.NewsDetail;
import gcy.com.knowledge.mvp.other.NewsItem;

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
 * 描     述 ：处理新闻数据
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public interface NewsModel<I extends NewsItem,N extends Data,D extends NewsDetail> {
    void getNews(int type, OnLoadDataListener<N> listener);

    void getNewsDetail(I newsItem, OnLoadDetailListener<D> listener);
}
