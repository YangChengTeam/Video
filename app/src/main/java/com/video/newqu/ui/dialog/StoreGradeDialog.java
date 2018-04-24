package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.databinding.DialogStoreGradeLayoutBinding;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 去商店评分
 */

public class StoreGradeDialog extends BaseDialog<DialogStoreGradeLayoutBinding> {

    public StoreGradeDialog(@NonNull Activity context) {
        super(context,R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_store_grade_layout);
    }

    @Override
    public void initViews() {
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
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnGood.setOnClickListener(onClickListener);
        bindingView.btnService.setOnClickListener(onClickListener);
        bindingView. btnCancel.setOnClickListener(onClickListener);
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
