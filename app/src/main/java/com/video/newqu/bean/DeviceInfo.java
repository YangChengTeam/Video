package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017/7/7 15:06
 * 设备信息
 */
public class DeviceInfo {

    private String brand;
    private String location_longitude;
    private String location_latitude;
    private String model;
    private String imeil;
    private String sdk_ini;
    private String app_ini;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(String location_longitude) {
        this.location_longitude = location_longitude;
    }

    public String getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(String location_latitude) {
        this.location_latitude = location_latitude;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImeil() {
        return imeil;
    }

    public void setImeil(String imeil) {
        this.imeil = imeil;
    }

    public String getSdk_ini() {
        return sdk_ini;
    }

    public void setSdk_ini(String sdk_ini) {
        this.sdk_ini = sdk_ini;
    }

    public String getApp_ini() {
        return app_ini;
    }

    public void setApp_ini(String app_ini) {
        this.app_ini = app_ini;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "brand='" + brand + '\'' +
                ", location_longitude='" + location_longitude + '\'' +
                ", location_latitude='" + location_latitude + '\'' +
                ", model='" + model + '\'' +
                ", imeil='" + imeil + '\'' +
                ", sdk_ini='" + sdk_ini + '\'' +
                ", app_ini='" + app_ini + '\'' +
                '}';
    }
}
