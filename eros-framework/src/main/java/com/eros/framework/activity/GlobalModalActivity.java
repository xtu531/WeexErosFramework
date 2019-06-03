package com.eros.framework.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.eros.framework.BMWXApplication;
import com.eros.framework.BMWXEnvironment;
import com.eros.framework.R;
import com.eros.framework.constant.Constant;
import com.eros.framework.model.RouterModel;
import com.eros.framework.utils.InsertEnvUtil;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.RenderContainer;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by finesure on 2019/4/6.
 */
public class GlobalModalActivity extends AppCompatActivity implements IWXRenderListener {
    public static GlobalModalActivity instance;
    WXSDKInstance mWXSDKInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modal);
        mWXSDKInstance = new WXSDKInstance(this);
        mWXSDKInstance.registerRenderListener(this);
        RenderContainer renderContainer = new RenderContainer(this);
        mWXSDKInstance.setRenderContainer(renderContainer);
        String url = (String)getIntent().getSerializableExtra(Constant.GLOBAL_MODAL_URL);
        String pageName = "AbstractWeexActivity";
        String bundleUrl = Uri.parse(BMWXEnvironment.mPlatformConfig.getUrl().getJsServer() +
                "/dist/js" + url).toString();//;
        Map<String, Object> options = new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL, bundleUrl);
        InsertEnvUtil.customerRender(options);
        mWXSDKInstance.renderByUrl(pageName, bundleUrl, null, null,WXRenderStrategy.APPEND_ASYNC);
        instance = this;
    }
    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        setContentView(view);
    }
    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
    }
    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
    }
    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityResume();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityPause();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityStop();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityDestroy();
        }
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(0, 0);
    }
    public void onClick(){
        this.finish();
    }
}
