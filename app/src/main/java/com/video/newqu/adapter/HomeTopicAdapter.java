package com.video.newqu.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.video.newqu.R;
import com.video.newqu.bean.FindVideoListInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.listener.HomeTopicItemClickListener;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/10/20.
 * 首页话题适配器
 */

public class HomeTopicAdapter extends BaseQuickAdapter<FindVideoListInfo.DataBean,BaseViewHolder> {


    private final HomeTopicItemClickListener mOHomeTopicItemClickListener;

    public HomeTopicAdapter(List<FindVideoListInfo.DataBean> data, HomeTopicItemClickListener oHomeTopicItemClickListener) {
        super(R.layout.re_home_topic_item, data);
        this.mOHomeTopicItemClickListener = oHomeTopicItemClickListener;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final FindVideoListInfo.DataBean item) {
        if(null!=item){
            helper.setText(R.id.tv_item_title, TextUtils.isEmpty(item.getTopic())?"精选":item.getTopic());
            List<FindVideoListInfo.DataBean.VideosBean> videos = item.getVideos();
            if(null!=videos&&videos.size()>0){
                RecyclerView item_recyler_view = helper.getView(R.id.item_recyler_view);
                HomeTopicListAdapter homeTopicListAdapter=new HomeTopicListAdapter(videos);
                item_recyler_view.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
                item_recyler_view.setHasFixedSize(true);
                item_recyler_view.setNestedScrollingEnabled(true);

                homeTopicListAdapter.setOnItemClickListener(new HomeTopicListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int poistion) {
                        if(null!=mOHomeTopicItemClickListener){
                            mOHomeTopicItemClickListener.onChildItemClick(item.getTopic(),helper.getPosition(),poistion);
                        }
                    }
                });
                item_recyler_view.setAdapter(homeTopicListAdapter);
            }
            helper.setOnClickListener(R.id.re_top_title, new OnItemClickListener(item.getTopic()));
        }
    }



    private class OnItemClickListener implements View.OnClickListener{

        private final String topic;

        public OnItemClickListener(String topic) {
            this.topic=topic;
        }

        @Override
        public void onClick(View view) {
            if(null!=mOHomeTopicItemClickListener){
                mOHomeTopicItemClickListener.onGroupItemClick(topic);
            }
        }
    }
}
