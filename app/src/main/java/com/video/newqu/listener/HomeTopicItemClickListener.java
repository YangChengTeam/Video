package com.video.newqu.listener;

/**
 * TinyHung@outlook.com
 * 2017/6/27 21:09
 * 发现外部点击监听
 */
public interface HomeTopicItemClickListener {
    void onGroupItemClick(String topicID);
    void onChildItemClick(String topicID,int groupPoistion,int childPoistion);
}
