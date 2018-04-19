package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.video.newqu.R;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 关注微信
 */

public class FollowWeiXnDialog extends Dialog {

    private String tips="免费送红包啦！！关注微信公众号就有机会领取微信红包哦!";
    public FollowWeiXnDialog(@NonNull Context context) {
        super(context,R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_follow_weixin_layout);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //关注
                    case R.id.btn_submit:
                        if(null!=mOnItemClickListener){
                            FollowWeiXnDialog.this.dismiss();
                            mOnItemClickListener.onFollow();
                        }
                        break;
                        //关闭
                    case R.id.btn_close:
                        FollowWeiXnDialog.this.dismiss();
                        break;
                }
            }
        };
        ((ImageView) findViewById(R.id.btn_close)).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.btn_submit)).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.tv_title_content)).setText(tips);
    }

    public interface OnItemClickListener{
        void onFollow();
    }

    public void setTipMsg(String msg){
        ((TextView) findViewById(R.id.tv_title_content)).setText(msg);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
