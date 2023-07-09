package com.hpe.findlover.mapper;

import com.hpe.findlover.model.Letter;
import com.hpe.findlover.model.LetterUser;
import com.hpe.util.BaseTkMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface LetterMapper extends BaseTkMapper<Letter> {
    /**
     * 选择发私信的用户
     * @param userid
     * @return
     */
    List<LetterUser> selectOther(@Param("userid") int userid);

    /**
     * 根据id选择私信信息
     * @param uid
     * @param otherId
     * @return
     */
    List<Letter> selectLetter(@Param("uid")int uid,@Param("otherId") int otherId);
    Integer selectAmount(@Param("sendid")int sendid,@Param("userid") int userid);

    /**
     * 选择未读信息数量
     * @param userid
     * @return
     */
    Integer selectUnreadCount(int userid);
}