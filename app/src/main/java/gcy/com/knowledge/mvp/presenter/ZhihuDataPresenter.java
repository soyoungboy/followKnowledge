package gcy.com.knowledge.mvp.presenter;

import android.content.Context;
import android.util.Log;

import gcy.com.knowledge.mvp.interf.NewsModel;
import gcy.com.knowledge.mvp.interf.NewsPresenter;
import gcy.com.knowledge.mvp.interf.NewsView;
import gcy.com.knowledge.mvp.interf.OnLoadDataListener;
import gcy.com.knowledge.mvp.model.ZhihuJson;
import gcy.com.knowledge.mvp.model.ZhihuModel;
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
public class ZhihuDataPresenter implements NewsPresenter,OnLoadDataListener<ZhihuJson> {

    private final NewsView mView;
    private final NewsModel mModel;
    private final Context context;
    public ZhihuDataPresenter(NewsView<ZhihuJson> view,Context context){
        mView = view;
        mModel = new ZhihuModel();
        this.context=context;
    }
    /**
     * 加载页面中
     */
    @Override
    public void loadNews() {
        mView.showProgress();
        mModel.getNews(API.TYPE_LATEST,this);//内部做获取数据的监听
    }

    /**
     * 加载之前
     */
    @Override
    public void loadBefore() {
        mModel.getNews(API.TYPE_BEFORE,this);
    }

    /**
     * 加载成功监听
     * @param data
     */
    @Override
    public void onSuccess(ZhihuJson data) {
        mView.addNews(data);
        mView.hideProgress();
    }

    /**
     * 加载失败监听
     * @param msg
     * @param e
     */
    @Override
    public void onFailure(String msg, Exception e) {
        mView.hideProgress();
        mView.loadFailed(msg);
    }
}
