package com.video.newqu.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.PhotoInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import com.video.newqu.util.ScreenUtils;
import java.util.List;

/**
 * @author TinyHung@Outlook.com
 * @version 1.0
 * @time 2016-11-06 21:30
 * @des $选择图片列表
 */
public class PhotoItemAdapter extends BaseQuickAdapter<PhotoInfo,BaseViewHolder> {

    private final int mItemWidth;

    public PhotoItemAdapter(List<PhotoInfo> photoInfoList, int screenWidth) {
        super(R.layout.lv_photo_item,photoInfoList);
        mItemWidth =(screenWidth- ScreenUtils.dpToPxInt(26))/3;
    }
    public interface  OnItemClickListener{
        void onDelete(int postion);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PhotoInfo item) {
        if(null==item) return;
        try {
            RelativeLayout view = helper.getView(R.id.re_item_icon);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.width=mItemWidth;
            layoutParams.height=mItemWidth;
            view.setLayoutParams(layoutParams);
            //封面
            Glide.with(mContext)
                    .load(item.getImagePath())
                    .crossFade()//渐变
                    .error(R.drawable.load_err)
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .into((ImageView) helper.getView(R.id.iv_icon));

            helper.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null!=mOnItemClickListener){
                        mOnItemClickListener.onDelete(helper.getPosition());
                    }
                }
            });
        }catch (Exception e){

        }
    }
}
