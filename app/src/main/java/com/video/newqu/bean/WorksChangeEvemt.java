package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/12/11.
 */

public class WorksChangeEvemt {

    private String message;

    public WorksChangeEvemt(){
        super();
    }

    public WorksChangeEvemt(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
