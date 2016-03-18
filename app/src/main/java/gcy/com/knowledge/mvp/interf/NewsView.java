package gcy.com.knowledge.mvp.interf;

import gcy.com.knowledge.mvp.other.Data;

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
 * 描     述 ：
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public interface NewsView<T extends Data> {
    /**
     * 正在加载
     */
    void showProgress();

    /**
     * 加载成功
     * @param news
     */
    void addNews(T news);

    /**
     * 未知
     */
    void hideProgress();

    /**
     * 加载失败
     * @param msg
     */
    void loadFailed(String msg);
}
