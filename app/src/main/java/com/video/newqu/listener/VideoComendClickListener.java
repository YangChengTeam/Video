package com.video.newqu.listener;

import com.video.newqu.bean.ComentList;

/**
 * TinyHung@outlook.com
 * 2017/7/5 14:38
 */
public interface VideoComendClickListener {
    void onAuthorIconClick(String userID);
    void onAuthorItemClick(ComentList.DataBean.CommentListBean data);
}
