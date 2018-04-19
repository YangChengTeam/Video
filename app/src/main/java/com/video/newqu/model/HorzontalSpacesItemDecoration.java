package com.video.newqu.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/5/24 8:58
 * GridLayout 两列列表间距
 */
public class HorzontalSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = HorzontalSpacesItemDecoration.class.getSimpleName();
    private int space;

    public HorzontalSpacesItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom=space;
        outRect.right=space;
        outRect.top=space;
        if(parent.getChildLayoutPosition(view)==0){
            outRect.left=(space+space);
        }else{
            outRect.left=space;
        }
    }
}
