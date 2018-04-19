package com.video.newqu.listener;

import com.video.newqu.bean.TopicVideoList;

/**
 * TinyHung@outlook.com
 * 2017/5/25 14:38
 * 视频条目的所有点击事件
 */
public interface TopicVideoOnItemClickListener {
    //条目点击事件
    void onItemClick(int position);
    //点赞
    void onItemPrice(int position, TopicVideoList.DataBean.VideoListBean  data);
    //评论/查看评论
    void onItemComent(int position, TopicVideoList.DataBean.VideoListBean  data, boolean isShowKeyBoard);
    //分享
    void onItemShare(int position, TopicVideoList.DataBean.VideoListBean  data);
    //菜单
    void onItemMenu(int position, TopicVideoList.DataBean.VideoListBean  data);
    //查看其他用户主页
    void onItemVisitOtherHome(int position, TopicVideoList.DataBean.VideoListBean  data);
    //关注
    void onItemFollow(int position, TopicVideoList.DataBean.VideoListBean  data);
    //子评论
    void onItemChildComent(int position, TopicVideoList.DataBean.VideoListBean data, boolean isShowKeyBoard);
    //登录
    void login();
}
