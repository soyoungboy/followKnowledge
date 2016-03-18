package gcy.com.knowledge.mvp.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.model.FreshPost;
import gcy.com.knowledge.mvp.model.Image;
import gcy.com.knowledge.net.DB;
import gcy.com.knowledge.ui.BaseActivity;
import gcy.com.knowledge.ui.BaseFragment;
import gcy.com.knowledge.utils.Constants;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import ooo.oxo.library.widget.PullBackLayout;

public class DetailActivity extends BaseActivity implements PullBackLayout.Callback, RealmChangeListener {

    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.container)
    FrameLayout container;
    private String menu_type;
    private static final int SYSTEM_UI_SHOW = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    private static final int SYSTEM_UI_HIDE = View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;
    private boolean isSystemUiShown = true;

    boolean isPicture=false;
    private int currentPosition;
    private RealmResults<Image> images;
    private DetailPagerAdapter adapter;

    @Override
    public int initLayoutId() {
        menu_type = getIntent().getStringExtra(Constants.MENU_TYPE);
        if(TabsFragment.MENU_NEWS.equals(menu_type)){
            return R.layout.activity_detail;
        }else {
            isPicture=true;
            //?设置主题
            return R.layout.activity_detail_pulldown;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        supportPostponeEnterTransition();//过渡动画

        int position=getIntent().getIntExtra(Constants.POSITION,0);
        List<BaseFragment> fragments=new ArrayList<BaseFragment>();
        if(isPicture){
            //设置监听
            ((PullBackLayout)container).setCallback(this);
            //得到标签页Index 根据标签页获取数据
            int type=getIntent().getIntExtra(Constants.TYPE,0);
            images = DB.getImages(type);
            for(int i=0;i<images.size();i++){
                fragments.add(ViewerFragment.newInstance(images.get(i).getUrl()));
            }
        }else{
            for(int i=0;i<=DB.findAll(FreshPost.class).size();i++){
                fragments.add(FreshDetailFragment.newInstance(i));
            }
        }
        adapter =new DetailPagerAdapter(getSupportFragmentManager(),fragments);
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        pager.setOffscreenPageLimit(2);//设置当前页2遍的缓存的页数
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                //setEnterSharedElement(position);//设置动画
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onPullStart() {

    }

    @Override
    public void onPull(float v) {
        getWindow().getDecorView().getBackground().setAlpha(0xff - (int) Math.floor(0xff * v));
    }

    @Override
    public void onPullCancel() {
        toggleUI();
    }

    @Override
    public void onPullComplete() {
        supportFinishAfterTransition();
    }

    @Override
    public void onChange() {
        adapter.notifyDataSetChanged();
    }
    public void toggleUI() {
        toggleSystemUI();
        toggleToolbar();
    }
    public void toggleToolbar() {
        if (isShowToolbar) {
            hideToolbar();
        } else {
            showToolbar();
        }
    }

    public void toggleSystemUI() {
        if (isSystemUiShown) {
            hideSystemUi();
        } else {
            showSystemUi();
        }
    }
    public void hideSystemUi(){
        pager.setSystemUiVisibility(SYSTEM_UI_HIDE);
        isSystemUiShown = false;
    }
    public void showSystemUi() {
        pager.setSystemUiVisibility(SYSTEM_UI_SHOW);
        isSystemUiShown = true;
    }
    public class DetailPagerAdapter extends FragmentPagerAdapter{

        List<BaseFragment> fragments;
        public DetailPagerAdapter(FragmentManager fm,List<BaseFragment> fragments) {
            super(fm);
            this.fragments=fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
