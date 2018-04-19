package com.video.newqu.event;

/**
 * TinyHung@outlook.com
 * 2017-05-25 0:24
 * 我的界面，界面刷新消息
 */

public class MineUpdataMessage {

    private String message;

    public MineUpdataMessage(){
        super();
    }

    public MineUpdataMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
