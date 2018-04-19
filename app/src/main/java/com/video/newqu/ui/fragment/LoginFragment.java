package com.video.newqu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.bean.SMSEventMessage;
import com.video.newqu.bean.UserData;
import com.video.newqu.databinding.FragmentLoginBinding;
import com.video.newqu.ui.activity.LoginGroupActivity;
import com.video.newqu.ui.contract.LoginXinQuContract;
import com.video.newqu.ui.presenter.LoginXinQuPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * TinyHung@Outlook.com
 * 2017/11/28.
 * 用户账号密码登录
 */

public class LoginFragment extends BaseLightWeightFragment<FragmentLoginBinding,LoginXinQuPresenter> implements LoginXinQuContract.View {

    private Animation mInputAnimation;
    private LoginGroupActivity mLoginGroupActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoginGroupActivity = (LoginGroupActivity) context;
    }

    @Override
    protected void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    //登录
                    case R.id.btn_login:
                        createAccountLogin();
                        break;
                    //忘记密码
                    case R.id.tv_retrieve_password:
                        if(null!=mLoginGroupActivity&&!mLoginGroupActivity.isFinishing()){
                            mLoginGroupActivity.addReplaceFragment(new LoginEditPasswordFragment(),"修改密码","登录");//打开修改密码界面
                            mLoginGroupActivity.showOthreLoginView(false);
                        }
                        break;
                }
            }
        };
        bindingView.tvRetrievePassword.setOnClickListener(onClickListener);

        bindingView.btnLogin.setOnClickListener(onClickListener);

        mInputAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new LoginXinQuPresenter(getActivity());
        mPresenter.attachView(this);
    }


    /**
     * 用户使用账号登录
     */
    private void createAccountLogin() {
        String account = bindingView.inputAccount.getEditContent();
        String password = bindingView.inputPassword.getEditContent();
        if(TextUtils.isEmpty(account)){
            ToastUtils.showCenterToast("手机号码不能为空");
            bindingView.inputAccount.startAnimation(mInputAnimation);
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtils.showCenterToast("密码不能为空");
            bindingView.inputPassword.startAnimation(mInputAnimation);
            return;
        }
        if(!Utils.isPhoneNumber(account)){
            ToastUtils.showCenterToast("手机号码格式不正确");
            return;
        }
        if(null!= mPresenter &&!mPresenter.isLogin()){
            showProgressDialog("登录中,请稍后...",true);
            mPresenter.userLogin("86",account,password);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 刷新通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SMSEventMessage event) {
        if(null!=event){
            if(98==event.getSmsCode()&&null!=bindingView) {
                bindingView.inputAccount.setEditContent( event.getAccount());
                bindingView.inputPassword.setEditContent(event.getPassword());
                if (null != mPresenter && !mPresenter.isLogin()) {
                    showProgressDialog("登录中,请稍后...", true);
                    mPresenter.userLogin("86", event.getAccount(), event.getPassword());
                }
            }else if(99==event.getSmsCode()&&null!=bindingView){
                bindingView.inputAccount.setEditContent(event.getAccount());
                bindingView.inputPassword.setEditContent("");
            }
        }
    }

    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {

    }

    @Override
    public void showLoginError(String data) {
        closeProgressDialog();
        ToastUtils.showCenterToast(data);
    }

    @Override
    public void showLoginFinlish(MineUserInfo data) {
        closeProgressDialog();
        MineUserInfo.DataBean.InfoBean info = data.getData().getInfo();
        UserData.DataBean.InfoBean infoBean =new UserData.DataBean.InfoBean();
        infoBean.setCity(info.getCity());
        infoBean.setGender(info.getGender());
        infoBean.setId(info.getId());
        infoBean.setImeil(info.getImeil());
        infoBean.setLogin_type(info.getLogin_type());
        infoBean.setLogo(info.getLogo());
        infoBean.setNickname(info.getNickname());
        infoBean.setOpen_id(info.getOpen_id());
        infoBean.setProvince(info.getProvince());
        infoBean.setSignature(info.getSignature());
        infoBean.setLogin_type(info.getLogin_type());
        infoBean.setStatus(info.getStatus());
        infoBean.setPhone(info.getPhone());
        VideoApplication.getInstance().setUserData(infoBean,true);
        if(null!=mLoginGroupActivity&&!mLoginGroupActivity.isFinishing()){
            mLoginGroupActivity.closeForResult(info);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeProgressDialog();
        bindingView.inputAccount.setEditContent("");
        bindingView.inputPassword.setEditContent("");
        if(null!=mInputAnimation){
            mInputAnimation.cancel();
            mInputAnimation=null;
        }
        mPresenter =null;
        mLoginGroupActivity=null;
    }
}
