package com.video.newqu.manager;

import android.content.Context;
import android.util.Log;
import com.video.newqu.bean.StickerDataBean;
import com.video.newqu.bean.StickerDataBeanDao;
import com.video.newqu.dao.DBBaseDao;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2018/7/4 17:31
 * 用户使用贴纸的记录
 */

public class DBStickerMakeManager extends DBBaseDao<StickerDataBean> {

    public DBStickerMakeManager(Context context) {
        super(context);
    }

    /**
     * 通过videoID获取UploadVideoInfo对象
     * @return
     */
    private List<StickerDataBean> getVideoInfoByName(String id){
        QueryBuilder queryBuilder =  daoSession.getStickerDataBeanDao().queryBuilder();
        queryBuilder.where(StickerDataBeanDao.Properties.Id.eq(id));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    /**
     * 根据对象插入一条消息
     * @param dataBean
     */
    public boolean insertNewStickerInfo(StickerDataBean dataBean) {
        try {
            StickerDataBeanDao stickerDataBeanDao = daoSession.getStickerDataBeanDao();
            StickerDataBean unique = stickerDataBeanDao.queryBuilder().where(StickerDataBeanDao.Properties.Id.eq(dataBean.getId())).unique();
            if(null==unique){
                stickerDataBeanDao.insertInTx(dataBean);
                return true;
            }else{
                updateStickerInfo(dataBean);
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }


    /**
     * 获取所有记录
     * @return
     */

    public synchronized List<StickerDataBean> getStickerList(){
        return daoSession.getStickerDataBeanDao().queryBuilder().orderAsc(StickerDataBeanDao.Properties.Id).list();
    }

    /**
     * 根据对象删除一上传记录
     *
     * @param dataBean
     */
    public void deleteStickerInfo(StickerDataBean dataBean) {
        try {
            daoSession.getStickerDataBeanDao().delete(dataBean);
        }catch (Exception e){

        }
    }


    /**
     * 更新一条消息
     * @param dataBean
     */
    public synchronized void updateStickerInfo(StickerDataBean dataBean) {
        Log.d(TAG,"更新="+dataBean.getId());
        try {
            daoSession.getStickerDataBeanDao().update(dataBean);
        }catch (Exception e){

        }
    }

    /**
     * 删除所有消息记录
     */
    public void deteleAllStickerInfo() {
        daoSession.getStickerDataBeanDao().deleteAll();
    }

    /**
     * 分页加载数据
     * @param page 页数
     * @param count 一页中的条数 加载第几页中的多少条数据
     */
    public List<StickerDataBean> queryStickerInfoListOfPage(int page, int count) {
        return daoSession.getStickerDataBeanDao().queryBuilder().offset((page-1)*count).limit(count).list();
    }
}
