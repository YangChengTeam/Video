package com.video.newqu.mode;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/5/24 8:58
 * GridLayout 两列列表间距
 */
public class ItemSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public ItemSpacesItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left=space;
        outRect.bottom=space;
        outRect.right=space;
        outRect.top=space;
    }
}
