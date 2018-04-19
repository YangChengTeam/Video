package com.video.newqu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import com.kk.securityhttp.domain.GoagalInfo;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.bean.SMSEventMessage;
import com.video.newqu.bean.UserData;
import com.video.newqu.bean.UserDataInfo;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityLoginGroupBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.contract.LoginContract;
import com.video.newqu.ui.fragment.CompleteUserDataDialogFragment;
import com.video.newqu.ui.fragment.LoginFragment;
import com.video.newqu.ui.fragment.LoginRegisterFragment;
import com.video.newqu.ui.presenter.LoginPresenter;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ToastUtils;
import org.greenrobot.eventbus.EventBus;
import java.util.Map;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

/**
 * TinyHung@Outlook.com
 * 2017/11/28.
 * 用户登录、注册、修改密码
 */

public class LoginGroupActivity extends BaseActivity<ActivityLoginGroupBinding> implements LoginContract.View {

    private LoginPresenter mLoginPresenter;
    private EventHandler mEventHandler;

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                        onBackPressed();
                        break;
                    case R.id.btn_register:
                        openBtnAction();
                        break;
                    //微信登录
                    case R.id.re_weichat:
                        login(SHARE_MEDIA.WEIXIN);
                        break;
                    //QQ登录
                    case R.id.re_qq:
                        login(SHARE_MEDIA.QQ);
                        break;
                    //微博登录
                    case R.id.re_weibo:
                        login(SHARE_MEDIA.SINA);
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnRegister.setOnClickListener(onClickListener);
        bindingView.reWeichat.setOnClickListener(onClickListener);
        bindingView.reQq.setOnClickListener(onClickListener);
        bindingView.reWeibo.setOnClickListener(onClickListener);
        addReplaceFragment(new LoginFragment(),"登录","注册");//初始化默认登录界面
        bindingView.tvOtherLoginTips.setText("快捷登录");
        showOthreLoginView(true);
    }


    @Override
    public void initData() {
        String text="登录或注册即代表阅读并同意服务条款";
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.setSpan(new TextCommentClick(CommonUtils.getColor(R.color.colorPrimary)),13,spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bindingView.tvLoginProtocol.setMovementMethod(LinkMovementMethod.getInstance());
        bindingView.tvLoginProtocol.setText(spannable);
        bindingView.tvLoginProtocol.setVisibility(View.VISIBLE);
        initSMS();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_group);
        showToolBar(false);
        mLoginPresenter = new LoginPresenter(this);
        mLoginPresenter.attachView(this);
    }

    /**
     * 初始化短信监听
     */
    private void initSMS() {
        mEventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                //回调完成
                if (result == SMSSDK.RESULT_COMPLETE) {
                    EventBus.getDefault().post(new SMSEventMessage(100,""));
                    //验证码正确
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        EventBus.getDefault().post(new SMSEventMessage(101,""));
                        //获取验证码成功
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        EventBus.getDefault().post(new SMSEventMessage(102,""));
                        //返回支持发送验证码的国家列表
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    }
                }else{
                    Throwable throwable = (Throwable) data;
                    EventBus.getDefault().post(new SMSEventMessage(99, throwable.toString()));
                }
            }
        };
        SMSSDK.registerEventHandler(mEventHandler); //注册短信回调
    }

    /**
     * 打开意图
     */
    public void openBtnAction() {
        if(TextUtils.equals("注册",bindingView.btnRegister.getText().toString())){
            addReplaceFragment(new LoginRegisterFragment(),"注册","登录");
            bindingView.tvOtherLoginTips.setText("快速注册");
        }else if(TextUtils.equals("登录",bindingView.btnRegister.getText().toString())){
            onBackPressed();
        }
    }

    /**
     * 叠加界面
     * @param fragment 片段目标
     * @param centerTitle 中间标题
     * @param rightTitle 右边小标题
     */
    public void addReplaceFragment(Fragment fragment, String centerTitle,String rightTitle) {
        bindingView.tvTitle.setText(centerTitle);
        bindingView.btnRegister.setText(rightTitle);
        try {
            android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_layout, fragment, centerTitle);
            fragmentTransaction.addToBackStack(centerTitle);
            fragmentTransaction.commit();
        }catch (Exception e){

        }
    }

    /**
     * 显示和占位第三方登录
     * @param flag
     */
    public void showOthreLoginView(boolean flag) {
        bindingView.llOtherLoginView.setVisibility(flag?View.VISIBLE:View.INVISIBLE);
        bindingView.tvLoginProtocol.setVisibility(flag?View.VISIBLE:View.GONE);
    }

    /**
     * 获取验证码
     * @param country
     * @param account
     */
    public void getCode(String country, String account) {
        SMSSDK.getVerificationCode(country, account, new OnSendMessageHandler() {
            @Override
            public boolean onSendMessage(String country, String account) {
                return false;//发送短信之前调用，返回TRUE表示无需真正发送验证码
            }
        });
    }

    public void makePasswordFinlish(String account) {
        SMSEventMessage smsEventMessage=new SMSEventMessage();
        smsEventMessage.setSmsCode(99);
        smsEventMessage.setAccount(account);
        onBackPressed();
        EventBus.getDefault().post(smsEventMessage);
    }


    private class TextCommentClick extends ClickableSpan {
        private final int color;//字体颜色
        public TextCommentClick(int color){
            this.color=color;
        }
        /**
         * 设置颜色
         * @param ds
         */
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(color);
        }
        /**
         * 点击事件的监听
         * @param widget
         */
        @Override
        public void onClick(View widget) {
            WebViewActivity.loadUrl(LoginGroupActivity.this,"http://v.nq6.com/user_services.html","新趣服务条款");
        }
    }

    //=====================================QQ、微信、微博登录========================================

    /**
     * 登录到服务器
     * @param userDataInfo
     */
    private void login(UserDataInfo userDataInfo) {
        if(null!=mLoginPresenter&&!mLoginPresenter.isLogin()){
            mLoginPresenter.qqAndWeichatLogin(userDataInfo);
        }
    }

    /**
     * 第三方账号登录成功
     */
    public void closeForResult() {
        if(null!=VideoApplication.getInstance().getUserData()&&!TextUtils.isEmpty(VideoApplication.getInstance().getUserData().getId())){
            //携带登录成功消息
            Intent intent=new Intent();
            intent.putExtra(Constant.INTENT_LOGIN_STATE,true);
            setResult(Constant.INTENT_LOGIN_RESULTCODE,intent);
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_LOGIN);
            finish();
        }else{
            ToastUtils.showCenterToast("登录异常，请重新登录!");
        }
    }

    /**
     * 手机账号登录成功
     */
    public void closeForResult(MineUserInfo.DataBean.InfoBean data) {
        //手机账号登录需要补全用户信息
        if(null!=VideoApplication.getInstance().getUserData()&&null!=data&&!TextUtils.isEmpty(VideoApplication.getInstance().getUserData().getId())){
            if(TextUtils.isEmpty(data.getGender())){
                CompleteUserDataDialogFragment fragment = CompleteUserDataDialogFragment.newInstance(data, "补全基本信息", Constant.MODE_USER_COMPLETE);
                fragment.setOnDismissListener(new CompleteUserDataDialogFragment.OnDismissListener() {
                    @Override
                    public void onDismiss(boolean change) {
                        Intent intent=new Intent();
                        intent.putExtra(Constant.INTENT_LOGIN_STATE,true);
                        setResult(Constant.INTENT_LOGIN_RESULTCODE,intent);
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_LOGIN);
                        finish();
                    }
                });
                fragment.show(getSupportFragmentManager(),"complete");
                return;
            }else{
                Intent intent=new Intent();
                intent.putExtra(Constant.INTENT_LOGIN_STATE,true);
                setResult(Constant.INTENT_LOGIN_RESULTCODE,intent);
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_LOGIN);
                finish();
                return;
            }
        }else{
            ToastUtils.showCenterToast("登录异常，请重新登录!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        //这个时候DialogFragment无法回调onActivityResult方法.由父窗体来回调结果到子View中,发出订阅的事件，如果用户信息编辑的界面已经初始化可以收到订阅消息
        MessageEvent messageEvent=new MessageEvent();
        messageEvent.setData(data);
        messageEvent.setMessage("CAMERA_REQUEST");
        messageEvent.setRequestCode(requestCode);
        messageEvent.setResultState(resultCode);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 账号密码登录
     * @param account
     * @param password
     */
    public void login(String account,String password){
        SMSEventMessage smsEventMessage=new SMSEventMessage();
        smsEventMessage.setSmsCode(98);
        smsEventMessage.setAccount(account);
        smsEventMessage.setPassword(password);
        onBackPressed();
        EventBus.getDefault().post(smsEventMessage);
    }

    /**
     * QQ、微信、微博 登录
     * @param media
     */
    public void login(SHARE_MEDIA media) {
        boolean isauth = UMShareAPI.get(LoginGroupActivity.this).isAuthorize(LoginGroupActivity.this, media);//判断当前APP有没有授权登录
        if (isauth) {
            UMShareAPI.get(LoginGroupActivity.this).getPlatformInfo(LoginGroupActivity.this, media, LoginAuthListener);//获取用户信息
        } else {
            UMShareAPI.get(LoginGroupActivity.this).doOauthVerify(LoginGroupActivity.this, media, LoginAuthListener);//用户授权登录
        }
    }


    /**
     * QQ 微信 微博 登陆后回调
     */
    UMAuthListener LoginAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            showProgressDialog("登录中，请稍后...",true,false);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            int loginType=0;
            switch (platform) {
                case QQ:
                    loginType=1;
                    break;
                case WEIXIN:
                    loginType=2;
                    break;
                case SINA:
                    loginType=3;
                    break;
            }
            try{
                if(null!=data&&data.size()>0){
                    UserDataInfo userDataInfo=new UserDataInfo();
                    userDataInfo.setIemil(GoagalInfo.get().uuid);
                    userDataInfo.setLoginType(loginType+"");
                    //新浪微博
                    if(platform== SHARE_MEDIA.SINA){
                        userDataInfo.setNickname(data.get("name"));
                        userDataInfo.setCity(data.get("location"));
                        userDataInfo.setFigureurl_qq_2(data.get("iconurl"));
                        userDataInfo.setGender(data.get("gender"));
                        userDataInfo.setProvince(data.get("location"));
                        userDataInfo.setOpenid(data.get("id"));
                        userDataInfo.setImageBG(data.get("cover_image_phone"));
                    //微信、QQ
                    }else{
                        userDataInfo.setNickname(data.get("screen_name"));
                        userDataInfo.setCity(data.get("city"));
                        userDataInfo.setFigureurl_qq_2(data.get("iconurl"));
                        userDataInfo.setGender(data.get("gender"));
                        userDataInfo.setProvince(data.get("province"));
                        userDataInfo.setOpenid(data.get("openid"));
                    }
                    //授权成功
                    if(TextUtils.isEmpty(userDataInfo.getNickname())&&TextUtils.isEmpty(userDataInfo.getFigureurl_qq_2())){
                        login(platform);
                    }else{
                        //登录App成功,防止微博
                        if(!TextUtils.isEmpty(userDataInfo.getOpenid())){
                            login(userDataInfo);
                        }else{
                            login(platform);
                        }
                    }
                }else{
                    closeProgressDialog();
                    ToastUtils.showCenterToast("登录失败，请重试!");
                }
            }catch (Exception e){
                closeProgressDialog();
                ToastUtils.showCenterToast("登录失败，请重试!");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            closeProgressDialog();
            ToastUtils.showCenterToast("登录失败，请重试!");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            closeProgressDialog();
            ToastUtils.showCenterToast("登录取消");
        }
    };



    //======================================登录到服务器回调=========================================

    @Override
    public void showQQWeichatUserData(UserData data) {
        closeProgressDialog();
        UserData.DataBean.InfoBean infoBean = data.getData().getInfo();
        VideoApplication.getInstance().setUserData(infoBean,true);
        closeForResult();
    }

    @Override
    public void showLoginError(String data) {
        closeProgressDialog();
        ToastUtils.showCenterToast(data);
    }

    @Override
    public void showLoginError() {
        closeProgressDialog();
    }


    @Override
    public void onBackPressed() {
        //只剩登录一个界面了
        if(getSupportFragmentManager().getBackStackEntryCount()==1&&!LoginGroupActivity.this.isFinishing()){
            finish();
            return;
        }
        //栈顶存在两个
        if(getSupportFragmentManager().getBackStackEntryCount()==2&&!LoginGroupActivity.this.isFinishing()){
            bindingView.tvTitle.setText("登录");
            bindingView.btnRegister.setText("注册");
            bindingView.tvOtherLoginTips.setText("快捷登录");
            showOthreLoginView(true);
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.menu_exit);//出场动画
    }

    @Override
    public void onDestroy() {
        SMSSDK.unregisterEventHandler(mEventHandler);
        super.onDestroy();
        if(null!=mLoginPresenter) mLoginPresenter.detachView();
        Runtime.getRuntime().gc();
    }
}
