
package com.video.newqu.base;


public interface BaseContract {

    interface BasePresenter<T> {

        void attachView(T view);

        void detachView();
    }

    interface BaseView {

        void showErrorView();

        void complete();
    }
}
