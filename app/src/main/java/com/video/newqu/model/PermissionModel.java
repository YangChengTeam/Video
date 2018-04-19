package com.video.newqu.model;

/**
 * TinyHung@Outlook.com
 * 2017/12/1.
 */

public class PermissionModel {
    /**
     * 请求的权限
     */
    public String permission;

    /**
     * 解析为什么请求这个权限
     */
    public String explain;

    /**
     * 请求代码
     */
    public int requestCode;

    public PermissionModel(String permission, String explain, int requestCode) {
        this.permission = permission;
        this.explain = explain;
        this.requestCode = requestCode;
    }
}
