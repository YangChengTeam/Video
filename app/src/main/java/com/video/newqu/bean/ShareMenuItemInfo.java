package com.video.newqu.bean;

/**
 * @time 2017/3/6 18:11
 * @des $弹窗菜单的对象
 */
public class ShareMenuItemInfo {
    private String itemName;
    private int itemLogo;

    public ShareMenuItemInfo(){
        super();
    }
    public ShareMenuItemInfo(String itemName, int itemLogo) {
        this.itemName = itemName;
        this.itemLogo = itemLogo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemLogo() {
        return itemLogo;
    }

    public void setItemLogo(int itemLogo) {
        this.itemLogo = itemLogo;
    }

    @Override
    public String toString() {
        return "HomeMenuInfo{" +
                "itemName='" + itemName + '\'' +
                ", itemLogo=" + itemLogo +
                '}';
    }
}
