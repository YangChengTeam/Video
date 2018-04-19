package com.video.newqu.util;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.SnackBarListener;

/**
 * TinyHung@outlook.com
 * 2017/3/17 16:56
 */
public class ToastUtils {

    private static TextView sMTv_text;
    private static Toast centerToast;


    public static void showCenterToast(String text) {

        if (null==centerToast) {
            centerToast = new Toast(VideoApplication.getInstance());
            centerToast.setDuration(Toast.LENGTH_SHORT);
            centerToast.setGravity(Gravity.NO_GRAVITY, 0, 0);
            View view = View.inflate(VideoApplication.getInstance(), R.layout.toast_center_layout, null);
            sMTv_text = (TextView) view.findViewById(R.id.tv_text);
            if (!TextUtils.isEmpty(text)) {
                sMTv_text.setText(text);
            } else {
                sMTv_text.setText("null");
            }
            centerToast.setView(view);

        } else {
            if (!TextUtils.isEmpty(text)) {
                sMTv_text.setText(text);
            } else {
                sMTv_text.setText("null");
            }
        }
        centerToast.show();
    }
    /**
     * 显示在底部的吐司
     * @param text
     * @param action
     * @param snackBarListener
     */
    public static void showSnackebarBottomToast( View view,String text, String action,SnackBarListener snackBarListener){

        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        //给Snackbar添加Action
        if(!TextUtils.isEmpty(action)&&null!=snackBarListener){
            snackbar.setAction(action,snackBarListener);
        }
        View snackbarView = snackbar.getView();
        //设置文字背景颜色，统一风格
        TextView snackbar_text = (TextView) snackbarView.findViewById(R.id.snackbar_text);//文字
        snackbar_text.setTextColor(CommonUtils.getColor(R.color.tips_color));
        snackbarView.setBackgroundColor(CommonUtils.getColor(R.color.qian_gray));
        Button snackbar_action = (Button) snackbarView.findViewById(R.id.snackbar_action);//Action
        snackbar_action.setTextColor(CommonUtils.getColor(R.color.common_h2));

        //绘制背景
//        int strokeWidth = Utils.dip2px(NewZoneApplication.getInstance(),1); // 1dp 边框宽度
//        int roundRadius = Utils.dip2px(NewZoneApplication.getInstance(), 3); // 3dp 圆角半径
//        int strokeColor = Color.rgb(255,255,0);//边框颜色
//        int fillColor = Color.rgb(46,179,108);//内部填充颜色
//
//        GradientDrawable gd = new GradientDrawable();//创建drawable
//        gd.setColor(fillColor);
//        gd.setCornerRadius(roundRadius);
//        gd.setStroke(strokeWidth, strokeColor);
//        snackbar_action.setBackground(gd);
        snackbar.show();
    }

    /**
     * 显示错误/正确的吐司
     * @param view
     * @param action
     * @param snackBarListener
     */
    public static void showSnackebarStateToast(View view, String action, SnackBarListener snackBarListener, int icon, String type, String message) {

        Snackbar snackbar = Snackbar.make(view, null, Snackbar.LENGTH_LONG);
        //给Snackbar添加Action
        if(!TextUtils.isEmpty(action)&&null!=snackBarListener){
            snackbar.setAction(action,snackBarListener);
        }
        View snackbarView = snackbar.getView();
        //设置文字背景颜色，统一风格
//        TextView snackbar_text = (TextView) snackbarView.findViewById(R.id.snackbar_text);//文字

        snackbarView.setBackgroundColor(CommonUtils.getColor(R.color.qian_gray));//255,78,92
//        if(TextUtils.equals(type, Constant.SNACKBAR_ERROR)){
//            snackbar_text.setTextColor(CommonUtils.getColor(R.color.tips_color));
//        }else if(TextUtils.equals(type, Constant.SNACKBAR_DONE)){
//            snackbar_text.setTextColor(CommonUtils.getColor(R.color.green));
//        }

        Button snackbar_action = (Button) snackbarView.findViewById(R.id.snackbar_action);//Action
        snackbar_action.setTextColor(CommonUtils.getColor(R.color.common_h2));
        //给Snackbar添加布局文件
        Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout)snackbarView;
        View inflateView = LayoutInflater.from(snackbarView.getContext()).inflate(R.layout.snackbar_error_layout, null);
        TextView tv_message = (TextView) inflateView.findViewById(R.id.tv_message);
        if(TextUtils.equals(type, Constant.SNACKBAR_ERROR)){
            tv_message.setTextColor(CommonUtils.getColor(R.color.tips_color));
        }else if(TextUtils.equals(type, Constant.SNACKBAR_DONE)){
            tv_message.setTextColor(CommonUtils.getColor(R.color.green));
        }
        tv_message.setText(message);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER_HORIZONTAL;
        snackbarLayout.addView(inflateView,0,layoutParams);
        ((ImageView) snackbarLayout.findViewById(R.id.iv_icon)).setImageResource(icon);
        snackbar.show();
    }


    /**
     * 统一的网络设置入口
     */
    public static  void showNetWorkTips(final Activity context, String action, String message){

        ToastUtils.showSnackebarStateToast(context.getWindow().findViewById(Window.ID_ANDROID_CONTENT), action, new SnackBarListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                context.startActivity(intent);
            }
        }, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR, message);
    }

    /**
     * 失败吐司
     * @param context
     * @param action
     * @param snackBarListener
     * @param message
     */
    public static  void showErrorToast(Activity context,String action, SnackBarListener snackBarListener, String message){
        ToastUtils.showSnackebarStateToast(context.getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
    }

    /**
     * 成功吐司
     * @param action
     * @param snackBarListener
     * @param message
     */
    public static  void showFinlishToast(Activity context,String action, SnackBarListener snackBarListener, String message){
        ToastUtils.showSnackebarStateToast(context.getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE,message);
    }
}
