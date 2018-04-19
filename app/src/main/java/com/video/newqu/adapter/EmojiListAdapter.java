package com.video.newqu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.ChatEmoji;
import com.video.newqu.model.GlideRoundTransform;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/27.
 * 表情面板
 */

public class EmojiListAdapter extends BaseAdapter {

    private final Context mContext;
    private final  List<ChatEmoji> mEmojiLists;
    private final LayoutInflater mLayoutInflater;

    public EmojiListAdapter(Context context,  List<ChatEmoji> emojiLists) {
        this.mContext=context;
        this.mEmojiLists=emojiLists;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return null==mEmojiLists?0:mEmojiLists.size();
    }

    @Override
    public Object getItem(int position) {
        return null==mEmojiLists?null:mEmojiLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null==convertView){
            convertView=mLayoutInflater.inflate(R.layout.list_item_face,null);
            viewHolder=new ViewHolder();
            viewHolder.item_iv_face= (ImageView) convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        ChatEmoji chatEmoji = mEmojiLists.get(position);
        //视频封面
        Glide.with(mContext)
                .load(chatEmoji.getId())
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideRoundTransform(mContext))
                .into( viewHolder.item_iv_face);

        return convertView;
    }

    public  List<ChatEmoji> getData() {
        return mEmojiLists;
    }
    private class ViewHolder{
        ImageView item_iv_face;
    }
}
