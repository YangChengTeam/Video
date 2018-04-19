package com.video.newqu.listener;

import com.video.newqu.bean.FansInfo;
import com.video.newqu.bean.FollowUserList;

/**
 * TinyHung@outlook.com
 * 2017/6/15 15:47
 * 针对于粉丝列表和关注列表的点击事件
 */
public interface OnFansClickListener {

    void onItemClick(int position);
    void onFollowFans(int position, FansInfo.DataBean.ListBean data);
    void onFollowUser(int position, FollowUserList.DataBean.ListBean data);
    void onMenuClick(FollowUserList.DataBean.ListBean data);
}
