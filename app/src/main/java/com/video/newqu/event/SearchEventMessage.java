package com.video.newqu.event;

/**
 * TinyHung@outlook.com
 * 2017/6/8 18:37
 */
public class SearchEventMessage {
    private String message;

    public SearchEventMessage(){
        super();
    }
    public SearchEventMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
