package com.video.newqu.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.bean.TopicList;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.util.ToastUtils;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/6/26 19:35
 * 话题列表适配器
 */

public class TopicListAdapter extends BaseQuickAdapter<TopicList.DataBean,BaseViewHolder>{

    private int topicMax;
    private int[] TextBgColors=new int[]{R.drawable.list_top_item_selector1,R.drawable.list_top_item_selector2,R.drawable.list_top_item_selector3};
    public void setMaxTopicNum(int topicMax) {
        this.topicMax=topicMax;
    }

    public interface  OnItemClickListener{
        void onItemClick();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public TopicListAdapter(List<TopicList.DataBean> topList) {
        super(R.layout.recyler_topic_item,topList);
    }


    @Override
    protected void convert(BaseViewHolder helper, TopicList.DataBean item) {
        if(null==item) return;
        TextView tv = helper.getView(R.id.tv_item_title);
        View view = helper.getView(R.id.item_view);
        view.setSelected(item.isSelector());
        tv.setText(TextUtils.isEmpty(item.getTopic())?"":"#"+item.getTopic()+"#");
        view.setOnClickListener(new OnOtemClickListener(view,tv,item));
    }

    /**
     * 点击事件
     */
    private class OnOtemClickListener implements View.OnClickListener{

        private final View view;
        private final TopicList.DataBean mData;
        private final TextView tv;

        public OnOtemClickListener(View view,TextView tv, TopicList.DataBean data) {
            this.view=view;
            this.mData=data;
            this.tv=tv;
        }

        @Override
        public void onClick(View v) {
            List<TopicList.DataBean> data = TopicListAdapter.this.getData();
            if(null!=data&&data.size()>0){
                if(mData.isSelector()){
                    view.setSelected(!mData.isSelector());
                    tv.setSelected(!mData.isSelector());
                    mData.setSelector(!mData.isSelector());
                    isBack=true;
                }else{
                    if(selectorCount>=topicMax){
                        ToastUtils.showCenterToast("最多只能选择三个话题");
                    }else{
                        if(null!=tv&&null!=view){
                            switch (selectorCount) {
                                case 0:
                                    view.setBackgroundResource(TextBgColors[0]);
                                    break;
                                case 1:
                                    if(isBack){
                                        view.setBackgroundResource(TextBgColors[1-1]);
                                    }else{
                                        view.setBackgroundResource(TextBgColors[1]);
                                    }
                                    break;
                                case 2:
                                    if(isBack){
                                        view.setBackgroundResource(TextBgColors[2-1]);
                                    }else{
                                        view.setBackgroundResource(TextBgColors[2]);
                                    }
                                    break;
                            }
                            view.setSelected(!mData.isSelector());
                            tv.setSelected(!mData.isSelector());
                            mData.setSelector(!mData.isSelector());
                        }
                    }
                }
                selectorCount=0;
            }
            if(null!=mOnItemClickListener) mOnItemClickListener.onItemClick();
            if(null!=data&&data.size()>0){
                for (TopicList.DataBean dataBean : data) {
                    if(dataBean.isSelector()){
                        selectorCount++;
                    }
                }
                if(0==selectorCount){
                    isBack=false;
                }
            }
        }
    }
    private int selectorCount=0;
    private boolean isBack=false;
}
