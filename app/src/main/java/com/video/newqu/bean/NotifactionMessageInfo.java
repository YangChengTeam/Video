package com.video.newqu.bean;

import com.video.newqu.comadapter.entity.MultiItemEntity;
import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/7/19 16:57
 */

public class NotifactionMessageInfo implements MultiItemEntity,Serializable{

    /**
     * user_id : 1060153
     * itemType : 2
     * cover : http://app.nq6.com/Upload/Cover/1060153/8878ad5c4abcde1246e525dd6464d8f4_275.jpg
     * video_id : 380
     * desp : 南湖公园
     * nickname : 皇太极
     * logo : http://app.nq6.com//Upload/Picture/2017-07-14/5967ab31c011a.jpg
     * type : 2
     * add_time : 1500426091
     */


    public static final int ITEM_1=1;//关注
    public static final int ITEM_2=2;//收藏
    public static final int ITEM_3=3;//留言
    public static final int ITEM_4=4;//二次留言

    private Long id;
    private String user_id;
    private int itemType;
    private String cover;
    private String video_id;
    private String desp;
    private String nickname;
    private String logo;
    private String type;
    private String add_time;
    private boolean isRead;
    private String comment;
    private String from_nickname;
    private String from_user_id;
    private String webUrl;
    private String topic;
    private int msg_type=0;//默认的消息类型,0：通知，1：自定义消息

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getFrom_nickname() {
        return from_nickname;
    }

    public void setFrom_nickname(String from_nickname) {
        this.from_nickname = from_nickname;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
