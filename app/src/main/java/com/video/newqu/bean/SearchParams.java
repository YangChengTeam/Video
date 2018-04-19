package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017/6/14 17:53
 * 搜索参数
 */
public class SearchParams {

    private String searchKey;
    private int searchKeyType;
    private int mPage=0;
    private int mPageSize=0;
    private int searchCurrenFragmentIndex;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public int getSearchKeyType() {
        return searchKeyType;
    }

    public void setSearchKeyType(int searchKeyType) {
        this.searchKeyType = searchKeyType;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public int getSearchCurrenFragmentIndex() {
        return searchCurrenFragmentIndex;
    }

    public void setSearchCurrenFragmentIndex(int searchCurrenFragmentIndex) {
        this.searchCurrenFragmentIndex = searchCurrenFragmentIndex;
    }
}
