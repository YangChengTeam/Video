
package com.video.newqu.ui.presenter;

import android.content.Context;

import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.ShareContract;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 分享
 */
public class SharePresenter extends RxPresenter<ShareContract.View> implements ShareContract.Presenter<ShareContract.View> {

    private final Context context;
    public SharePresenter(Context context){
        this.context=context;
    }

    @Override
    public void shareResult(String userID, String videoID, String type) {
        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("video_id",videoID);
        params.put("share_type",type);
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "share", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                if(null!=mView) mView.showShareResulet(data);
            }
        });
        addSubscrebe(subscribe);
    }
}
