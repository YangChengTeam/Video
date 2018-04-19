package com.video.newqu.bean;

import android.support.annotation.NonNull;
import com.video.newqu.comadapter.entity.MultiItemEntity;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * TinyHung@Outlook.com
 * 2018/4/14
 * 贴纸
 */
@Entity
public class StickerDataBean implements Serializable,MultiItemEntity,Comparable<StickerDataBean>{

    @Id(autoincrement = true)
    private Long ID;
    private String id;
    private String title;
    private String src;
    private String desp;
    private String type_id;
    private String sort;
    private String add_time;
    private String add_date;
    private String down_num;
    private boolean isSelector;
    private boolean isDownloading;
    private int itemType;
    private long updataTime;

    @Generated(hash = 1549405638)
    public StickerDataBean(Long ID, String id, String title, String src,
            String desp, String type_id, String sort, String add_time,
            String add_date, String down_num, boolean isSelector,
            boolean isDownloading, int itemType, long updataTime) {
        this.ID = ID;
        this.id = id;
        this.title = title;
        this.src = src;
        this.desp = desp;
        this.type_id = type_id;
        this.sort = sort;
        this.add_time = add_time;
        this.add_date = add_date;
        this.down_num = down_num;
        this.isSelector = isSelector;
        this.isDownloading = isDownloading;
        this.itemType = itemType;
        this.updataTime = updataTime;
    }

    @Generated(hash = 76278545)
    public StickerDataBean() {
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public long getUpdataTime() {
        return this.updataTime;
    }

    public void setUpdataTime(long updataTime) {
        this.updataTime = updataTime;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean getIsDownloading() {
        return this.isDownloading;
    }

    public void setIsDownloading(boolean isDownloading) {
        this.isDownloading = isDownloading;
    }

    public boolean getIsSelector() {
        return this.isSelector;
    }

    public void setIsSelector(boolean isSelector) {
        this.isSelector = isSelector;
    }

    public String getDown_num() {
        return this.down_num;
    }

    public void setDown_num(String down_num) {
        this.down_num = down_num;
    }

    public String getAdd_date() {
        return this.add_date;
    }

    public void setAdd_date(String add_date) {
        this.add_date = add_date;
    }

    public String getAdd_time() {
        return this.add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getType_id() {
        return this.type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getDesp() {
        return this.desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
    @Override
    public int compareTo(@NonNull StickerDataBean o) {
        return o.getUpdataTime()>this.getUpdataTime()?1:0;
    }
}
