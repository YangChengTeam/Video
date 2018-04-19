package com.video.newqu.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.video.newqu.R;

/**
 * TinyHung@Outlook.com
 * 2018/3/13.
 * 首页的底部TAB
 * 调用者控制是否启用重复点击刷新功能
 */

public class HomeTabItem extends FrameLayout {

    private static final String TAG = "HomeTabItem";
    private boolean isRefresh;//是否支持重复点击刷新
    private int mCureenViewIndex=0;//当前显示的Index
    private RadioButton[] mRadioButtons;
    private TextView mTvMsgCount;

    public HomeTabItem(@NonNull Context context) {
        super(context);
    }

    public HomeTabItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_home_table,this);
        initViews();
    }

    private void initViews() {
        OnClickListener onTabClickListener=new OnClickListener() {
            @Override
            public void onClick(View view) {
                int childViewIndex=0;
                switch (view.getId()) {
                    case R.id.rb_home:
                        childViewIndex=0;
                        break;
                    case R.id.rb_mine:
                        childViewIndex=1;
                        break;
                }
                //将再次点击事件拦截，用于处理刷新
                if(isRefresh&&mCureenViewIndex==childViewIndex&&null!=mOnTabChangeListene){
                    mOnTabChangeListene.onRefresh(childViewIndex);
                    return;
                }

                if(null!=mOnTabChangeListene){
                    mOnTabChangeListene.onChangeed(childViewIndex);
                }
                mCureenViewIndex=childViewIndex;
            }
        };
        RadioButton rbHome = (RadioButton) findViewById(R.id.rb_home);
        RadioButton rbMine = (RadioButton) findViewById(R.id.rb_mine);
        rbHome.setOnClickListener(onTabClickListener);
        rbMine.setOnClickListener(onTabClickListener);
        findViewById(R.id.re_menu_camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnTabChangeListene){
                    mOnTabChangeListene.onTakePicture();
                }
            }
        });
        mRadioButtons=new RadioButton[2];
        mRadioButtons[0]=rbHome;
        mRadioButtons[1]=rbMine;
        mRadioButtons[0].setChecked(true);
        mTvMsgCount = (TextView) findViewById(R.id.tv_menu_mine_msg_count);
    }


    /**
     * 设置是否支持重复点击刷新功能
     * @param flag
     */
    public void setDoubleRefresh(boolean flag){
        this.isRefresh=flag;
    }

    /**
     * 设置选中的TAB
     * @param index
     */
    public void setCurrentIndex(int index){
        if(mCureenViewIndex==index) return;
        if(null!=mRadioButtons&&mRadioButtons.length>0){
            mRadioButtons[mCureenViewIndex].setChecked(false);
            mRadioButtons[index].setChecked(true);
        }
        mCureenViewIndex=index;
        if(null!=mOnTabChangeListene){
            mOnTabChangeListene.onChangeed(index);
        }
    }

    public void setMessageVisibility(boolean flag) {
        if(null!=mTvMsgCount) mTvMsgCount.setVisibility(flag?VISIBLE:GONE);
    }

    public void setMessageContent(String text) {
        if(null!=mTvMsgCount) mTvMsgCount.setText(text);
    }

    public void onDestroy() {
        mRadioButtons[mCureenViewIndex].setChecked(false);
        mCureenViewIndex=0;
        mRadioButtons[mCureenViewIndex].setChecked(true);
    }

    public interface OnTabChangeListene{
        void onChangeed(int index);
        void onRefresh(int index);
        void onTakePicture();//拍照
    }

    private OnTabChangeListene mOnTabChangeListene;

    public void setOnTabChangeListene(OnTabChangeListene onTabChangeListene) {
        mOnTabChangeListene = onTabChangeListene;
    }
}
