package com.video.newqu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.bean.MineTabInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/5/24
 * 个人界面横向标题栏,适配器
 */
public class MineMenuAdapter extends BaseAdapter {

    private List<MineTabInfo> mineTabInfoList;
    private Context mContext;

    public MineMenuAdapter(Context context, List<MineTabInfo> mineTabInfoList) {
        this.mContext = context;
        this.mineTabInfoList=mineTabInfoList;
    }

    @Override
    public int getCount() {
        return mineTabInfoList==null?0:mineTabInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null==mineTabInfoList?null:mineTabInfoList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(null==convertView){
            convertView = View.inflate(mContext, R.layout.list_mine_tab_item, null);
            viewHolder=new ViewHolder();
            viewHolder.tv_item_title= (TextView) convertView.findViewById(R.id.tv_item_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        MineTabInfo mineTabInfo = mineTabInfoList.get(position);
        if(null!=mineTabInfo){
            viewHolder.tv_item_title.setText(mineTabInfo.getAboutCount()+" "+mineTabInfo.getTitleName());
            viewHolder.tv_item_title.setSelected(mineTabInfo.isSelector());
        }
        return convertView;
    }

    public void setNewData(List<MineTabInfo> mineTabInfos) {
        this.mineTabInfoList=mineTabInfos;
    }

    private class ViewHolder{
        private TextView tv_item_title;
    }
}
