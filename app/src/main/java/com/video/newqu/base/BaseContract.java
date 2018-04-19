
package com.video.newqu.base;


public interface BaseContract {

    interface BasePresenter<T> {

        void attachView(T view);

        void detachView();
    }

    public interface BaseView {

        void showErrorView();

        void complete();
    }
}
