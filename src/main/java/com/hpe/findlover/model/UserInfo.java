package com.hpe.findlover.model;

import com.github.pagehelper.PageInfo;



public class UserInfo {
    private String message;
    private PageInfo pageInfo;

    public UserInfo(String message, PageInfo pageInfo) {
        this.message = message;
        this.pageInfo = pageInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
