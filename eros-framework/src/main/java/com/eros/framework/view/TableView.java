package com.eros.framework.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.eros.framework.BMWXEnvironment;
import com.eros.framework.R;
import com.eros.framework.activity.AbstractWeexActivity;
import com.eros.framework.activity.GlobalModalActivity;
import com.eros.framework.adapter.DefaultNavigationAdapter;
import com.eros.framework.constant.Constant;
import com.eros.framework.constant.WXEventCenter;
import com.eros.framework.event.TabbarEvent;
import com.eros.framework.fragment.MainWeexFragment;
import com.eros.framework.manager.ManagerFactory;
import com.eros.framework.manager.impl.dispatcher.DispatchEventManager;
import com.eros.framework.model.NatigatorModel;
import com.eros.framework.model.NavigatorModel;
import com.eros.framework.model.PlatformConfigBean;
import com.eros.framework.model.TabbarBadgeModule;
import com.eros.framework.model.TabbarWatchBean;
import com.eros.framework.model.WeexEventBean;
import com.eros.widget.utils.ColorUtils;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 首页 tebView
 * Created by liuyuanxiao on 2018/5/24.
 */

public class TableView extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private LinearLayout llTabBar;
    private ImageView borderLine;
    private NoScrollViewPager viewpager;
    private PlatformConfigBean.TabBar tabBarBean;
    private List<MainWeexFragment> fragments;
    private MyFragmentAdapter fragmentAdapter;
    private SparseArray<NavigatorModel> navigatorArray;
    private Activity activity;

    public TableView(Context context) {
        super(context);
        initView(context);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        activity = (Activity) context;
        this.inflater = LayoutInflater.from(context);
        fragments = new ArrayList<>();
        view = inflater.inflate(R.layout.view_tab_layout, this);
        llTabBar = (LinearLayout) view.findViewById(R.id.llTabBar);
        borderLine = (ImageView) view.findViewById(R.id.borderLine);
        viewpager = (NoScrollViewPager) view.findViewById(R.id.viewpager);
    }

    public void setData(PlatformConfigBean.TabBar tabBar) {
        this.tabBarBean = tabBar;
        navigatorArray = new SparseArray<>();
        // 设置Tab 上面线的颜色
        if (!TextUtils.isEmpty(tabBar.getBorderColor())) {
            borderLine.setBackgroundColor(ColorUtils.getColor(tabBar.getBorderColor()));
        }
        // 设置 Tab 背景
        if (!TextUtils.isEmpty(tabBar.getBackgroundColor())) {
            llTabBar.setBackgroundColor(ColorUtils.getColor(tabBar.getBackgroundColor()));
        }
        fragmentAdapter = new MyFragmentAdapter(((AbstractWeexActivity) context).getSupportFragmentManager(), fragments);
        initItem(tabBar);
        viewpager.setAdapter(fragmentAdapter);
        viewpager.addOnPageChangeListener(this);
        viewpager.setCurrentItem(0);
        viewpager.setOffscreenPageLimit(5);

        DefaultNavigationAdapter.setTabbarNavigation(activity, navigatorArray.get(0));

    }
    public void clear(){
        llTabBar.removeAllViews();
    }

    /**
     * 初始化各个Item
     *
     * @param tabBar
     */
    private void initItem(PlatformConfigBean.TabBar tabBar) {
        List<PlatformConfigBean.TabItem> items = tabBar.getList();
        // 循环add  tab Item
        for (int i = 0; i < items.size(); i++) {
            PlatformConfigBean.TabItem item = items.get(i);
            TableItemView itemView = new TableItemView(context);
            LinearLayout.LayoutParams weight1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            itemView.setLayoutParams(weight1);
            itemView.setTextColor(tabBar.getColor(), tabBar.getSelectedColor());
            if(items.size() > 4 && "click".equals(items.get(2).getAction())){
                if(i > 2){
                    itemView.setIndex(i-1);
                }else if(i != 2) {
                    itemView.setIndex(i);
                }else if(i == 2){
                    //itemView.set
                }
            } else {
                itemView.setIndex(i);
            }


            itemView.setData(item);
            llTabBar.addView(itemView);
            if ("click".equals(item.getAction())) {
                //click事件
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent().putExtra(Constant.GLOBAL_MODAL_URL,item.getPagePath())
                                .setClass(context,GlobalModalActivity.class);
                        context.startActivity(intent);
                    }
                });
            } else {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewpager.setCurrentItem(((TableItemView) v).getIndex(), false);
                    }
                });
            }
            if(items.size() > 4 && "click".equals(items.get(2).getAction())){
                if(i > 2){
                    // new fragment
                    initFragment(item, i-1);
                }else if(i != 2){
                    // new fragment
                    initFragment(item, i);
                }
            } else {
                initFragment(item, i);
            }

        }
    }
    /**
     * 修改某个Item
     *
     * @param tabBar
     */
    public void changeItem(PlatformConfigBean.TabBar tabBar,int i) {
        List<PlatformConfigBean.TabItem> items = tabBar.getList();
            PlatformConfigBean.TabItem item = items.get(i);
            TableItemView itemView = (TableItemView) llTabBar.getChildAt(i);
            LinearLayout.LayoutParams weight1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            itemView.setLayoutParams(weight1);
            itemView.setTextColor(tabBar.getColor(), tabBar.getSelectedColor());
            itemView.setData(item);
            if("click".equals(item.getAction())){
                if(i == 2){
                    //itemView.set
                    TableItemView temp1 = (TableItemView) llTabBar.getChildAt(i+1);
                    temp1.setIndex(i);
                    TableItemView temp2 = (TableItemView) llTabBar.getChildAt(i+2);
                    temp2.setIndex(i+1);
                }
            } else {
                if(i == 2){
                    items.set(2,item);
                    itemView.setIndex(i);
                    TableItemView temp1 = (TableItemView) llTabBar.getChildAt(i+1);
                    temp1.setIndex(i+1);
                    TableItemView temp2 = (TableItemView) llTabBar.getChildAt(i+2);
                    temp2.setIndex(i+2);
//
                }

            }

            if ("click".equals(item.getAction())) {
                //click事件
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent().putExtra(Constant.GLOBAL_MODAL_URL,item.getPagePath())
                                .setClass(context,GlobalModalActivity.class);
                        context.startActivity(intent);
                    }
                });
            } else {
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewpager.setCurrentItem(((TableItemView) v).getIndex(), false);
                    }
                });
            }
            if("click".equals(item.getAction())){
                if(i == 2){
                    fragments.remove(i);
                    viewpager.getAdapter().notifyDataSetChanged();
//                    if(viewpager.getCurrentItem() > 2)viewpager.setCurrentItem(viewpager.getCurrentItem()-1);
                    NavigatorModel model1 = navigatorArray.get(3);
                    NavigatorModel model2 = navigatorArray.get(4);
                    navigatorArray.append(2, model1);
                    navigatorArray.append(3, model2);
                }
            } else {
                changeFragment(item, i);
            }
    }
    public void setBadge(TabbarBadgeModule module) {
        TableItemView itemView = (TableItemView) llTabBar.getChildAt(module.getIndex());
        if (!TextUtils.isEmpty(module.getTextColor())) {
            itemView.setCircTextColor(module.getTextColor());
        }
        if (!TextUtils.isEmpty(module.getBgColor())) {
            itemView.setBgColor(module.getBgColor());
        }
        if (module.getValue() == 0) {
            itemView.showPoint(true);
        } else {
            itemView.setCircText(String.valueOf(module.getValue()));
        }

    }

    public void hideBadge(int index) {
        TableItemView itemView = (TableItemView) llTabBar.getChildAt(index);
        itemView.showPoint(false);
        itemView.showCircText(false);
    }

    public void openPage(int index) {
        viewpager.setCurrentItem(index);
    }

    /**
     * 初始化 Fragment
     */
    private void changeFragment(PlatformConfigBean.TabItem item, int index){
        if(fragments.size() > 4)fragments.remove(index);
        MainWeexFragment fragment =  new MainWeexFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainWeexFragment.PAGE_URL, item.getPagePath());
        fragment.setArguments(bundle);
        fragments.add(index,fragment);
        viewpager.getAdapter().notifyDataSetChanged();
        if(viewpager.getCurrentItem() > 2)viewpager.setCurrentItem(viewpager.getCurrentItem()+1);
        NavigatorModel model = new NavigatorModel();
        model.navigatorModel = getNavStr(item);

        NavigatorModel model1 = navigatorArray.get(index);
        NavigatorModel model2 = navigatorArray.get(index+1);
        navigatorArray.append(index+2, model2);
        navigatorArray.append(index+1, model1);
        navigatorArray.append(index, model);
    }
    private void initFragment(PlatformConfigBean.TabItem item, int index) {
        MainWeexFragment fragment = new MainWeexFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainWeexFragment.PAGE_URL, item.getPagePath());
        fragment.setArguments(bundle);
        fragments.add(fragment);
        NavigatorModel model = new NavigatorModel();
        model.navigatorModel = getNavStr(item);
        navigatorArray.append(index, model);
    }


    private String getNavStr(PlatformConfigBean.TabItem item) {
        NatigatorModel model = new NatigatorModel();
        model.setNavShow(item.isNavShow());
        model.setTitle(item.getNavTitle());
        return JSON.toJSONString(model);
    }


    public WXSDKInstance getWXSDKInstance() {
        MainWeexFragment fragment = fragments.get(viewpager.getCurrentItem());
        return fragment.getWXSDKInstance();
    }

    public void refresh() {
        MainWeexFragment fragment = fragments.get(viewpager.getCurrentItem());
        fragment.refresh();
    }

    /**
     * ViewPager 滑动时 动态切换底部按钮的 文字颜色和 图片
     *
     * @param index
     */
    private void setCurrentItem(int index) {
        for (int i = 0; i < llTabBar.getChildCount(); i++) {
            TableItemView itemView = (TableItemView) llTabBar.getChildAt(i);
            itemView.setSelector(index);
        }
        MainWeexFragment fragment = fragments.get(index);
        fragment.setNavigator(navigatorArray.get(index));
    }


    /**
     * 接通 navigator ，前端可以直接 使用 navigator  设置到 fragment
     *
     * @param weexEventBean 参数对象
     * @return
     */
    public boolean setNaigation(WeexEventBean weexEventBean) {
        String params = weexEventBean.getJsParams();
        JSCallback jsCallback = weexEventBean.getJscallback();
        String type = weexEventBean.getKey();

        int currentIndex = viewpager.getCurrentItem();

        for (int i = 0; i < fragments.size(); i++) {
            MainWeexFragment fragment = fragments.get(i);
            if (fragment.getWxInstanseHasCode() == (int) weexEventBean.getExpand()) {
                NavigatorModel navigatorModel = navigatorArray.get(i);
                switch (type) {
                    case WXEventCenter.EVENT_NAVIGATIONINFO: //setNavigationInfo
                        navigatorModel.navigatorModel = params;
                        if (currentIndex == i) {
                            DefaultNavigationAdapter.setNavigationInfo(params, jsCallback);
                        }
                        break;
                    case WXEventCenter.EVENT_LEFTITEM: //setLeftItem
                        navigatorModel.leftNavigatorbarModel = params;
                        navigatorModel.leftItemJsCallback = jsCallback;
                        if (currentIndex == i) {
                            DefaultNavigationAdapter.setLeftItem(params, jsCallback);
                        }
                        break;
                    case WXEventCenter.EVENT_RIGHTITEM://setRightItem
                        navigatorModel.rightNavigatorbarModel = params;
                        navigatorModel.rightItemJsCallback = jsCallback;
                        if (currentIndex == i) {
                            DefaultNavigationAdapter.setRightItem(params, jsCallback);
                        }
                        break;
                    case WXEventCenter.EVENT_CENTERITEM: //setCenterItem
                        navigatorModel.centerNavigatorBarModel = params;
                        navigatorModel.centerItemJsCallback = jsCallback;
                        if (currentIndex == i) {
                            DefaultNavigationAdapter.setCenterItem(params, jsCallback);
                        }
                        break;
                }
                if (currentIndex == i) {
                    DefaultNavigationAdapter.setTabbarNavigation(activity, navigatorModel);
                }
            }

        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(new TabbarWatchBean(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * ViewPage Fragment 适配器
     */
    private static class MyFragmentAdapter extends FragmentStatePagerAdapter {
        private List<MainWeexFragment> fragments;

        public MyFragmentAdapter(FragmentManager fm, List<MainWeexFragment> fragments) {
            super(fm);
            this.fragments = fragments;
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
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    public int getCurrentIndex() {
        return viewpager.getCurrentItem();
    }


}
