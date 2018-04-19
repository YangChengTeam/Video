package com.video.newqu.listener;

import android.support.v7.widget.RecyclerView;
import com.video.newqu.contants.RecylerConstant;

/*
* This class is a ScrollListener for RecyclerView that allows to show/hide
* views when list is scrolled. It assumes that you have added a header
* to your list. @see pl.michalz.hideonscrollexample.adapter.partone.RecyclerAdapter
* */
public abstract class RecylerViewScrollListener extends RecyclerView.OnScrollListener {


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (RecylerConstant.SCROLLEDDISTANCE > RecylerConstant.HIDE_THRESHOLD && RecylerConstant.CONTROLSVISIBLE) {
            onHide();
            RecylerConstant.CONTROLSVISIBLE = false;
            RecylerConstant.SCROLLEDDISTANCE = 0;
        } else if (RecylerConstant.SCROLLEDDISTANCE < -RecylerConstant.HIDE_THRESHOLD && !RecylerConstant.CONTROLSVISIBLE) {
            onShow();
            RecylerConstant.CONTROLSVISIBLE = true;
            RecylerConstant.SCROLLEDDISTANCE = 0;
        }
        if((RecylerConstant.CONTROLSVISIBLE && dy>0) || (!RecylerConstant.CONTROLSVISIBLE && dy<0)) {
            RecylerConstant.SCROLLEDDISTANCE += dy;
        }
    }

    public abstract void onHide();
    public abstract void onShow();
}
