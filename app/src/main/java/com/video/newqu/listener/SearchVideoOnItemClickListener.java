package com.video.newqu.listener;

import com.video.newqu.bean.SearchResultInfo;

/**
 * TinyHung@outlook.com
 * 2017/5/25 14:38
 * 视频条目的点击事件
 */
public interface SearchVideoOnItemClickListener {
    //条目点击事件
    void onItemClick(int position);
    //评论/查看评论
    void onItemComent( SearchResultInfo.DataBean.VideoListBean data, boolean isShowKeyBoard);
    //分享
    void onItemShare(SearchResultInfo.DataBean.VideoListBean data);
    //菜单
    void onItemMenu( SearchResultInfo.DataBean.VideoListBean data);
    //查看其他用户主页
    void onItemVisitOtherHome( SearchResultInfo.DataBean.VideoListBean data);
    //关注
    void onItemFollow( SearchResultInfo.DataBean.VideoListBean data);
    //子评论
    void onItemChildComent( SearchResultInfo.DataBean.VideoListBean data, boolean isShowKeyBoard);
}
