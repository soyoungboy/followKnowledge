package gcy.com.knowledge.mvp.view;

import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.ButterKnife;
import gcy.com.knowledge.KnowledgeApp;
import gcy.com.knowledge.MainActivity;
import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.interf.NewsPresenter;
import gcy.com.knowledge.mvp.interf.NewsView;
import gcy.com.knowledge.mvp.interf.OnListFragmentInteract;
import gcy.com.knowledge.mvp.model.ZhihuJson;
import gcy.com.knowledge.mvp.other.ZhihuListAdapter;
import gcy.com.knowledge.mvp.presenter.ZhihuDataPresenter;
import gcy.com.knowledge.net.API;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.SPUtil;
import gcy.com.knowledge.utils.UI;

public class ZhihuFragment extends RecyclerFragment implements NewsView<ZhihuJson>, OnListFragmentInteract {


    private NewsPresenter mPresenter;
    private ZhihuListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ConvenientBanner banner;
    private static final int PRELOAD_COUNT = 1;
    private int firstPosition;
    private int lastPostion;

    @Override
    public void onDestroyView() {
        OkHttpUtils.getInstance().cancelTag(API.TAG_ZHIHU);
        SPUtil.save(type + Constants.POSITION, firstPosition);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public int initLayoutId() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void initViews() {
        super.initViews();
        type = TabsFragment.TYPE_ZHIHU;

        Context context = getActivity();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ZhihuListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {//手指静止状态
                    onListScrolled();
                }
            }
        });
    }

    private void onListScrolled() {
        initBanner();
        firstPosition = layoutManager.findFirstVisibleItemPosition();
        lastPostion = layoutManager.findLastVisibleItemPosition();
        if (lastPostion + PRELOAD_COUNT == adapter.getItemCount()) {
            mPresenter.loadBefore();
        }
    }

    @Override
    protected void initData() {
        mPresenter = new ZhihuDataPresenter(this, KnowledgeApp.context);
        initBanner();
        onRefresh();
    }

    private void initBanner() {
        if (null == banner) {
            if (recyclerView.getChildCount() != 0 && layoutManager.findFirstVisibleItemPosition() == 0) {
                banner = (ConvenientBanner) layoutManager.findViewByPosition(0);
                banner.setScrollDuration(1000);
                banner.startTurning(5000);
            }
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.loadNews();
    }

    @Override
    public void showProgress() {
        changeProgress(true);
    }

    @Override
    public void addNews(ZhihuJson news) {
        adapter.addNews(news);
    }

    @Override
    public void hideProgress() {
        changeProgress(false);
    }

    @Override
    public void loadFailed(String msg) {
        UI.showSnack(((MainActivity) getActivity()).getDrawerLayout(), R.string.load_fail);
    }

    @Override
    public void onListFragmentInteraction(RecyclerView.ViewHolder holder) {
        if (holder instanceof ZhihuListAdapter.ViewHolder) {
            ZhihuListAdapter.ViewHolder viewHolder = (ZhihuListAdapter.ViewHolder) holder;
            Intent intent = new Intent(getActivity(), ZhihuDetailActivity.class);
            intent.putExtra(Constants.ID, viewHolder.zhihuStory.getId());
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    viewHolder.mImage, getString(R.string.shared_img));//共享的元素作为2个Activity之间的联系
            ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
        }
    }
}
