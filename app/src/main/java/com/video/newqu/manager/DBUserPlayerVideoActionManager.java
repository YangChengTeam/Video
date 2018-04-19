package com.video.newqu.manager;

import android.content.Context;
import android.util.Log;
import com.video.newqu.bean.UserVideoPlayerList;
import com.video.newqu.bean.UserVideoPlayerListDao;
import com.video.newqu.dao.DBBaseDao;
import com.video.newqu.util.Logger;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/10 8:48
 * 用户播放视频行为记录表
 */

public class DBUserPlayerVideoActionManager extends DBBaseDao<UserVideoPlayerList> {

    public DBUserPlayerVideoActionManager(Context context) {
        super(context);
    }

    /**
     * 通过Id查询对象
     * @return
     */
    public UserVideoPlayerList loadById(long id){

        return daoSession.getUserVideoPlayerListDao().load(id);
    }

    /**
     * 获取某个对象的主键Id
     * @param data
     * @return
     */
    private long getId(UserVideoPlayerList data){
        return daoSession.getUserVideoPlayerListDao().getKey(data);
    }

    /**
     * 通过Id获取UserVideoPlayerList对象
     * @return
     */
    private List<UserVideoPlayerList> getVideoInfoByName(String id){
        QueryBuilder queryBuilder =  daoSession.getUserVideoPlayerListDao().queryBuilder();
        queryBuilder.where(UserVideoPlayerListDao.Properties.VideoID.eq(id));
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
        List<UserVideoPlayerList> students = getVideoInfoByName(key);
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

        daoSession.getUserVideoPlayerListDao().deleteByKey(id);
    }

    /**
     * 根据Id同步删除数据库操作
     * @param ids
     */
    private void deleteByIds(List<Long> ids){

        daoSession.getUserVideoPlayerListDao().deleteByKeyInTx(ids);
    }

    /**
     * 根据对象插入一条记录
     * @param data
     */
    public boolean insertNewPlayerHistoryOfObject(UserVideoPlayerList data) {
        UserVideoPlayerListDao userVideoPlayerListDao = daoSession.getUserVideoPlayerListDao();
        UserVideoPlayerList unique = userVideoPlayerListDao.queryBuilder().where(UserVideoPlayerListDao.Properties.VideoID.eq(data.getVideoID())).unique();
        if(null==unique){
            userVideoPlayerListDao.insertInTx(data);
            return true;
        }else{
            return false;
        }
    }


    /**
     * 获取所有记录
     * @return
     */

    public synchronized List<UserVideoPlayerList> getAllUserPlayerActionList(){
        return daoSession.getUserVideoPlayerListDao().queryBuilder().orderAsc(UserVideoPlayerListDao.Properties.ID).list();
    }

    /**
     * 根据对象删除一条记录
     *
     * @param data
     */
    public void deletePlayerHistoryOfObject(UserVideoPlayerList data) {

        try {
            daoSession.getUserVideoPlayerListDao().delete(data);
        }catch (Exception e){

        }
    }


    /**
     * 更新一条记录
     * @param data
     */
    public synchronized void updatePlayerHistoryInfo(UserVideoPlayerList data) {
        daoSession.getUserVideoPlayerListDao().update(data);
    }

    /**
     * 删除所有历史记录
     */
    public void deteleAllPlayerHistoryList() {
        daoSession.getUserVideoPlayerListDao().deleteAll();
    }

    /**
     * 分页加载数据
     * @param page 页数
     * @param count 一页中的条数 加载第几页中的多少条数据
     */
    public List<UserVideoPlayerList> queryPlayerHistoryListOfPage(int page, int count) {
        return daoSession.getUserVideoPlayerListDao().queryBuilder().offset((page-1)*count).limit(count).list();
    }

}
