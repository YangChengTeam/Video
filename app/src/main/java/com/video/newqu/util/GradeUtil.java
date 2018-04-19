package com.video.newqu.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.contants.Constant;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.dialog.StoreGradeDialog;

/**
 * TinyHung@Outlook.com
 * 2017/11/28
 * 引导用户去应用商店评分
 */

public class GradeUtil {

    /**
     * @param context
     * 用户在升级版本后，如果从未拒绝的话，就提示用户
     */
    public static void init(final Context context) {
        //如果是新版本&&用户没有拒绝过&&非第一次打开程序
        if(SharedPreferencesUtil.getInstance().getInt(Constant.GRADE_VERSTION_CODE)!=Utils.getVersionCode()
                &&!SharedPreferencesUtil.getInstance().getBoolean(Constant.GRADE_CATION,false)
                &&SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_FIRST_START_GRADE, false)){
            StoreGradeDialog storeGradeDialog=new StoreGradeDialog(context);
            storeGradeDialog.setOnItemClickListener(new StoreGradeDialog.OnItemClickListener() {

                @Override
                public void onCancel() {
                    //今后永久不再提示
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.GRADE_CATION,true);
                }

                @Override
                public void onService() {
                    MobclickAgent.onEvent(context, "start_servicer_addmsg");
                    Intent intent=new Intent(context, ContentFragmentActivity.class);
                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.KEY_FRAGMENT_SERVICES);
                    intent.putExtra(Constant.KEY_TITLE,"反馈中心");
                    context.startActivity(intent);
                }

                @Override
                public void onGood() {
                    MobclickAgent.onEvent(context, "start_market_score");
                    try {
                        Uri uri = Uri.parse("market://details?id="+ Utils.getAppProcessName(context));
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (Exception e){

                    }
                }
            });
            storeGradeDialog.show();
            SharedPreferencesUtil.getInstance().putInt(Constant.GRADE_VERSTION_CODE,Utils.getVersionCode());//这个版本不会再提示了
        }
    }
}
