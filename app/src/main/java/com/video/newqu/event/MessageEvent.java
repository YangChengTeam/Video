package com.video.newqu.event;

import android.content.Intent;

/**
 * TinyHung@outlook.com
 * 2017/5/24 15:52
 * 事件总线
 */
public class MessageEvent {

    private String message;
    private int type;
    private int extar;
    private int requestCode;
    private int resultState;
    private Intent data;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultState() {
        return resultState;
    }

    public void setResultState(int resultState) {
        this.resultState = resultState;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    public int getExtar() {
        return extar;
    }

    public void setExtar(int extar) {
        this.extar = extar;
    }

    public MessageEvent(){
        super();
    }
    public MessageEvent(String message) {
        this.message = message;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
