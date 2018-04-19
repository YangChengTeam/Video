
package com.video.newqu.manager;

import com.video.newqu.VideoApplication;
import com.video.newqu.bean.SearchAutoResult;
import com.video.newqu.contants.Constant;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.Logger;
import com.video.newqu.util.SharedPreferencesUtil;

import java.util.List;

/**
 * 缓存管理者
 */
public class SearchCacheManager {

    private static SearchCacheManager manager;

    public static SearchCacheManager getInstance() {
        return manager == null ? (manager = new SearchCacheManager()) : manager;
    }

    /**
     * 全部搜索类型词条
     * @return
     */
    public List<SearchAutoResult> getAllSearchHistory() {
        return SharedPreferencesUtil.getInstance().getObject(getAllSearchHistoryKey(), List.class);
    }

    public synchronized void saveAllSearchHistory(Object obj) {
        SharedPreferencesUtil.getInstance().putObject(getAllSearchHistoryKey(), obj);
    }

    private String getAllSearchHistoryKey() {
        return "allSearchHistory";
    }

    /**
     * 搜索用户类型词条
     */

    public List<SearchAutoResult> getUserSearchHistory() {
        return SharedPreferencesUtil.getInstance().getObject(getUserSearchHistoryKey(), List.class);
    }

    public synchronized void saveUserSearchHistory(Object obj) {
        SharedPreferencesUtil.getInstance().putObject(getUserSearchHistoryKey(), obj);
    }

    private String getUserSearchHistoryKey() {
        return "searchHistory";
    }




    /**
     * 获取缓存大小
     *
     * @return
     */
    public synchronized String getCacheSize() {
        long cacheSize = 0;

        try {
            String cacheDir = Constant.BASE_PATH;
            cacheSize += FileUtils.getFolderSize(cacheDir);
            if (FileUtils.isSdCardAvailable()) {
                String extCacheDir = VideoApplication.getInstance().getExternalCacheDir().getPath();
                cacheSize += FileUtils.getFolderSize(extCacheDir);
            }
        } catch (Exception e) {

        }

        return FileUtils.formatFileSizeToString(cacheSize);
    }

    /**
     * 保存历史搜索记录
     * @param list
     * @param type 0:全部搜索类型词条 1:用户词条
     */
    public synchronized void saveHistoryList(List<SearchAutoResult> list, int type) {
        if(0==type){
            saveAllSearchHistory(list);
        }else if(1==type){
            saveUserSearchHistory(list);
        }
    }

    /**
     * 返回搜索记录，根据类型返回
     * @param type 0:
     */
    public synchronized List<SearchAutoResult> getSearchHistoeyList(int type) {
        if(0==type){
            return getAllSearchHistory();
        }else if(1==type){
            return getUserSearchHistory();
        }
        return null;
    }
}
