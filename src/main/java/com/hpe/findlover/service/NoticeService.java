package com.hpe.findlover.service;

import com.hpe.findlover.model.Notice;
import com.hpe.findlover.model.UserBasic;

import java.util.List;


public interface NoticeService extends BaseService<Notice> {

    List<Notice> selectAllByIdentity(String identity, String column, String keyword);
    List<Notice> selectUnReadNotice(UserBasic userBasic);
    List<Notice> selectReadNotice(UserBasic userBasic);
}
