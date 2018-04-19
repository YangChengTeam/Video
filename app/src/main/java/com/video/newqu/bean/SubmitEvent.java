package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017-06-02 20:10
 */

public class SubmitEvent {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;


    public SubmitEvent(){
        super();
    }
    public SubmitEvent(String message) {
        this.message = message;
    }

}
