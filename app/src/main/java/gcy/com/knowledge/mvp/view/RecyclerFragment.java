package gcy.com.knowledge.mvp.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import gcy.com.knowledge.R;
import gcy.com.knowledge.ui.BaseFragment;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.SPUtil;

public abstract class RecyclerFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    boolean isFirst = true;   //whether is first time to enter fragment
    int type;               // type of recyclerView's content
    int lastPosition;       //last visible position
    int firstPosition;      //first visible position

    @Override
    public int initLayoutId() {
        return R.layout.fragment_recycler;
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState==null){
            //restoring position when reentering fragment.
            lastPosition = SPUtil.getInt(type + Constants.POSITION);
            if (lastPosition > 0) {
                recyclerView.scrollToPosition(lastPosition);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        SPUtil.save(type + Constants.POSITION, firstPosition);
    }

    @Override
    protected void initViews() {
        recyclerView.setHasFixedSize(true);
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary,
                R.color.colorPrimaryDark, R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(this);
    }

    public void changeProgress(final boolean refreshState) {
        if (null != swipeRefresh) {
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(refreshState);
                }
            });
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
