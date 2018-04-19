package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.video.newqu.R;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 去商店评分
 */

public class StoreGradeDialog extends Dialog {

    public StoreGradeDialog(@NonNull Context context) {
        super(context,R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_store_grade_layout);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //取消
                    case R.id.btn_cancel:
                        if(null!=mOnItemClickListener){
                            StoreGradeDialog.this.dismiss();
                            mOnItemClickListener.onCancel();
                        }
                        break;
                    //去吐槽
                    case R.id.btn_service:
                        if(null!=mOnItemClickListener){
                            StoreGradeDialog.this.dismiss();
                            mOnItemClickListener.onService();
                        }
                        break;
                    //去好评
                    case R.id.btn_good:
                        if(null!=mOnItemClickListener){
                            StoreGradeDialog.this.dismiss();
                            mOnItemClickListener.onGood();
                        }
                        break;
                    //关闭
                    case R.id.btn_close:
                        StoreGradeDialog.this.dismiss();
                        break;
                }
            }
        };

        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        Button btn_service = (Button) findViewById(R.id.btn_service);
        Button btn_good = (Button) findViewById(R.id.btn_good);
        ImageView btn_close = (ImageView) findViewById(R.id.btn_close);

        btn_close.setOnClickListener(onClickListener);
        btn_good.setOnClickListener(onClickListener);
        btn_service.setOnClickListener(onClickListener);
        btn_cancel.setOnClickListener(onClickListener);
    }

    public interface OnItemClickListener{
        void onCancel();
        void onService();
        void onGood();
    }

    public void setTipMsg(String msg){
        ((TextView) findViewById(R.id.tv_title_content)).setText(msg);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
