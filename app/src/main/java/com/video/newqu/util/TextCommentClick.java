package com.video.newqu.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/6/27 15:49
 * 通用的部分文字变色可点击
 */
public class TextCommentClick extends ClickableSpan {


    private final int color;//字体颜色


    public TextCommentClick(int color){
        this.color=color;
    }

    /**
     * 设置颜色
     * @param ds
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(color);
    }

    /**
     * 点击事件的监听
     * @param widget
     */
    @Override
    public void onClick(View widget) {

    }
}
