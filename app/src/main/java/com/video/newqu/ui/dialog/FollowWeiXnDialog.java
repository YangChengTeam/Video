package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import com.video.newqu.R;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.databinding.DialogFollowWeixinLayoutBinding;
import com.video.newqu.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 关注微信
 */

public class FollowWeiXnDialog extends BaseDialog<DialogFollowWeixinLayoutBinding> {

    private String tips="免费送红包啦！！关注微信公众号就有机会领取微信红包哦!";

    public FollowWeiXnDialog(@NonNull Activity context) {
        super(context,R.style.CommendDialogStyle);
        setContentView(R.layout.dialog_follow_weixin_layout);
        Utils.setDialogWidth(this);
    }

    @Override
    public void initViews() {
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
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        bindingView.tvTitleContent.setText(tips);
    }

    public interface OnItemClickListener{
        void onFollow();
    }

    public void setTipMsg(String msg){
        if(null!=bindingView)bindingView.tvTitleContent.setText(msg);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
