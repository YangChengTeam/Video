package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import com.video.newqu.R;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.databinding.DialogExitLayoutBinding;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 首页退出提示
 */

public class ExitAppDialog extends BaseDialog<DialogExitLayoutBinding> {

    public ExitAppDialog(@NonNull Activity context) {
        super(context,R.style.ComentEmptyDialogAnimation);
        setContentView(R.layout.dialog_exit_layout);
    }

    @Override
    public void initViews() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            bindingView.tvMessage.setLetterSpacing(0.1f);
        }
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_submit:
                        ExitAppDialog.this.dismiss();
                        if(null!=mOnDialogClickListener){
                            mOnDialogClickListener.onExitApp();
                        }
                        break;
                    case R.id.tv_cancel:
                        ExitAppDialog.this.dismiss();
                        break;
                }
            }
        };
        bindingView.tvSubmit.setOnClickListener(onClickListener);
        bindingView.tvCancel.setOnClickListener(onClickListener);
    }

    public interface OnDialogClickListener{
        void onExitApp();
    }
    private OnDialogClickListener mOnDialogClickListener;

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        mOnDialogClickListener = onDialogClickListener;
    }
}
