package com.hpe.findlover.mapper;

import com.hpe.findlover.model.Notice;
import com.hpe.findlover.model.UserBasic;
import com.hpe.util.BaseTkMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper extends BaseTkMapper<Notice> {

    /**
     * 选择所有通知
     * @param identity
     * @param column
     * @param keyword
     * @return
     */
    List<Notice> selectAllByIdentity(@Param("identity") String identity,
                                     @Param("column")String column,
                                     @Param("keyword")String keyword);
    /**
     * 选择未读消息
     * @param userBasic
     * @return
     */
    List<Notice> selectUnReadNotice(UserBasic userBasic);

    /**
     * 选择已读消息
     * @param userBasic
     * @return
     */
    List<Notice> selectReadNotice(UserBasic userBasic);
}