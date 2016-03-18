package gcy.com.knowledge;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import butterknife.Bind;
import gcy.com.knowledge.mvp.view.TabsFragment;
import gcy.com.knowledge.ui.BaseActivity;
import gcy.com.knowledge.utils.Share;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private String currentType="";
    @Override
    public int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        super.initViews();

        setupDrawer();
        initNavigationView();//设置左侧NavigationView
        //设置tab孩子
        replace(TabsFragment.MENU_NEWS);
        //设置崩溃分析插件
    }
    private void initNavigationView(){
        //设置点击监听
        navView.setNavigationItemSelectedListener(this);
        //填充数据
        navView.inflateMenu(R.menu.main_menu_all);
        //设置默认数据
        navView.getMenu().getItem(0).setChecked(true);
    }
    private void replace(String type) {
        if(!currentType.equals(type)){
            replaceFragment(TabsFragment.newInstance(type),type);
        }
    }

    /**
     * 设置DrawLayout
     */
    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_knowledge) {
            replace(TabsFragment.MENU_NEWS);
        } else if (id == R.id.nav_beauty) {
            replace(TabsFragment.MENU_PIC);
        }else if (id == R.id.nav_share) {
            startActivity(
                    Intent.createChooser(
                            Share.getShareIntent(getString(R.string.share_app_description)),
                            getString(R.string.share_app)));

        } else if (id == R.id.nav_setting) {
            //startActivity(new Intent(this, SettingsActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
