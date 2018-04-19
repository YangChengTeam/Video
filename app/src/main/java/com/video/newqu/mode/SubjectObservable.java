package com.video.newqu.mode;

import java.util.Observable;

/**
 * TinyHung@Outlook.com
 * 2018/4/12.
 * 统一的观察者订阅中心
 * 首页的登录、登出、个人中心的作品、收藏变化等
 */

public class SubjectObservable extends Observable{

    public SubjectObservable(){

    }

    public void updataSubjectObserivce(Object obj){
        setChanged();
        notifyObservers(obj);
    }
}
