package com.video.newqu.bean;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/6/20 18:09
 */
public class NumberCountryInfo implements Serializable{

    /**
     * zone : 590
     * rule : ^\d+
     */

    private String zone;
    private String rule;

    public NumberCountryInfo(String zone, String sortLetters, String rule, String countryName) {
        this.zone = zone;
        this.sortLetters = sortLetters;
        this.rule = rule;
        this.countryName = countryName;
    }

    private String sortLetters; //显示数据拼音的首字母
    private String countryName;

    public NumberCountryInfo(){
        super();
    }
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }



    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
