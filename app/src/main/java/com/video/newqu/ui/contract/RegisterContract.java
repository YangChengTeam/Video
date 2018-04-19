
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import java.io.File;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface RegisterContract {

    interface View extends BaseContract.BaseView {
        void registerResultError(String data);
        void registerResultFinlish(String data);
        void registerError();
        void imageUploadError();
        void imageUploadFinlish(String data);
        void needIploadImageLogo();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void register(String imeil, String account, String passsword, String code, String countryCode, String nickName, String sex, File filePath);
        void register(String imeil, final String account, String passsword, String code);
    }
}
