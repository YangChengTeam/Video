package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivitySplashBinding;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;

/**
 * TinyHung@outlook.com
 * 2017/5/20 12:20
 * 开屏页
 */

public class SplashActivity extends AppCompatActivity{

    private ActivitySplashBinding bindingView=null;

    /**
     * 避免BUG，1000以内的数值
     */
    private final static int READ_PHONE_STATE_CODE = 101;//读取手机唯一识别身份
    private final static int WRITE_EXTERNAL_STORAGE_CODE = 102;//SD卡
    /**
     * 向用户申请的权限列表
     */
    private static PermissionModel[] models = new PermissionModel[] {
            new PermissionModel(Manifest.permission.READ_PHONE_STATE, "我们需要读取手机信息的权限来标识您的身份", READ_PHONE_STATE_CODE),
            new PermissionModel(Manifest.permission.WRITE_EXTERNAL_STORAGE, "为方便我们存储临时数据和保障部分功能的正常使用，我们需要您允许我们读写你的存储卡", WRITE_EXTERNAL_STORAGE_CODE),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        bindingView.ivLogo.setImageResource(R.drawable.splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < 23) {
            goToNextActivity();
            return;
        }
        checkPermissions();//安卓6.0申请权限
    }

    private void goToNextActivity() {
        //至少是第二次启动了
        if(VideoApplication.mToday ==SharedPreferencesUtil.getInstance().getInt(Constant.SETTING_SPLASH, 0)){
            Class clazz;
            if(SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_FIRST_START)){
                clazz=MainActivity.class;
            }else{
                clazz=GuideActivity.class;//GuideActivity
            }
            Intent intent=new Intent(SplashActivity.this,clazz);
            startActivity(intent);
            this.finish();
        }else{
            SharedPreferencesUtil.getInstance().putInt(Constant.GRADE_PLAYER_VIDEO_COUNT,0);
            AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
            animation.setDuration(2200);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    SharedPreferencesUtil.getInstance().putInt(Constant.SETTING_SPLASH,VideoApplication.mToday);
                    startNextActivity();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            bindingView.splashRootviewRl.startAnimation(animation);
        }
    }

    /**
     * Android 6.0+上运行时申请权限
     *
     */
    private void checkPermissions() {
        try {
            for (PermissionModel model : models) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, model.permission)) {
                    ActivityCompat.requestPermissions(this, new String[]{model.permission}, model.requestCode);
                    return;
                }
            }
            // 到这里就表示所有需要的权限已经通过申
            goToNextActivity();
        } catch (Throwable e) {

        }
    }

    /**
     * 申请结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case READ_PHONE_STATE_CODE:
            case WRITE_EXTERNAL_STORAGE_CODE:
//            case REQUEST_PERMISSION_CODE:
                // 如果用户不允许，我们视情况发起二次请求或者引导用户到应用页面手动打开
                if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {

                    // 二次请求，表现为：以前请求过这个权限，但是用户拒接了
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        new AlertDialog.Builder(this).setTitle("权限申请失败").setMessage(findPermissionExplain(permissions[0]))
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermissions();
                                    }
                                }).show();
                    }
                    // 到这里就表示已经是第3+次请求，让用户自己手动打开
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this)
                                .setTitle("权限申请失败")
                                .setMessage("部分权限被拒绝获取，没有授予权限将无法使用后续功能，是否立即前往设置中心授予本软件存储权限?");
                        builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SystemUtils.getInstance().startAppDetailsInfoActivity(SplashActivity.this,123);
                            }
                        });
                        builder.show();
                    }
                    return;
                }
                // 到这里就表示用户允许了本次请求，继续检查是否还有待申请的权限没有申请
                if (isAllRequestedPermissionGranted()) {
                    goToNextActivity();
                } else {
                    checkPermissions();
                }
                break;
        }
    }

    private String findPermissionExplain(String permission) {
        if (models != null) {
            for (PermissionModel model : models) {
                if (model != null && model.permission != null && model.permission.equals(permission)) {
                    return model.explain;
                }
            }
        }
        return null;
    }

    private boolean isAllRequestedPermissionGranted() {
        for (final PermissionModel model : models) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, model.permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 123:
                if (isAllRequestedPermissionGranted()) {
                    goToNextActivity();
                } else {
                    checkPermissions();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public static class PermissionModel {
        /**
         * 请求的权限
         */
        public String permission;

        /**
         * 解析为什么请求这个权限
         */
        public String explain;

        /**
         * 请求代码
         */
        public int requestCode;

        public PermissionModel(String permission, String explain, int requestCode) {
            this.permission = permission;
            this.explain = explain;
            this.requestCode = requestCode;
        }
    }


    /**
     * 启动到下到下一页
     */
    private void startNextActivity(){
        Class clazz;
        if(SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_FIRST_START)){
            clazz=MainActivity.class;
        }else{
            clazz=GuideActivity.class;
        }
        Intent intent=new Intent(SplashActivity.this,clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bindingView=null;
        Runtime.getRuntime().gc();
    }
}
