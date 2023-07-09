package com.hpe.findlover.mapper;

import com.hpe.findlover.model.SuccessStoryReply;
import com.hpe.util.BaseTkMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SuccessStoryReplyMapper extends BaseTkMapper<SuccessStoryReply> {

    /**
     * 查询成功故事评论
     * @param storyId
     * @return
     */
    List<SuccessStoryReply> selectByStoryId(int storyId);
}