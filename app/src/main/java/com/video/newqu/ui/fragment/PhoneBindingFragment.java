package com.video.newqu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.UserData;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentPhoneBindingBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.activity.PhoneChangedActivity;
import com.video.newqu.ui.contract.BindingPhoneContract;
import com.video.newqu.ui.presenter.BindingPhonePresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.Logger;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

/**
 * TinyHung@Outlook.com
 * 2018/4/18
 * 绑定手机号
 */

public class PhoneBindingFragment extends BaseFragment<FragmentPhoneBindingBinding,BindingPhonePresenter> implements BindingPhoneContract.View, Observer {

    private static final String TAG = "PhoneBindingFragment";
    private String mContent;
    private PhoneChangedActivity mActivity;
    private Animation mLoadAnimation;
    private String phone;
    private Handler mHandler;


    public static PhoneBindingFragment newInstance(String stringExtra) {
        PhoneBindingFragment fragment=new PhoneBindingFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.KEY_CONTENT_EXTRA,stringExtra);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mContent = arguments.getString(Constant.KEY_CONTENT_EXTRA);
        }
    }

    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_submit:
                        bindingPhonrBumber();
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
        return R.layout.fragment_phone_binding;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (PhoneChangedActivity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindingView.tvTips.setText(mContent);
        ApplicationManager.getInstance().addObserverToMusic(this);
        mLoadAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        mPresenter=new BindingPhonePresenter(getActivity());
        mPresenter.attachView(this);
        mHandler=new Handler();
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        String phoneBumber = bindingView.inputAccount.getEditContent();
        if(TextUtils.isEmpty(phoneBumber)){
            bindingView.inputAccount.startAnimation(mLoadAnimation);
            ToastUtils.showCenterToast("手机号码不能为空!");
            return;
        }
        if(!Utils.isPhoneNumber(phoneBumber)){
            bindingView.inputAccount.startAnimation(mLoadAnimation);
            ToastUtils.showCenterToast("手机号码格式不正确！");
            return;
        }
        showProgressDialog("获取验证码中，请稍后...",true);

        SMSSDK.getVerificationCode("86", phoneBumber, new OnSendMessageHandler() {
            @Override
            public boolean onSendMessage(String country, String account) {
                return false;//发送短信之前调用，返回TRUE表示无需真正发送验证码
            }
        });
    }

    /**
     * 绑定手机号
     */
    private void bindingPhonrBumber() {
        String phoneBumber = bindingView.inputAccount.getEditContent();
        String phoneCode = bindingView.etCode.getText().toString().trim();
        if(TextUtils.isEmpty(phoneBumber)){
            bindingView.inputAccount.startAnimation(mLoadAnimation);
            ToastUtils.showCenterToast("手机号码不能为空!");
            return;
        }
        if(!Utils.isPhoneNumber(phoneBumber)){
            bindingView.inputAccount.startAnimation(mLoadAnimation);
            ToastUtils.showCenterToast("手机号码格式不正确！");
            return;
        }
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
        if(null!= mPresenter &&!mPresenter.isSanLogin()){
            showProgressDialog("验证中...",true);
            phone=phoneBumber;
            mPresenter.bindingPhone(phoneBumber,phoneCode);
        }
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



    private void bindingPhoneFinlish() {
        if(null!=mActivity&&!mActivity.isFinishing()){
            mActivity.hendleFinlish(phone);
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showBindingPhoneResult(String data) {
        closeProgressDialog();
        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject=new JSONObject(data);
                if(null!=jsonObject&&jsonObject.length()>0){
                    if(1==jsonObject.getInt("code")){
                        ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        if(null!= VideoApplication.getInstance().getUserData()){
                            UserData.DataBean.InfoBean userData = VideoApplication.getInstance().getUserData();
                            if(!TextUtils.isEmpty(phone))userData.setPhone(phone);
                            VideoApplication.getInstance().setUserData(userData,true);
                            bindingPhoneFinlish();
                            return;
                        }
                    }else if(0==jsonObject.getInt("code")){
                        ToastUtils.showCenterToast(jsonObject.getString("msg"));
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ToastUtils.showCenterToast("验证失败");
        }
    }

    @Override
    public void showBindingPhoneError(String data) {
        closeProgressDialog();
        ToastUtils.showCenterToast(data);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof Integer){
                Integer action= (Integer) arg;
                switch (action) {
                    //发送验证码成功
                    case Constant.OBSERVABLE_ACTION_SEND_FINLISH_SMS:
                        closeProgressDialog();
                        showGetCodeDisplay();
                        break;
                    //发送验证码失败
                    case Constant.OBSERVABLE_ACTION_SEND_SMS_ERROR:
                        closeProgressDialog();
                        initGetCodeBtn();
                        break;
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity=null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ApplicationManager.getInstance().removeObserverToMusic(this);
        if(null!=mHandler) mHandler.removeMessages(0);
        taskRunnable=null;
        if(null!=mLoadAnimation) mLoadAnimation.cancel();
        mLoadAnimation=null;
        if(null!=getActivity()){
            ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
