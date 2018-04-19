package com.video.newqu.event;

/**
 * TinyHung@outlook.com
 * 2017/5/24 15:52
 * 事件总线
 */
public class VerticalPlayMessageEvent {

    private String authorID;
    private String userCover;
    private String userName;
    private int poistion;
    private String userGender;

    public VerticalPlayMessageEvent(){
        super();
    }

    public VerticalPlayMessageEvent(String authorID, String userCover, String userName, int poistion,String userGender) {
        this.authorID = authorID;
        this.userCover = userCover;
        this.userName = userName;
        this.poistion = poistion;
        this.userGender=userGender;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getUserCover() {
        return userCover;
    }

    public void setUserCover(String userCover) {
        this.userCover = userCover;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPoistion() {
        return poistion;
    }

    public void setPoistion(int poistion) {
        this.poistion = poistion;
    }
    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

}
