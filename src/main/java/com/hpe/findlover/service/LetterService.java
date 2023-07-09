package com.hpe.findlover.service;

import com.hpe.findlover.model.Letter;
import com.hpe.findlover.model.UserBasic;

import java.util.List;
import java.util.Map;


public interface LetterService extends BaseService<Letter> {

    /**
     * 获取联系人列表
     * @param userid
     * @return
     */
    Map<String,Object> selectOther(int userid);

    /**
     * 获取私信信息
     * @param uid
     * @param otherId
     * @return
     */
    List<Letter> selectLetter(int uid,int otherId);

    /**
     * 读取私信
     * @param letter
     * @return
     */
    Boolean readLetter(Letter letter);

    Integer selectUnreadCount(int userid);

}
