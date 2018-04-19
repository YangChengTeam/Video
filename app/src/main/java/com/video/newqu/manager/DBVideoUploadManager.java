package com.video.newqu.manager;

import android.content.Context;
import android.util.Log;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.bean.UploadVideoInfoDao;
import com.video.newqu.dao.DBBaseDao;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/10 8:48
 * 上传视频信息记录
 */

public class DBVideoUploadManager extends DBBaseDao<UploadVideoInfo> {


    public DBVideoUploadManager(Context context) {
        super(context);
    }

    /**
     * 通过ID查询对象
     * @return
     */
    public UploadVideoInfo loadById(long id){

        return daoSession.getUploadVideoInfoDao().load(id);
    }

    /**
     * 获取某个对象的主键ID
     * @param uploadVideoInfo
     * @return
     */
    private long getID(UploadVideoInfo uploadVideoInfo){
        return daoSession.getUploadVideoInfoDao().getKey(uploadVideoInfo);
    }

    /**
     * 通过videoID获取UploadVideoInfo对象
     * @return
     */
    private List<UploadVideoInfo> getVideoInfoByName(String id){
        QueryBuilder queryBuilder =  daoSession.getUploadVideoInfoDao().queryBuilder();
        queryBuilder.where(UploadVideoInfoDao.Properties.Id.eq(id));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    /**
     * 通过AddTime名字获取MessageListInfo对象
     * @return
     */
    private List<Long> getIdByName(String key){
        List<UploadVideoInfo> students = getVideoInfoByName(key);
        List<Long> ids = new ArrayList<Long>();
        int size = students.size();
        if (size > 0){
            for (int i = 0;i < size;i++){
                ids.add(students.get(i).getId());
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
    public void deleteById(long id){

        daoSession.getUploadVideoInfoDao().deleteByKey(id);
    }

    /**
     * 根据ID同步删除数据库操作
     * @param ids
     */
    private void deleteByIds(List<Long> ids){

        daoSession.getUploadVideoInfoDao().deleteByKeyInTx(ids);
    }

    /**
     * 根据对象插入一条消息
     * @param uploadVideoInfo
     */
    public boolean insertNewUploadVideoInfo(UploadVideoInfo uploadVideoInfo) {
        UploadVideoInfoDao uploadVideoInfoDao = daoSession.getUploadVideoInfoDao();
        UploadVideoInfo unique = uploadVideoInfoDao.queryBuilder().where(UploadVideoInfoDao.Properties.Id.eq(uploadVideoInfo.getId())).unique();
        if(null==unique){
            uploadVideoInfoDao.insertInTx(uploadVideoInfo);
            return true;
        }else{
           return false;
        }
    }


    /**
     * 获取所有上传列表
     * @return
     */

    public synchronized List<UploadVideoInfo> getUploadVideoList(){
        return daoSession.getUploadVideoInfoDao().queryBuilder().orderAsc(UploadVideoInfoDao.Properties.Id).list();
    }

    /**
     * 根据对象删除一上传记录
     *
     * @param uploadVideoInfo
     */
    public void deleteUploadVideoInfo(UploadVideoInfo uploadVideoInfo) {
        try {
            daoSession.getUploadVideoInfoDao().delete(uploadVideoInfo);
        }catch (Exception e){

        }
    }


    /**
     * 更新一条消息
     * @param uploadVideoInfo
     */
    public synchronized void updateUploadVideoInfo(UploadVideoInfo uploadVideoInfo) {
        Log.d(TAG,"更新="+uploadVideoInfo.getUploadProgress());
        daoSession.getUploadVideoInfoDao().update(uploadVideoInfo);
    }

    /**
     * 删除所有消息记录
     */
    public void deteleAllUploadList() {
        daoSession.getUploadVideoInfoDao().deleteAll();
    }

    /**
     * 分页加载数据
     * @param page 页数
     * @param count 一页中的条数 加载第几页中的多少条数据
     */
    public List<UploadVideoInfo> queryUploadListOfPage(int page, int count) {
        return daoSession.getUploadVideoInfoDao().queryBuilder().offset((page-1)*count).limit(count).list();
    }

}
