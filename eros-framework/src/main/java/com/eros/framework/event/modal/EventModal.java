package com.eros.framework.event.modal;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.eros.framework.activity.GlobalModalActivity;
import com.eros.framework.constant.Constant;
import com.eros.framework.manager.impl.ModalManager;
import com.eros.framework.model.BaseEventBean;
import com.eros.framework.model.WeexEventBean;
import com.eros.wxbase.EventGate;
import com.taobao.weex.bridge.JSCallback;

import java.util.ArrayList;

public class EventModal extends EventGate {
    @Override
    public void perform(Context context, WeexEventBean weexEventBean) {
        String params = weexEventBean.getJsParams();
        if (TextUtils.isEmpty(params)){
            close(params,context);
            return;
        }
        open(params,context);
    }

    public void open(String url, Context context) {
        Intent intent = new Intent().putExtra(Constant.GLOBAL_MODAL_URL,url)
                .setClass(context, GlobalModalActivity.class);
        context.startActivity(intent);
    }

    public void close(String url, Context context) {
        GlobalModalActivity.instance.finish();
    }
}
