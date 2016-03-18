package gcy.com.knowledge.mvp.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.Map;

import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.interf.OnLoadDataListener;
import gcy.com.knowledge.mvp.interf.UpdateReceiver;
import gcy.com.knowledge.mvp.model.Image;
import gcy.com.knowledge.mvp.other.Data;
import gcy.com.knowledge.mvp.other.PictureAdapter;
import gcy.com.knowledge.mvp.presenter.FetchService;
import gcy.com.knowledge.net.API;
import gcy.com.knowledge.net.DB;
import gcy.com.knowledge.net.Net;
import gcy.com.knowledge.ui.BaseFragment;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.SPUtil;
import io.realm.RealmResults;
import okhttp3.Call;

/**
 * Gank and DB beauty fragment.
 */
public class PictureFragment extends RecyclerFragment implements OnLoadDataListener {
    public static final int TYPE_GANK = 0;
    public static final int TYPE_DB_BREAST = 1;
    public static final int TYPE_DB_BUTT = 2;
    public static final int TYPE_DB_SILK = 3;
    public static final int TYPE_DB_LEG = 4;
    public static final int TYPE_DB_RANK = 5;

    private static final int LOAD_COUNT_LARGE = 15;
    private static int LOAD_COUNT = 8;
    private static int PRELOAD_COUNT = 10;

    private String url;
    private int page = 1;
    private StaggeredGridLayoutManager layoutManager;
    private PictureAdapter adapter;
    private RealmResults<Image> images;
    private long GET_DURATION = 3000;
    private UpdateReceiver loadReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private FragmentActivity context;

    @Override
    public void onResume() {
        super.onResume();
        if (lastPosition > layoutManager.getItemCount() - PRELOAD_COUNT) {
            PRELOAD_COUNT++;
            fetch(false);
        }
    }

    @Override
    public void onPause() {
        firstPosition = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()])[0];
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        OkHttpUtils.getInstance().cancelTag(API.TAG_PICTURE);
        localBroadcastManager.unregisterReceiver(loadReceiver);
        SPUtil.save(type + Constants.PAGE, page);
        super.onDestroyView();
    }


    public static PictureFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(Constants.TYPE, type);
        PictureFragment fragment = new PictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        context = getActivity();
        registerUpdateDataReceiver();
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PictureAdapter(context) {
            @Override
            protected void onItemClick(View v, int position) {
                startViewer(v, position);
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onListScrolled();
                }
            }
        });
        type = getArguments().getInt(Constants.TYPE);//当前是哪个标签页

    }

    private void registerUpdateDataReceiver() {
        loadReceiver = new UpdateReceiver(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.registerReceiver(loadReceiver, new IntentFilter(FetchService.ACTION_FETCH));
    }

    private void setUpShareElement() {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                int i = SPUtil.getInt("shared_index");
                Log.i("test", i + " position");
                sharedElements.clear();
                sharedElements.put(adapter.get(i).getUrl(), layoutManager.findViewByPosition(i));
            }
        });
    }


    private void startViewer(View view, int position) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.MENU_TYPE, TabsFragment.MENU_PIC);
        intent.putExtra(Constants.TYPE, type);
        intent.putExtra(Constants.POSITION, position);

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(context, view, adapter.get(position).getUrl());
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }

    private void onListScrolled() {
        int itemCount = layoutManager.getItemCount();
        int[] spans = new int[layoutManager.getSpanCount()];

        firstPosition = layoutManager.findFirstVisibleItemPositions(spans)[0];
        lastPosition = layoutManager.findLastVisibleItemPositions(spans)[1];

        if (isFirst && page <= 1) {
            if (lastPosition > images.size() / 3) {
                page = SPUtil.getInt(type + Constants.PAGE);
                fetch(false);
            }

        } else if (lastPosition > itemCount - PRELOAD_COUNT) {
            fetch(false);
        }
        //split show progress code with fetch code
        //so user may not see the annoying circle here and there
        if (lastPosition > itemCount - PRELOAD_COUNT / 3) {
            changeProgress(true);
        }
    }

    private void fetch(boolean fresh) {
        initUrl(fresh);
        getData();
    }

    private void getData() {
        final long lastGetTime = System.currentTimeMillis();
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                if (System.currentTimeMillis() - lastGetTime < GET_DURATION) {
                    Net.get(url, this, API.TAG_PICTURE);
                    return;
                }
                onFailure("load failed",e);
            }

            @Override
            public void onResponse(String response) {
                if (isAlive()) {
                    FetchService.startActionFetch(getActivity(), type, response);
                }
            }
        };
        Net.get(url, callback, API.TAG_PICTURE);
    }

    private void initUrl(boolean fresh) {
        if (fresh) {
            page = 1;
            isFirst = true;
        } else {
            isFirst = false;
        }

        switch (type) {
            case TYPE_DB_BREAST:
                url = API.DB_BREAST + page;
                break;
            case TYPE_DB_BUTT:
                url = API.DB_BUTT + page;
                break;
            case TYPE_DB_LEG:
                url = API.DB_LEG + page;
                break;
            case TYPE_DB_SILK:
                url = API.DB_SILK + page;
                break;
            case TYPE_DB_RANK:
                url = API.DB_RANK + page;
                break;
            default://type = 0, 代表GANK
                url = API.GANK + LOAD_COUNT + "/" + page;
                if (isFirst) {
                    //if first load, we make load count larger next time
                    // (coz user has images to see)
                    LOAD_COUNT = LOAD_COUNT_LARGE;
                }
                break;
        }
    }



    @Override
    protected void initData() {
        images = DB.getImages(type);
        if (images.isEmpty()) {
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    changeProgress(true);
                }
            });
            fetch(true);
            return;
        }
        adapter.addAll(images);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        fetch(true);
    }


    @Override
    public void onSuccess(Data data) {
        changeProgress(false);
        adapter.replaceWith(images);
        adapter.notifyDataSetChanged();
        page++;
    }

    @Override
    public void onFailure(String msg, Exception e) {
        changeProgress(false);
        adapter.replaceWith(images);
        if (isAlive()) {
            Snackbar.make(rootView, getString(R.string.load_no_result), Snackbar.LENGTH_LONG)
                    .setAction(R.string.try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fetch(false);
                        }
                    }).show();
        }
    }
}
