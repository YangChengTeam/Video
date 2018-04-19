package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/12/6.
 */

public class MessageVerticalPlayEvent {

    private String message;
    private int messageCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    public MessageVerticalPlayEvent(String message, int messageCode) {
        this.message = message;
        this.messageCode = messageCode;
    }

    public MessageVerticalPlayEvent(){
        super();
    }

}
