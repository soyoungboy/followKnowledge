package gcy.com.knowledge.mvp.view;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import gcy.com.knowledge.R;
import gcy.com.knowledge.ui.BaseFragment;
import gcy.com.knowledge.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabsFragment extends BaseFragment {

    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.tabs)
    TabLayout tabs;

    public static final int TYPE_ZHIHU = 1024;
    public static final int TYPE_FRESH = 1025;
    private NewsTabPagerAdapter adapter;
    private List<RecyclerFragment> fragments=new ArrayList<>();
    private List<String> titles=new ArrayList<>();

    public static final String MENU_NEWS = "news";
    public static final String MENU_PIC = "pic";
    private String menuType;

    public static TabsFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(Constants.TYPE,type);
        TabsFragment fragment = new TabsFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_news_tab;
    }

    @Override
    protected void initViews() {

        adapter = new NewsTabPagerAdapter(getChildFragmentManager());
        initFragments();
        pager.setAdapter(adapter);
        //判断type
        if(menuType.equals(MENU_NEWS)){
            tabs.setTabMode(TabLayout.MODE_FIXED);
        }else if(menuType.equals(MENU_PIC)){
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        tabs.setupWithViewPager(pager);
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    private void initFragments() {
        menuType = getArguments().getString(Constants.TYPE);
        if(MENU_NEWS.equals(menuType)){
            fragments.add(new ZhihuFragment());
            fragments.add(new FreshFragment());
            titles.add(getString(R.string.zhihu_news));
            titles.add(getString(R.string.fresh_news));
        }else if(MENU_PIC.equals(menuType)){
            String[] titles = new String[]{
                    getString(R.string.gank),
                    getString(R.string.db_rank),
                    getString(R.string.db_leg),
                    getString(R.string.db_silk),
                    getString(R.string.db_breast),
                    getString(R.string.db_butt)};
            this.titles= Arrays.asList(titles);

            fragments.add(PictureFragment.newInstance(PictureFragment.TYPE_GANK));
            fragments.add(PictureFragment.newInstance(PictureFragment.TYPE_DB_RANK));
            fragments.add(PictureFragment.newInstance(PictureFragment.TYPE_DB_LEG));
            fragments.add(PictureFragment.newInstance(PictureFragment.TYPE_DB_SILK));
            fragments.add(PictureFragment.newInstance(PictureFragment.TYPE_DB_BREAST));
            fragments.add(PictureFragment.newInstance(PictureFragment.TYPE_DB_BUTT));
            if (fragments.size() != titles.length) {
                throw new IllegalArgumentException("You need add all fragments in MenuTabFragment");
            }
        }
        adapter.setFragments(fragments,titles);
    }

    public class NewsTabPagerAdapter extends FragmentPagerAdapter{
        private List<RecyclerFragment> fragments;
        private List<String> titles;
        public void setFragments(List<RecyclerFragment> fragments, List<String> titles) {
            this.fragments = fragments;
            this.titles = titles;
        }
        public void addFragment(RecyclerFragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        public NewsTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
