package com.video.newqu.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * TinyHung@Outlook.com
 * 2018/4/23
 */

public abstract class BaseDialog<V extends ViewDataBinding> extends BottomSheetDialog {

    protected  V bindingView;

    public BaseDialog(@NonNull AppCompatActivity context) {
        super(context);
        bindingView=DataBindingUtil.inflate(context.getLayoutInflater(),getLayoutID(),null,false);
        setContentView(bindingView.getRoot());
    }

    public abstract int getLayoutID();
}
