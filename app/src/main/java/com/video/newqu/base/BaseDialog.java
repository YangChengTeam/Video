package com.video.newqu.base;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

/**
 * TinyHung@Outlook.com
 * 2017/3/24 9:12
 * 弹窗的统一父类
 */

public abstract class BaseDialog<V extends ViewDataBinding> extends Dialog {

    protected  V bindingView;

    public BaseDialog(@NonNull Activity context) {
        super(context);
    }

    public BaseDialog(@NonNull Activity context,int themeResId) {
        super(context,themeResId);
    }

    @Override
    public void setContentView(int layoutResId) {
        bindingView=DataBindingUtil.inflate(getLayoutInflater(),layoutResId,null,false);
        getWindow().setContentView(bindingView.getRoot());
        initViews();
    }
    public abstract void initViews();
}
