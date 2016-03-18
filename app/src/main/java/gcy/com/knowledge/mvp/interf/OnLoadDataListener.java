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
 * 描     述 ：当新闻加载时，这个接口调用
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public interface OnLoadDataListener<T extends Data> {
    void onSuccess(T data);
    void onFailure(String msg, Exception e);
}
