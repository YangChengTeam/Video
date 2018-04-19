package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.CircleProgressView;
import com.video.newqu.view.widget.ProgressWheel;

/**
 * TinyHung@outlook.com
 * 2017-06-25 19:19
 * 居中样式的进度条
 */

public class RecordProgressDialog extends Dialog {

    private CircleProgressView mCircleProgressbar;
    private final TextView tv_loading_message;
    private final ProgressWheel mProgress;
    public static final int SHOW_MODE1=1;//有数字的进度条
    public static final int SHOW_MODE2=2;//没有数字的进度条
    private boolean isBack;

    public RecordProgressDialog(Context context) {
        super(context, R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_transcoding);
        Utils.setDialogWidth(this);
        setCancelable(false);
        mCircleProgressbar = (CircleProgressView) findViewById(R.id.circleProgressbar);
        mProgress = (ProgressWheel) findViewById(R.id.progress);
        tv_loading_message = (TextView) findViewById(R.id.tv_loading_message);
        mCircleProgressbar.setProgress(0);
    }

    public void setTipsMessage(String tips){
        if(null!=tv_loading_message) tv_loading_message.setText(tips);
    }

    public void setProgress(int progress){
        if(null!=mCircleProgressbar)mCircleProgressbar.setProgressNotInUiThread(progress);
    }

    /**
     * 设置显示模式
     * @param mode
     */
    public void setMode(int mode){
        if(SHOW_MODE2==mode){
            mCircleProgressbar.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
        }else if(SHOW_MODE1==mode){
            mProgress.setVisibility(View.INVISIBLE);
            mCircleProgressbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
        if(null!=mCircleProgressbar){
            mCircleProgressbar.setProgress(0);
        }
    }

    @Override
    public void dismiss() {
        if(null!=mCircleProgressbar){
            mCircleProgressbar.setProgress(0);
        }
        super.dismiss();
    }

    public void setMax(int progress) {
        mCircleProgressbar.setMaxProgress(progress);
    }


    public interface  OnDialogBackListener{
        void onBack();
    }
    private OnDialogBackListener mOnDialogBackListener;

    public void setOnDialogBackListener(OnDialogBackListener onDialogBackListener) {
        mOnDialogBackListener = onDialogBackListener;
    }


    /**
     * 将用户按下返回键时间传递出去
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!isBack){
                if(mOnDialogBackListener!=null){
                    mOnDialogBackListener.onBack();
                }
                return false;
            }else{
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
