package com.video.newqu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityPreviewImageBinding;
import com.video.newqu.download.FileDownloadComposrTask;
import com.video.newqu.view.widget.PinchImageView;
import com.video.newqu.view.widget.PinchImageViewPager;
import java.util.ArrayList;

/**
 * @author TinyHung@Outlook.com
 * 2017-06-09 19:21
 * 查看大图
 */
public class MediaImagePreviewActivity extends BaseActivity <ActivityPreviewImageBinding>{

    private ArrayList<String> mPics;
    private boolean isNet;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requstDrawStauBar(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        showToolBar(false);
        Intent intent = getIntent();
        mPics = intent.getStringArrayListExtra("pic_list");
        isNet=intent.getBooleanExtra("isNet",false);
        mIndex = intent.getIntExtra("index",0);
        if(mPics!=null&&mPics.size()>0){
            showImage();
        }
    }

    @Override
    public void initViews() {
        if(isNet){
            bindingView.btnSave.setVisibility(View.VISIBLE);
            bindingView.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mPics&&mPics.size()>0){
                        String url = mPics.get(bindingView.viewPager.getCurrentItem());
                        new FileDownloadComposrTask(MediaImagePreviewActivity.this,url, Constant.IMAGE_PATH).start();
                    }
                }
            });
        }
    }

    @Override
    public void initData() {

    }

    /**
     * 显示图片
     */
    private void showImage() {
        bindingView.viewPager.setAdapter(new ImagePagerAdapter());
        bindingView.viewPager.setOnPageChangeListener(new PinchImageViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bindingView.tvIndexNum.setText((position+1)+"/"+mPics.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bindingView.viewPager.setOffscreenPageLimit(1);
        bindingView.viewPager.setCurrentItem(mIndex);
        bindingView.tvIndexNum.setText((mIndex+1)+"/"+mPics.size());
    }


    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPics == null ? 0 : mPics.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PinchImageView piv=new PinchImageView(MediaImagePreviewActivity.this);
            piv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            container.addView(piv);
            return piv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * 设置图片
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            PinchImageView piv = (PinchImageView) object;
            String url="";
            if(isNet){
                url=mPics.get(position);
            }else{
                url="file://"+mPics.get(position);
            }
            Glide.with(container.getContext())
                    .load(url)
                    .error(R.drawable.ic_error)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(piv);
            bindingView.viewPager.setMainPinchImageView(piv);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        bindingView.viewPager.removeAllViews();
        Runtime.getRuntime().gc();
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.screen_zoom_out);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
