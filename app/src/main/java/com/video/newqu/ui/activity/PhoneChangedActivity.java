package com.video.newqu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityPhoneChangedBinding;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.ui.fragment.PhoneBindingFragment;
import com.video.newqu.ui.fragment.PhoneCheckedFragment;
import com.video.newqu.util.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * TinyHung@Outlook.com
 * 2017/11/19
 * 手机号码校验、绑定
 */

public class PhoneChangedActivity extends BaseActivity<ActivityPhoneChangedBinding>{

    private EventHandler mEventHandler;

    @Override
    public void initViews() {
        setTitle(getIntent().getStringExtra(Constant.KEY_TITLE));
        int fragmentType = getIntent().getIntExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_PHONE_BINDING);
        switch (fragmentType) {
            //手机号码绑定
            case Constant.FRAGMENT_TYPE_PHONE_BINDING:
                replaceFragment(R.id.frame_layout,PhoneBindingFragment.newInstance(getIntent().getStringExtra(Constant.KEY_CONTENT_EXTRA)));
                break;
            //手机号码校验
            case Constant.FRAGMENT_TYPE_PHONE_CHECKED:
                replaceFragment(R.id.frame_layout,PhoneCheckedFragment.newInstance(getIntent().getStringExtra(Constant.KEY_CONTENT_EXTRA),getIntent().getStringExtra(Constant.KEY_PHONE)));
                break;
        }
    }

    @Override
    public void initData() {
        mEventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                //回调完成
                if (result == SMSSDK.RESULT_COMPLETE) {
                    Log.d("RegisterActivity", "afterEvent:回调完成 ");
                    mHandler.sendEmptyMessage(100);
                    //验证码正确
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        Log.d("RegisterActivity", "afterEvent:提交验证码成功 ");
                        mHandler.sendEmptyMessage(101);
                        //获取验证码成功
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Log.d("RegisterActivity", "afterEvent:获取验证码成功 ");
                        mHandler.sendEmptyMessage(102);
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    Log.d("RegisterActivity", "afterEvent:错误 ");
                    Message message=Message.obtain();
                    message.what=99;
                    message.obj=data.toString();
                    mHandler.sendMessage(message);
                }
            }
        };
        SMSSDK.registerEventHandler(mEventHandler); //注册短信回调
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_changed);

    }


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                //短信验证码验证成功
                case 100:

                    break;
                //获取验证码成功
                case 102:
                    ApplicationManager.getInstance().observerUpdataToMusic(Constant.OBSERVABLE_ACTION_SEND_FINLISH_SMS);
                    ToastUtils.showCenterToast("已成功发送验证码");
                    break;
                //短信验证码已提交完成
                case 101:
                    ApplicationManager.getInstance().observerUpdataToMusic(Constant.OBSERVABLE_ACTION_CHECKED_PHONE);
                    break;
                //失败
                case 99:
                    try {
                        String data = (String) msg.obj;
                        if(!TextUtils.isEmpty(data)){
                            try {
                                JSONObject jsonObject=new JSONObject(data);
                                if(null!=jsonObject&&jsonObject.length()>0){
                                    ToastUtils.showCenterToast(jsonObject.getString("detail"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){

                    }
                    ApplicationManager.getInstance().observerUpdataToMusic(Constant.OBSERVABLE_ACTION_SEND_SMS_ERROR);
                    break;
            }
        }
    };

    /**
     * 切换Fragment
     * @param fragment
     */
    public void replaceFragment( Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commitAllowingStateLoss();
        setTitle("绑定手机号码");
    }

    @Override
    public void onDestroy() {
        SMSSDK.unregisterEventHandler(mEventHandler);
        super.onDestroy();
        ApplicationManager.getInstance().removeAllObserverToMusic();
    }

    /**
     * 绑定、验证手机号码成功
     * @param phone
     */
    public void hendleFinlish(String phone) {
        Intent intent=new Intent();
        intent.putExtra("phone",phone);
        setResult(Constant.MEDIA_BINDING_PHONE_RESULT,intent);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,R.anim.menu_exit);//出场动画
    }

    public void submitCode(String phone,String code) {
        SMSSDK.submitVerificationCode("86", phone, code);
    }
}
