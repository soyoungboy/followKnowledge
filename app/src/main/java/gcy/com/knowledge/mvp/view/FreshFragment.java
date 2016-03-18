package gcy.com.knowledge.mvp.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.ButterKnife;
import gcy.com.knowledge.KnowledgeApp;
import gcy.com.knowledge.MainActivity;
import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.interf.NewsPresenter;
import gcy.com.knowledge.mvp.interf.NewsView;
import gcy.com.knowledge.mvp.interf.OnListFragmentInteract;
import gcy.com.knowledge.mvp.model.FreshJson;
import gcy.com.knowledge.mvp.other.NewsListAdapter;
import gcy.com.knowledge.mvp.other.ZhihuListAdapter;
import gcy.com.knowledge.mvp.presenter.FreshDataPresenter;
import gcy.com.knowledge.net.API;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.SPUtil;
import gcy.com.knowledge.utils.UI;

public class FreshFragment extends RecyclerFragment implements NewsView<FreshJson>, OnListFragmentInteract {

    private NewsPresenter mPresenter;
    private NewsListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int firstPosition;
    private int lastPostion;

    @Override
    public void onDestroyView() {
        OkHttpUtils.getInstance().cancelTag(API.TAG_FRESH);
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
        type = TabsFragment.TYPE_FRESH;

        Context context = getActivity();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsListAdapter(this);
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
        firstPosition = layoutManager.findFirstVisibleItemPosition();
        lastPostion = layoutManager.findLastVisibleItemPosition();
        if (lastPostion  == adapter.getItemCount()) {
            mPresenter.loadBefore();
        }
    }

    @Override
    protected void initData() {
        mPresenter = new FreshDataPresenter(this);
        onRefresh();
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
    public void addNews(FreshJson news) {
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
        if (holder instanceof NewsListAdapter.ViewHolder) {
            NewsListAdapter.ViewHolder viewholder = (NewsListAdapter.ViewHolder) holder;
            viewholder.mTitle.setTextColor(ZhihuListAdapter.textGrey);
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Constants.MENU_TYPE, TabsFragment.MENU_NEWS);//点击的详情的类型是新闻
            intent.putExtra(Constants.POSITION, viewholder.getAdapterPosition());//点击的位置
            startActivity(intent);
        }
    }
}
