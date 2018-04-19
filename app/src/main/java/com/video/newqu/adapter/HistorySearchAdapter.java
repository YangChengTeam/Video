package com.video.newqu.adapter;

import android.view.View;
import com.video.newqu.R;
import com.video.newqu.bean.SearchAutoResult;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import java.util.List;

/**
 * @time 2017/3/7 16:49
 * @des $搜索界面自动补全列表适配器
 */
public class HistorySearchAdapter extends BaseQuickAdapter<SearchAutoResult,com.video.newqu.comadapter.BaseViewHolder> {


    public HistorySearchAdapter(List<SearchAutoResult> data) {
        super(R.layout.list_search_actocomple_item_layout, data);
    }

    public interface OnDeleteOneItemListener{
        void onDeleteOneData(int poistion);
        void onSearch(String key,int type);
    }
    private OnDeleteOneItemListener mOnDeleteOneItemListener;

    public void setOnDeleteOneItemListener(OnDeleteOneItemListener onDeleteOneItemListener) {
        mOnDeleteOneItemListener = onDeleteOneItemListener;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final SearchAutoResult item) {
        try {
            if(null!=item){
                helper.setText(R.id.tv_item_title,item.getKey());
                helper.setImageResource(R.id.iv_item_icon,0==item.getType()?R.drawable.iv_search_author:1==item.getType()?R.drawable.iv_search_video:R.drawable.iv_search_list);
                //删除单条
                helper.setOnClickListener(R.id.iv_item_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mOnDeleteOneItemListener){
                            mOnDeleteOneItemListener.onDeleteOneData(helper.getAdapterPosition());
                        }
                    }
                });
                helper.setOnClickListener(R.id.re_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mOnDeleteOneItemListener){
                            mOnDeleteOneItemListener.onSearch(item.getKey(),item.getType());
                        }
                    }
                });
            }
        }catch (Exception e){

        }
    }
}
