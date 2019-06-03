package com.eros.framework.extend.module;

import android.util.Log;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.eros.framework.constant.WXEventCenter;
import com.eros.framework.manager.ManagerFactory;
import com.eros.framework.manager.impl.dispatcher.DispatchEventManager;
import com.eros.framework.model.WeexEventBean;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;
/**
 * Created by finesure on 2019/4/7.
 */
@WeexModule(name = "bmGlobalModal", lazyLoad = true)
public class GlobalModal extends WXModule {

    @JSMethod(uiThread = true)
    public void openModal(String params) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_MODAL_OPEN);
        weexEventBean.setJsParams(params);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }

    @JSMethod(uiThread = true)
    public void closeModal(String params) {
        WeexEventBean weexEventBean = new WeexEventBean();
        weexEventBean.setKey(WXEventCenter.EVENT_MODAL_CLOSE);
        weexEventBean.setJsParams(params);
        weexEventBean.setContext(mWXSDKInstance.getContext());
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().post(weexEventBean);
    }
}
