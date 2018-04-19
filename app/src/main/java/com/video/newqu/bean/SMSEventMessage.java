package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/11/28.
 */

public class SMSEventMessage {

    private int smsCode;
    private String message;
    private String account;
    private String password;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public SMSEventMessage(){
        super();
    }

    public SMSEventMessage(int smsCode, String message) {
        this.smsCode = smsCode;
        this.message = message;
    }

    public int getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(int smsCode) {
        this.smsCode = smsCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
