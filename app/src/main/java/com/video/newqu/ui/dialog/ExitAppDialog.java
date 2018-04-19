package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.video.newqu.R;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 首页退出提示
 */

public class ExitAppDialog extends Dialog {

    public interface OnDialogClickListener{
        void onExitApp();
    }

    private OnDialogClickListener mOnDialogClickListener;

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        mOnDialogClickListener = onDialogClickListener;
    }


    public ExitAppDialog(@NonNull Context context) {
        super(context,R.style.ComentEmptyDialogAnimation);
        setContentView(R.layout.dialog_exit_layout);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            ((TextView) findViewById(R.id.tv_message)).setLetterSpacing(0.1f);
        }

        TextView tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitAppDialog.this.dismiss();
                if(null!=mOnDialogClickListener){
                    mOnDialogClickListener.onExitApp();
                }
            }
        });

        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitAppDialog.this.dismiss();
            }
        });
    }
}
