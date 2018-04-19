package com.video.newqu.bean;

import com.video.newqu.comadapter.entity.MultiItemEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/6/28 15:44
 */
@Entity
public class MessageListInfo implements MultiItemEntity,Serializable{


//    public static final int ITEM_1=1;//关注
//    public static final int ITEM_2=2;//收藏
//    public static final int ITEM_3=3;//留言
//    public static final int ITEM_4=4;//二次留言

    @Id(autoincrement = true)
    private Long id;
    private Long add_time;
    private int is_follow;
    private int itemType;
    private String logo;
    private String nickname;
    private String type;
    private String user_id;
    private String comment;
    private String cover;
    private String desp;
    private String video_id;
    private String from_nickname;
    private boolean isRead;//是否已读

    @Generated(hash = 1512817294)
    public MessageListInfo(Long id, Long add_time, int is_follow, int itemType,
            String logo, String nickname, String type, String user_id,
            String comment, String cover, String desp, String video_id,
            String from_nickname, boolean isRead) {
        this.id = id;
        this.add_time = add_time;
        this.is_follow = is_follow;
        this.itemType = itemType;
        this.logo = logo;
        this.nickname = nickname;
        this.type = type;
        this.user_id = user_id;
        this.comment = comment;
        this.cover = cover;
        this.desp = desp;
        this.video_id = video_id;
        this.from_nickname = from_nickname;
        this.isRead = isRead;
    }

    @Generated(hash = 1851479960)
    public MessageListInfo() {
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getFrom_nickname() {
        return this.from_nickname;
    }

    public void setFrom_nickname(String from_nickname) {
        this.from_nickname = from_nickname;
    }

    public String getVideo_id() {
        return this.video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getDesp() {
        return this.desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getIs_follow() {
        return this.is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public Long getAdd_time() {
        return this.add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
