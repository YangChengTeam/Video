
package com.video.newqu.ui.presenter;

import android.content.Context;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.TopicList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.TopicContract;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 获取关注着
 */
public class TopicPresenter extends RxPresenter<TopicContract.View> implements TopicContract.Presenter<TopicContract.View> {

    private final Context context;

    public TopicPresenter(Context context){
        this.context=context;
    }

    @Override
    public void getTopicList() {
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "get_topic", TopicList.class, null,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<TopicList>() {
            @Override
            public void call(TopicList data) {
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0){
                    if(null!=mView) mView.showTopicListFinlish(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()<=0){
                    if(null!=mView) mView.showTopicListEmpty("话题列表为空");
                }else{
                    if(null!=mView) mView.showTopicListError("错误");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
