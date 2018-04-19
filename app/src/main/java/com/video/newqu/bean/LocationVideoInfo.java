package com.video.newqu.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@outlook.com
 * 2017/7/6 12:00
 */
public class LocationVideoInfo implements Parcelable{


    private String videoPath;
    private String durtaion;
    private Bitmap videoThbun;
    private boolean isSelector;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getDurtaion() {
        return durtaion;
    }

    public void setDurtaion(String durtaion) {
        this.durtaion = durtaion;
    }

    public Bitmap getVideoThbun() {
        return videoThbun;
    }

    public void setVideoThbun(Bitmap videoThbun) {
        this.videoThbun = videoThbun;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }


    public static final Parcelable.Creator<LocationVideoInfo> CREATOR = new Creator<LocationVideoInfo>() {

        public LocationVideoInfo createFromParcel(Parcel source) {
            LocationVideoInfo locationVideoInfo = new LocationVideoInfo();
            locationVideoInfo.videoPath = source.readString();
            locationVideoInfo.durtaion = source.readString();
            locationVideoInfo.isSelector=source.readByte()!=0;
//            locationVideoInfo.videoThbun = Bitmap.CREATOR.createFromParcel(source);
            return locationVideoInfo;
        }
        public LocationVideoInfo[] newArray(int size) {
            return new LocationVideoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(videoPath);
        parcel.writeString(durtaion);
        parcel.writeByte((byte)(isSelector?1:0));
//        videoThbun.writeToParcel(parcel, 0);//大小超过限制
    }
}
