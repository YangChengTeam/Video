package com.video.newqu.ui.fragment;

import android.content.Intent;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.video.newqu.R;
import com.video.newqu.base.BaseDialogFragment;
import com.video.newqu.camera.auth.SignerTest;
import com.video.newqu.databinding.FragmentKsyAuthorizeSettingBinding;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.LoadingButton;
import java.util.Iterator;
import java.util.Map;

/**
 * TinyHung@outlook.com
 * 2018/4/25 14:01
 * 金山云授权检查、设置
 */

public class KsyAuthorizeSettingFragment extends BaseDialogFragment<FragmentKsyAuthorizeSettingBinding,MainPresenter> {

    public static KsyAuthorizeSettingFragment newInstance() {
        KsyAuthorizeSettingFragment fragment=new KsyAuthorizeSettingFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ksy_authorize_setting;
    }

    @Override
    public void initViews() {
        boolean checkNetwork = Utils.isCheckNetwork();
        bindingView.tvTipsMsg.setText(Html.fromHtml("合成权限授予失败!原因："+"<font color='#e95b55'>"+(checkNetwork?"所连接的网络不可用":"设备网络未连接")+"</font>"+" 导致视频编辑权限被拒绝，请尝试连接可用网络后重试！"));
        bindingView.btnOpenWlan.setOnClickSubmitListener(new LoadingButton.OnClickSubmitListener() {
            @Override
            public void onSubmit(View view) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                startActivity(intent);
            }
        });
        bindingView.btnRetryAuthorize.setOnClickSubmitListener(new LoadingButton.OnClickSubmitListener() {
            @Override
            public void onSubmit(View view) {
                if(!AuthInfoManager.getInstance().getAuthState()){
                    bindingView.btnRetryAuthorize.startSubmitAnimation();
                    authorizeKsyPower();
                }else{
                    ToastUtils.showCenterToast("已授予视频合并权限");
                }
            }
        });
        bindingView.btnClose.setOnClickSubmitListener(new LoadingButton.OnClickSubmitListener() {
            @Override
            public void onSubmit(View view) {
                dismiss();
            }
        });
        bindingView.bottomSheet.getBackground().setAlpha(239);
    }

    /**
     * 开始想金山云重新获取视频合并权限
     */
    private void authorizeKsyPower() {
        try {
            if (!AuthInfoManager.getInstance().getAuthState()) {
                Map<String, Map<String, Object>> stringMapMap = new SignerTest().generateAuthHeader();
                Iterator<Map.Entry<String, Map<String, Object>>> iterator = stringMapMap.entrySet().iterator();
                Map.Entry<String, Map<String, Object>> next = iterator.next();
                Map<String, Object> stringObjectMap = stringMapMap.get(next.getKey());
                if (0 == (int) stringObjectMap.get("RetCode")) {
                    String date = (String) stringObjectMap.get("x-amz-date");
                    String Authorization = (String) stringObjectMap.get("Authorization");
                    AuthInfoManager.getInstance().setAuthInfo(Authorization, date);
                    AuthInfoManager.getInstance().addAuthResultListener(new AuthInfoManager.CheckAuthResultListener() {
                        @Override
                        public void onAuthResult(final int i) {
                            if(null!=getActivity()&&!getActivity().isFinishing()){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bindingView.btnRetryAuthorize.stopSubmitAnimation();
                                        if(1==i){
                                            bindingView.btnRetryAuthorize.setText("授权成功");
                                            return;
                                        }else{
                                            bindingView.btnRetryAuthorize.setText("授权失败，点击重试");
                                            return;
                                        }
                                    }
                                });
                            }
                        }
                    });
                    //开始向KSServer申请鉴权
                    AuthInfoManager.getInstance().checkAuth();
                }
            }
        }catch (Exception e){
            bindingView.btnRetryAuthorize.stopSubmitAnimation();
            bindingView.btnRetryAuthorize.setText("授权失败，点击重试");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
