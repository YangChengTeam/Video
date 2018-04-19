package com.video.newqu.manager;

import android.content.Context;
import android.util.Log;
import com.video.newqu.bean.WeiChactVideoInfo;
import com.video.newqu.bean.WeiChactVideoInfoDao;
import com.video.newqu.dao.DBBaseDao;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/10 8:48
 * 扫描的微信视频批量上传数据库管理者
 */
public class DBBatchVideoUploadManager extends DBBaseDao<WeiChactVideoInfo> {

    public DBBatchVideoUploadManager(Context context) {
        super(context);
    }

    /**
     * 通过ID查询对象
     * @param id
     * @return
     */
    private WeiChactVideoInfo loadById(long id){

        return daoSession.getWeiChactVideoInfoDao().load(id);
    }

    /**
     * 获取某个对象的主键ID
     * @param weiChactVideoInfo
     * @return
     */
    private long getID(WeiChactVideoInfo weiChactVideoInfo){
        return daoSession.getWeiChactVideoInfoDao().getKey(weiChactVideoInfo);
    }

    /**
     * 通过videoID获取UploadVideoInfo对象
     * @return
     */
    private List<WeiChactVideoInfo> getVideoInfoByName(String md5Key){
        QueryBuilder queryBuilder =  daoSession.getWeiChactVideoInfoDao().queryBuilder();
        queryBuilder.where(WeiChactVideoInfoDao.Properties.Video_durtion.eq(md5Key));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    /**
     * 通过文件MD5 Key 获取所有ID
     * @return
     */
    private List<String> getIdByName(String md5Key){
        List<WeiChactVideoInfo> students = getVideoInfoByName(md5Key);
        List<String> ids = new ArrayList<String>();
        int size = students.size();
        if (size > 0){
            for (int i = 0;i < size;i++){
                ids.add(students.get(i).getFileKey());
            }
            return ids;
        }else{
            return null;
        }
    }



    /**
     * 根据ID进行数据库的删除操作
     * @param id
     */
    public void deleteById(long  id){
        Log.d(TAG, "deleteById: 删除元素");
        daoSession.getWeiChactVideoInfoDao().deleteByKey(id);
    }

    /**
     * 根据ID同步删除数据库操作
     * @param ids
     */
    private void deleteByIds(List<Long> ids){

        daoSession.getWeiChactVideoInfoDao().deleteByKeyInTx(ids);
    }

    /**
     * 根据对象插入一条消息
     * @param weiChactVideoInfo
     */
    public boolean insertNewUploadVideoInfo(WeiChactVideoInfo weiChactVideoInfo) {
        WeiChactVideoInfoDao uploadVideoInfoDao = daoSession.getWeiChactVideoInfoDao();
        WeiChactVideoInfo unique = uploadVideoInfoDao.queryBuilder().where(WeiChactVideoInfoDao.Properties.Video_durtion.eq(weiChactVideoInfo.getVideo_durtion())).unique();
        if(null==unique){
            Log.d(TAG, "insertNewUploadVideoInfo: 插入一条上传记录");
            uploadVideoInfoDao.insertInTx(weiChactVideoInfo);
            return true;
        }else{
            Log.d(TAG, "insertNewUploadVideoInfo: 上传记录已存在");
           return false;
        }
    }


    /**
     * 获取所有上传列表
     * @return
     */

    public synchronized List<WeiChactVideoInfo> getUploadVideoList(){
        return daoSession.getWeiChactVideoInfoDao().queryBuilder().orderAsc(WeiChactVideoInfoDao.Properties.Video_durtion).list();
    }

    /**
     * 根据对象删除一上传记录
     *
     * @param weiChactVideoInfo
     */
    public void deleteUploadVideoInfo(WeiChactVideoInfo weiChactVideoInfo) {
        daoSession.getWeiChactVideoInfoDao().delete(weiChactVideoInfo);
    }


    /**
     * 更新一条消息
     * @param weiChactVideoInfo
     */
    public synchronized void updateUploadVideoInfo(WeiChactVideoInfo weiChactVideoInfo) {
        Log.d(TAG,"更新="+weiChactVideoInfo.getFileKey());
        daoSession.getWeiChactVideoInfoDao().update(weiChactVideoInfo);
    }

    /**
     * 删除所有消息记录
     */
    public void deteleAllUploadList() {
        daoSession.getWeiChactVideoInfoDao().deleteAll();
    }

    /**
     * 分页加载数据
     * @param page 页数
     * @param count 一页中的条数 加载第几页中的多少条数据
     */
    public List<WeiChactVideoInfo> queryUploadListOfPage(int page, int count) {
        return daoSession.getWeiChactVideoInfoDao().queryBuilder().offset((page-1)*count).limit(count).list();
    }

}
