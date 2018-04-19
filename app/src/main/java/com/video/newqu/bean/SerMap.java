package com.video.newqu.bean;

import java.io.Serializable;
import java.util.HashMap;

/**
 * TinyHung@outlook.com
 * 2017/6/21 13:18
 */
public class SerMap implements Serializable {

    public SerMap(){
        super();
    }

    public HashMap<String,String> map;

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
}
