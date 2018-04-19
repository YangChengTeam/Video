package com.video.newqu.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.video.newqu.R;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityGuideBinding;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.SharedPreferencesUtil;

/**
 * @version 1.0
 * @outhor TingHung@Outlook.com
 * @time 2017-05-26 17:51
 * 第一次进入欢迎界面轮播图
 */

public class GuideActivity extends AppCompatActivity {

    private ActivityGuideBinding bindingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bindingView = DataBindingUtil.setContentView(GuideActivity.this, R.layout.activity_guide);
        initViews();
    }


    private void initViews() {
        bindingView.vpGank.setAdapter(mPageAdapter);
        bindingView.vpGank.fixScrollSpeed(800);
        bindingView.vpGank.addOnPageChangeListener(mPageListener);
        bindingView.liDost.removeAllViews();

        addDots();
        mPageListener.onPageSelected(0);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_canel:
                    case R.id.bt_skip:
                        startActivityNext();
                        break;
                }
            }
        };
        bindingView.btnCanel.setOnClickListener(onClickListener);
        bindingView.btSkip.setOnClickListener(onClickListener);
    }



    /**
     * 过场动画适配器
     */
    private PagerAdapter mPageAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return Cheeses.GUIDE_IMAGE.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return  view == object;
        }

        // 预加载页面用到的方法
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setImageResource(Cheeses.GUIDE_IMAGE[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);
            return  imageView;
        }

        // 销毁页面用到的方法
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };

    /**
     * 过场动画监听器
     */
    private ViewPager.OnPageChangeListener mPageListener =new ViewPager.SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            //切换显示状态
            for (int i = 0; i < mPageAdapter.getCount(); i++) {
                bindingView.liDost.getChildAt(i).setEnabled(i != position);
            }
            switchButtomState(position==mPageAdapter.getCount() -1);
            if(position==Cheeses.GUIDE_IMAGE.length-1){
                bindingView.btnCanel.setVisibility(View.INVISIBLE);
            }else{
                bindingView.btnCanel.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 更具是否是最后一页来显示状态
     * @param isLastCureenItem
     */
    private void switchButtomState(boolean isLastCureenItem) {
        bindingView.btSkip.setVisibility(isLastCureenItem?View.VISIBLE:View.INVISIBLE);
    }



    @Override
    public void onBackPressed() {
        startActivityNext();
    }

    /**
     * 去到下一个界面
     */
    private void startActivityNext() {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_FIRST_START,true);
        startActivity(MainActivity.class);
    }

    /**
     * 打开新界面
     * @param clazz
     */
    private void startActivity(Class clazz){
        Intent intent=new Intent();
        intent.setClass(GuideActivity.this,clazz);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
    }


    /**
     * 添加小圆点
     */
    private void addDots() {
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        for (int i = 0; i < Cheeses.GUIDE_IMAGE.length; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(margin, margin);
            lp.setMargins(margin, margin, margin, margin);
            lp.width= ScreenUtils.dpToPxInt(6);
            lp.height= ScreenUtils.dpToPxInt(6);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.arl_orgin_dot_selector);
            bindingView.liDost.addView(dot);
            dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int child = bindingView.liDost.indexOfChild(v);
                    bindingView.vpGank.setCurrentItem(child);
                }
            });
        }
        bindingView.liDost.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
