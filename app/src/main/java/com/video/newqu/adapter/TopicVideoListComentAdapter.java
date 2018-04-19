package com.video.newqu.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.bean.TopicVideoList;
import com.video.newqu.listener.TopicClickListener;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.TextViewTopicSpan;
import java.net.URLDecoder;
import java.util.List;


/**
 * @time 2017/5/22 22:10
 * @des $视频列表评论列表
 */
public class TopicVideoListComentAdapter extends BaseAdapter {

    private final TopicClickListener topicClickListener;
    private final List<TopicVideoList.DataBean.VideoListBean.CommentListBean> comentContentList;
    private LayoutInflater mInflater;

    public TopicVideoListComentAdapter(Context context,List<TopicVideoList.DataBean.VideoListBean.CommentListBean> comentContentList, TopicClickListener topicClickListener) {
        this.comentContentList=comentContentList;
        mInflater = LayoutInflater.from(context);
        this.topicClickListener=topicClickListener;
    }



    @Override
    public int getCount() {
        return comentContentList==null?0:comentContentList.size()>=2?2:comentContentList.size();
    }

    @Override
    public Object getItem(int position) {
        return comentContentList==null?null:comentContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuGridViewHolder holder;
        if(null==convertView){
            convertView=mInflater.inflate(R.layout.video_list_coment_item,null);
            holder=new MenuGridViewHolder();
            holder.tv_item_name= (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.tv_item_content= (TextView) convertView.findViewById(R.id.tv_item_content);
            convertView.setTag(holder);
        }else{
            holder= (MenuGridViewHolder) convertView.getTag();
        }
        try {
            TopicVideoList.DataBean.VideoListBean.CommentListBean commentListBean = comentContentList.get(position);
            if(null!=commentListBean){
                holder.tv_item_name.setText(commentListBean.getNickname()+" : ");
                String decode = URLDecoder.decode(commentListBean.getComment(), "utf-8");
                SpannableString topicStyleContent = TextViewTopicSpan.getTopicStyleContent(decode, CommonUtils.getColor(R.color.app_text_style),  holder.tv_item_content, topicClickListener,null);
                holder.tv_item_content.setText(topicStyleContent);
            }
        }catch (Exception e){

        }

        return convertView;
    }
    private class MenuGridViewHolder{
        private TextView tv_item_name;
        private TextView tv_item_content;
    }
}
