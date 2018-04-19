package com.video.newqu.manager;

import android.content.Context;

import com.video.newqu.bean.UserPlayerVideoHistoryList;
import com.video.newqu.bean.UserPlayerVideoHistoryListDao;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.dao.DBBaseDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/10 8:48
 * 用户播放视频的历史记录
 */

public class DBUserPlayerVideoHistoryManager extends DBBaseDao<UserPlayerVideoHistoryList> {


    public DBUserPlayerVideoHistoryManager(Context context) {
        super(context);
    }

    /**
     * 通过Id查询对象
     * @return
     */
    public UserPlayerVideoHistoryList loadById(long id){

        return daoSession.getUserPlayerVideoHistoryListDao().load(id);
    }

    /**
     * 获取某个对象的主键Id
     * @param data
     * @return
     */
    private long getId(UserPlayerVideoHistoryList data){
        return daoSession.getUserPlayerVideoHistoryListDao().getKey(data);
    }

    /**
     * 通过Id获取UserPlayerVideoHistoryList对象
     * @return
     */
    private List<UserPlayerVideoHistoryList> getVideoInfoByName(String id){
        QueryBuilder queryBuilder =  daoSession.getUserPlayerVideoHistoryListDao().queryBuilder();
        queryBuilder.where(UserPlayerVideoHistoryListDao.Properties.VideoId.eq(id));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    /**
     * 通过Key名字获取所有列表对象的唯一标识
     * @return
     */
    private List<Long> getIdByName(String key){
        List<UserPlayerVideoHistoryList> students = getVideoInfoByName(key);
        List<Long> ids = new ArrayList<Long>();
        int size = students.size();
        if (size > 0){
            for (int i = 0;i < size;i++){
                ids.add(students.get(i).getID());
            }
            return ids;
        }else{
            return null;
        }
    }



    /**
     * 根据Id进行数据库的删除操作
     * @param id
     */
    public void deleteById(Long id){

        daoSession.getUserPlayerVideoHistoryListDao().deleteByKey(id);
    }

    /**
     * 根据Id同步删除数据库操作
     * @param ids
     */
    private void deleteByIds(List<Long> ids){

        daoSession.getUserPlayerVideoHistoryListDao().deleteByKeyInTx(ids);
    }

    /**
     * 根据对象插入一条记录
     * @param data
     */
    public boolean insertNewPlayerHistoryOfObject(UserPlayerVideoHistoryList data) {
        try {
            if(null!=daoSession){
                UserPlayerVideoHistoryListDao userPlayerVideoHistoryListDao = daoSession.getUserPlayerVideoHistoryListDao();
                UserPlayerVideoHistoryList unique = userPlayerVideoHistoryListDao.queryBuilder().where(UserPlayerVideoHistoryListDao.Properties.VideoId.eq(data.getVideoId())).unique();
                if(null==unique){
                    List<UserPlayerVideoHistoryList> allHistoryPlayerVideoList = getAllHistoryPlayerVideoList();
                    if(null!=allHistoryPlayerVideoList&&allHistoryPlayerVideoList.size()>0&&allHistoryPlayerVideoList.size()>= ConfigSet.SAVE_USER_LOOK_VIDEO_HISTORY_COUNT){
                        //如果大于指定存储数量，就删除最前面那一个元素
                        UserPlayerVideoHistoryList userPlayerVideoHistoryList = allHistoryPlayerVideoList.get(0);
                        if(null!=userPlayerVideoHistoryList){
                            deletePlayerHistoryOfObject(userPlayerVideoHistoryList);
                            userPlayerVideoHistoryListDao.insertInTx(data);
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        userPlayerVideoHistoryListDao.insertInTx(data);
                        return true;
                    }
                }else{
                    updatePlayerHistoryInfo(data);
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }


    /**
     * 获取所有记录
     * @return
     */

    public synchronized List<UserPlayerVideoHistoryList> getAllHistoryPlayerVideoList(){
        return daoSession.getUserPlayerVideoHistoryListDao().queryBuilder().orderAsc(UserPlayerVideoHistoryListDao.Properties.Id).list();
    }

    /**
     * 根据对象删除一条记录
     *
     * @param data
     */
    public void deletePlayerHistoryOfObject(UserPlayerVideoHistoryList data) {
        try {
            daoSession.getUserPlayerVideoHistoryListDao().delete(data);
        }catch (Exception e){

        }
    }


    /**
     * 更新一条记录
     * @param data
     */
    public synchronized void updatePlayerHistoryInfo(UserPlayerVideoHistoryList data) {
        try {
            daoSession.getUserPlayerVideoHistoryListDao().update(data);
        }catch (Exception e){

        }
    }

    /**
     * 删除所有历史记录
     */
    public void deteleAllPlayerHistoryList() {
        daoSession.getUserPlayerVideoHistoryListDao().deleteAll();
    }

    /**
     * 分页加载数据
     * @param page 页数
     * @param count 一页中的条数 加载第几页中的多少条数据
     */
    public List<UserPlayerVideoHistoryList> queryPlayerHistoryListOfPage(int page, int count) {
        return daoSession.getUserPlayerVideoHistoryListDao().queryBuilder().offset((page-1)*count).limit(count).list();
    }

}
