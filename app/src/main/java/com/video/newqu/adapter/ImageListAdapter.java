package com.video.newqu.adapter;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.PhotoInfo;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.comadapter.BaseViewHolder;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/8/15
 */

public class ImageListAdapter extends BaseQuickAdapter<PhotoInfo,BaseViewHolder>{

    private final int mItemHeight;
    private int mNum=0;
    private int maxNum=3;

    public interface OnItemChangeListener{
        void onChange(int num);
    }

    private OnItemChangeListener mOnItemChangeListener;

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        mOnItemChangeListener = onItemChangeListener;
    }

    public ImageListAdapter(List<PhotoInfo> data, int screenWidth) {
        super(R.layout.recyler_image_list_item, data);
        mItemHeight=(screenWidth- 12)/3;
    }


    public void setMaxNum(int num){
        this.maxNum=num;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoInfo item) {
        try {
            if(null!=item){
                RelativeLayout re_item_icon = helper.getView(R.id.re_item_icon_layout);
                RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams)re_item_icon.getLayoutParams();
                linearParams.height = mItemHeight;
                re_item_icon.setLayoutParams(linearParams);
                AppCompatCheckBox checkbox = (AppCompatCheckBox) helper.getView(R.id.checkbox);
                checkbox.setChecked(item.isSelector());
                View view = helper.getView(R.id.view_selector);
                view.setVisibility(item.isSelector()?View.VISIBLE:View.GONE);
                //封面
                Glide.with(mContext)
                        .load(item.getImagePath())
                        .crossFade()//渐变
                        .error(R.drawable.load_err)
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .into((ImageView) helper.getView(R.id.iv_item_icon_layout));
                //同步
                helper.setOnClickListener(R.id.checkbox, new OnItemClickListenet(view,checkbox,item));
                helper.setOnClickListener(R.id.iv_item_icon_layout, new OnItemClickListenet(view,checkbox,item));
            }
        }catch (Exception e){

        }
    }


    private class OnItemClickListenet implements View.OnClickListener{

        private final AppCompatCheckBox checkbox;
        private final PhotoInfo item;
        private final View mView;

        public OnItemClickListenet(View view,AppCompatCheckBox checkbox, PhotoInfo item) {
            this.checkbox=checkbox;
            this.item=item;
            this.mView=view;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkbox:
                case R.id.iv_item_icon_layout:
                    //已选中的直接反选
                    if(item.isSelector()){
                        item.setSelector(item.isSelector() ? false : true);
                        checkbox.setChecked(item.isSelector());
                        mView.setVisibility(item.isSelector()?View.VISIBLE:View.GONE);
                        //未选中的判断选中数量上限
                    }else{
                        mNum=0;
                        List<PhotoInfo> photoInfos = ImageListAdapter.this.getData();
                        if(null!=photoInfos&&photoInfos.size()>0){
                            for (int i = 0; i < photoInfos.size(); i++) {
                                if(photoInfos.get(i).isSelector()){
                                    mNum++;
                                }
                            }
                            if(mNum<maxNum){//超出最大上限不用理会
                                item.setSelector(item.isSelector() ? false : true);
                                checkbox.setChecked(item.isSelector());
                                mView.setVisibility(item.isSelector()?View.VISIBLE:View.GONE);
                            }
                        }
                    }
                    mNum=0;
                    //获取最终的已选择图片的数量
                    List<PhotoInfo> photoInfos = ImageListAdapter.this.getData();
                    if(null!=photoInfos&&photoInfos.size()>0){
                        for (int i = 0; i < photoInfos.size(); i++) {
                            if(photoInfos.get(i).isSelector()){
                                mNum++;
                            }
                        }
                        if (null != mOnItemChangeListener) mOnItemChangeListener.onChange(mNum);
                    }
                    break;
            }
        }
    }
}
