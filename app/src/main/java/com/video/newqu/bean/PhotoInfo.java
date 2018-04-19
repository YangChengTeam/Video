package com.video.newqu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2017/8/15
 */

public class PhotoInfo implements Parcelable {

    private String imagePath;
    private String imageMd5Key;
    private String id;
    private boolean isSelector;

    public PhotoInfo(){
        super();
    }
    public PhotoInfo(String id,String imagePath, String imageMd5Key,boolean isSelector) {
        this.imagePath = imagePath;
        this.imageMd5Key = imageMd5Key;
        this.id=id;
        this.isSelector=isSelector;
    }


    protected PhotoInfo(Parcel in) {
        imagePath = in.readString();
        imageMd5Key = in.readString();
        id = in.readString();
        isSelector = in.readByte() != 0;
    }

    public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {
        @Override
        public PhotoInfo createFromParcel(Parcel in) {
            return new PhotoInfo(in);
        }

        @Override
        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    };

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageMd5Key() {
        return imageMd5Key;
    }

    public void setImageMd5Key(String imageMd5Key) {
        this.imageMd5Key = imageMd5Key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
        dest.writeString(imageMd5Key);
        dest.writeString(id);
        dest.writeByte((byte) (isSelector ? 1 : 0));
    }
}
