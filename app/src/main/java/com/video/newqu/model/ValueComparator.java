package com.video.newqu.model;

import com.video.newqu.bean.UploadVideoInfo;

import java.util.Comparator;
import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2017/11/23.
 */

public class ValueComparator implements Comparator<UploadVideoInfo>{

    Map<String, Long> base;
    //这里需要将要比较的map集合传进来
    public ValueComparator(Map<String, Long> base) {
        this.base = base;
    }

    @Override
    public int compare(UploadVideoInfo o1, UploadVideoInfo o2) {
        if (base.get(o1.getItemType()) >= base.get(o2.getItemType())) {
            return 1;
        } else {
            return 0;
        }
    }
}
