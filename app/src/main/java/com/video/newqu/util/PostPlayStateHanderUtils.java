package com.video.newqu.util;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.PlayCountInfo;
import com.video.newqu.bean.UserVideoPlayerList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.listener.OnPostPlayStateListener;
import com.video.newqu.manager.ApplicationManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/7/8 11:02
 * 上传播放记录
 */

public class PostPlayStateHanderUtils {

    private static PostPlayStateHanderUtils mInstance;
    private boolean isLoading;

    public static synchronized  PostPlayStateHanderUtils getInstance(){
        synchronized (PostPlayStateHanderUtils.class){
            if(null==mInstance){
                mInstance=new PostPlayStateHanderUtils();
            }
        }
        return mInstance;
    }

    /**
     * 上传播放的记录
     * @param actionData 封装好的用户播放行为记录
     */
    public  void postVideoPlayState(final UserVideoPlayerList actionData, final OnPostPlayStateListener onPostPlayStateListener) {
        if(null==actionData) return;
        //判断本地有没有上报过播放记录
        if(1==actionData.getState()){
            List<UserVideoPlayerList> allUserPlayerActionList = ApplicationManager.getInstance().getUserPlayerActionDB().getAllUserPlayerActionList();
            if(null!=allUserPlayerActionList&&allUserPlayerActionList.size()>0){
                for (UserVideoPlayerList userVideoPlayerList : allUserPlayerActionList) {
                    if(null!=userVideoPlayerList){
                        //已经存在用户行为的播放记录
                        if(TextUtils.equals(actionData.getVideoID(),userVideoPlayerList.getVideoID())){
                            return;
                        }
                    }
                }
            }
        }
        if(isLoading)return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("video_id", actionData.getVideoID());
        params.put("imeil", VideoApplication.mUuid);
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("play_state",actionData.getState()+"");
        params.put("state",actionData.getState()+"");

        HttpCoreEngin.get(VideoApplication.getInstance().getApplicationContext()).rxpost(NetContants.BASE_VIDEO_HOST + "play_record", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isLoading=false;
                if(TextUtils.isEmpty(data)){
                    return;
                }
                if(1==actionData.getState()){
                    ApplicationManager.getInstance().getUserPlayerActionDB().insertNewPlayerHistoryOfObject(actionData);
                }
//                try {
//                    JSONObject jsonObject=new JSONObject(data);
//                    if(1==jsonObject.getInt("code")){
//                        PlayCountInfo playCountInfo = new Gson().fromJson(data, PlayCountInfo.class);
//                        if(null!=playCountInfo&&null!=playCountInfo.getData()){
//                            PlayCountInfo.DataBean.InfoBean info = playCountInfo.getData().getInfo();
//                            if(null!=info){
//                                if(null!=onPostPlayStateListener){
//                                    onPostPlayStateListener.onPostPlayStateComple(info.getPlaty_times()+"");
//                                }
//                            }
//                        }
//                    }else{
//                        if(null!=onPostPlayStateListener){
//                            onPostPlayStateListener.onPostPlayStateError();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    if(null!=onPostPlayStateListener){
//                        onPostPlayStateListener.onPostPlayStateError();
//                    }
//                }
            }
        });
    }
}
