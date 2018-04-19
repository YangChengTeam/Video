package com.video.newqu.util;

import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.video.newqu.camera.auth.SignerTest;
import java.util.Iterator;
import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2017/11/3.
 * 获得金山云短视频拍摄编辑使用权限
 */

public class KSYAuthorPermissionsUtil {

    private static boolean isAuthorPermissions;

    public static void init() {
        try {
            if (!AuthInfoManager.getInstance().getAuthState()) {
                if(isAuthorPermissions) return;
                isAuthorPermissions=true;
                Map<String, Map<String, Object>> stringMapMap = new SignerTest().generateAuthHeader();
                Iterator<Map.Entry<String, Map<String, Object>>> iterator = stringMapMap.entrySet().iterator();
                Map.Entry<String, Map<String, Object>> next = iterator.next();
                Map<String, Object> stringObjectMap = stringMapMap.get(next.getKey());
                if (0 == (int) stringObjectMap.get("RetCode")) {
                    String date = (String) stringObjectMap.get("x-amz-date");
                    String Authorization = (String) stringObjectMap.get("Authorization");
                    AuthInfoManager.getInstance().setAuthInfo(Authorization, date);
                    AuthInfoManager.getInstance().addAuthResultListener(new AuthInfoManager.CheckAuthResultListener() {
                        @Override
                        public void onAuthResult(int i) {
                            isAuthorPermissions=false;
                        }
                    });
                    //开始向KSServer申请鉴权
                    AuthInfoManager.getInstance().checkAuth();
                }
            }
        }catch (Exception e){
            isAuthorPermissions=false;
        }
    }
}
