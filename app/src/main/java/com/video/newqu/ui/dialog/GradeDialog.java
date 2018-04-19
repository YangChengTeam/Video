package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.video.newqu.R;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 评分弹窗
 */

public class GradeDialog extends Dialog {

    private final TextView mBtn_left;
    private final TextView mBtn_right;
    private final TextView mTv_title_content;

    public GradeDialog(@NonNull Context context) {
        super(context,R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_grade_layout);
        mBtn_left = (TextView) findViewById(R.id.btn_left);
        mBtn_right = (TextView) findViewById(R.id.btn_right);
        mTv_title_content = (TextView) findViewById(R.id.tv_title_content);

        mTv_title_content.setText("喜欢新趣吗？");
        mBtn_left.setText("一般般");
        mBtn_right.setText("喜欢");

        mBtn_left.setTag(0);
        mBtn_right.setTag(0);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_left:
                        clickEvent(mBtn_left);
                        break;
                    case R.id.btn_right:
                        clickEvent(mBtn_right);
                        break;
                }
            }
        };
        mBtn_left.setOnClickListener(onClickListener);
        mBtn_right.setOnClickListener(onClickListener);
    }

    private void clickEvent(TextView view) {
        if(mBtn_left==view){
            //一回合，点击了左边按钮
            if(0==(int)mBtn_left.getTag()){
                if(null!=mTv_title_content) mTv_title_content.setText("可以给些宝贵意见");
                if(null!=mBtn_left) mBtn_left.setText("不了，谢谢");
                if(null!=mBtn_right) mBtn_right.setText("去反馈");
                mBtn_left.setTag(1);
                mBtn_right.setTag(1);
            //二回合，处理第二次点击左边按钮
            }else if(1==(int)mBtn_left.getTag()){
                if(null!=mOnItemClickListener){
                    GradeDialog.this.dismiss();
                    mOnItemClickListener.onCancel();
                }
            }
        }else if(mBtn_right==view){
            //一回合，点击了右边喜欢按钮
            if(0==(int)mBtn_right.getTag()){
                if(null!=mTv_title_content) mTv_title_content.setText("给个好评怎么样？");
                if(null!=mBtn_left) mBtn_left.setText("不了，谢谢");
                if(null!=mBtn_right) mBtn_right.setText("去打分");
                mBtn_left.setTag(1);
                mBtn_right.setTag(1);
                //二回合，处理第二次点击左边按钮
            }else if(1==(int)mBtn_right.getTag()){
                if(null!=mOnItemClickListener){
                    GradeDialog.this.dismiss();
                    if(TextUtils.equals("去反馈",mBtn_right.getText().toString())){
                        mOnItemClickListener.onGoToTheServicer();
                    }else if(TextUtils.equals("去打分",mBtn_right.getText().toString())){
                        mOnItemClickListener.onGoToTheStore();
                    }else{
                        mOnItemClickListener.onGoToTheStore();
                    }
                }
            }
        }
    }


    public interface OnItemClickListener{
        void onGoToTheStore();
        void onGoToTheServicer();
        void onCancel();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
