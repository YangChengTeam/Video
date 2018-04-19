package com.video.newqu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.video.newqu.R;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentPhoneCheckedBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.activity.PhoneChangedActivity;
import com.video.newqu.ui.presenter.BindingPhonePresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.Logger;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import java.util.Observable;
import java.util.Observer;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

/**
 * TinyHung@Outlook.com
 * 2018/4/18
 * 手机号码校验
 */

public class PhoneCheckedFragment extends BaseLightWeightFragment<FragmentPhoneCheckedBinding,BindingPhonePresenter> implements Observer {

    private static final String TAG = "PhoneCheckedFragment";
    private String mContent;
    private String mPhone;
    private Handler mHandler;
    private PhoneChangedActivity mActivity;
    private Animation mLoadAnimation;

    public static PhoneCheckedFragment newInstance(String stringExtra,String phone) {
        PhoneCheckedFragment fragment=new PhoneCheckedFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.KEY_CONTENT_EXTRA,stringExtra);
        bundle.putString(Constant.KEY_PHONE,phone);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mContent = arguments.getString(Constant.KEY_CONTENT_EXTRA);
            mPhone = arguments.getString(Constant.KEY_PHONE);
        }
    }

    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_submit:
                        submitCode();
                        break;
                    case R.id.btn_get_code:
                        getCode();
                        break;
                }
            }
        };
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        bindingView.btnGetCode.setOnClickListener(onClickListener);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone_checked;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (PhoneChangedActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity=null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindingView.tvTips.setText(mContent);
        mLoadAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        bindingView.tvPhoneNumber.setText(Utils.getBindPhoneNumber(mPhone));
        mHandler=new Handler();
        ApplicationManager.getInstance().addObserverToMusic(this);
    }


    // 提交验证码
    public void submitCode() {
        if(!TextUtils.isEmpty(mPhone)&&null!=mActivity&&!mActivity.isFinishing()){
            String phoneCode = bindingView.etCode.getText().toString().trim();
            if(TextUtils.isEmpty(phoneCode)){
                bindingView.etCode.startAnimation(mLoadAnimation);
                ToastUtils.showCenterToast("请输入接收到的验证码！");
                return;
            }

            if(!Utils.isNumberCode(phoneCode)){
                bindingView.etCode.startAnimation(mLoadAnimation);
                ToastUtils.showCenterToast("验证码格式不正确！");
                return;
            }
            showProgressDialog("验证中...",true);
            mActivity.submitCode(mPhone,phoneCode);
        }
    }


    /**
     * 获取验证码
     */
    private void getCode() {
        if(TextUtils.isEmpty(mPhone)){
            return;
        }
        showProgressDialog("获取验证码中，请稍后...",true);
        SMSSDK.getVerificationCode("86", mPhone, new OnSendMessageHandler() {
            @Override
            public boolean onSendMessage(String country, String account) {
                return false;//发送短信之前调用，返回TRUE表示无需真正发送验证码
            }
        });
    }

    /**
     * 改变获取验证码按钮状态
     */
    private void showGetCodeDisplay() {
        if(null==mHandler) mHandler=new Handler();
        totalTime=60;
        bindingView.btnGetCode.setClickable(false);
        bindingView.btnGetCode.setTextColor(CommonUtils.getColor(R.color.coment_color));
        bindingView.btnGetCode.setBackgroundResource(R.drawable.btn_find_password_bg_gray);
        mHandler.postDelayed(taskRunnable,0);
    }


    /**
     * 还原获取验证码按钮状态
     */
    private void initGetCodeBtn() {
        if(null==mHandler) mHandler=new Handler();
        totalTime=0;
        if(null!=taskRunnable){
            mHandler.removeCallbacks(taskRunnable);
        }
        bindingView.btnGetCode.setText("重新获取");
        bindingView.btnGetCode.setClickable(true);
        bindingView.btnGetCode.setTextColor(CommonUtils.getColor(R.color.login_hint));
        bindingView.btnGetCode.setBackgroundResource(R.drawable.square_login_background_orgin);
    }


    /**
     * 定时任务，模拟倒计时广告
     */
    private int totalTime=60;

    Runnable taskRunnable=new Runnable() {
        @Override
        public void run() {
            bindingView.btnGetCode.setText(totalTime+"S后重试");
            totalTime--;
            if(totalTime<0){
                //还原
                initGetCodeBtn();
                return;
            }
            if(null!=mHandler) mHandler.postDelayed(this,1000);
        }
    };


    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof Integer){
                Integer action= (Integer) arg;
                switch (action) {
                    //发送验证码成功
                    case Constant.OBSERVABLE_ACTION_SEND_FINLISH_SMS:
                        Logger.d(TAG,"发送验证码成功");
                        closeProgressDialog();
                        showGetCodeDisplay();
                        break;
                    //发送验证码失败
                    case Constant.OBSERVABLE_ACTION_SEND_SMS_ERROR:
                        Logger.d(TAG,"发送验证码失败");
                        closeProgressDialog();
                        initGetCodeBtn();
                        break;
                    //验证码验证成功
                    case Constant.OBSERVABLE_ACTION_CHECKED_PHONE:
                        Logger.d(TAG,"手机号验证成功");
                        closeProgressDialog();
                        initGetCodeBtn();
                        //校验通过，去更换手机号码
                        if(null!=mActivity&&!mActivity.isFinishing()){
                            mActivity.replaceFragment(PhoneBindingFragment.newInstance(mContent));
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=mLoadAnimation) mLoadAnimation.cancel();
        mLoadAnimation=null;
        ApplicationManager.getInstance().removeObserverToMusic(this);
    }
}
