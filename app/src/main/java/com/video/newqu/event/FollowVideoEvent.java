package com.video.newqu.event;

/**
 * TinyHung@outlook.com
 * 2017/6/5 8:55
 * 关注列表时间接受
 */
public class FollowVideoEvent {
    private String message;

    public FollowVideoEvent(){
        super();
    }

    public FollowVideoEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
