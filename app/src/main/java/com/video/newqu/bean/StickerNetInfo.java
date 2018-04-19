package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/9/11.
 * 贴纸Info
 */

public class StickerNetInfo implements Serializable{

    /**
     * code : 1
     * data : [{"id":"7270","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-11/59b64640e10e7.jpg","desp":null,"type_id":"157","sort":"500","add_time":"1505117760","add_date":"20170911","down_num":null},{"id":"7271","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1d6b93a.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177117","add_date":"20170912","down_num":null},{"id":"7272","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1d94270.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177117","add_date":"20170912","down_num":null},{"id":"7273","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1db710e.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177117","add_date":"20170912","down_num":null},{"id":"7274","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1ddf207.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177117","add_date":"20170912","down_num":null},{"id":"7275","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1e079ff.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177118","add_date":"20170912","down_num":null},{"id":"7276","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1e27c4d.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177118","add_date":"20170912","down_num":null},{"id":"7277","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1e3e357.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177118","add_date":"20170912","down_num":null},{"id":"7278","title":null,"src":"http://sc.wk2.com/upload/157/2017-09-12/59b72e1e5a7d4.png","desp":null,"type_id":"157","sort":"500","add_time":"1505177118","add_date":"20170912","down_num":null}]
     */

    private int code;
    /**
     * id : 7270
     * title : null
     * src : http://sc.wk2.com/upload/157/2017-09-11/59b64640e10e7.jpg
     * desp : null
     * type_id : 157
     * sort : 500
     * add_time : 1505117760
     * add_date : 20170911
     * down_num : null
     */

    private List<StickerDataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<StickerDataBean> getData() {
        return data;
    }
    public void setData(List<StickerDataBean> data) {
        this.data = data;
    }
}
