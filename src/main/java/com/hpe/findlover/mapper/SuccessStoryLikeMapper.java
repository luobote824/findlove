package com.hpe.findlover.mapper;

import com.hpe.findlover.model.SuccessStoryLike;
import com.hpe.util.BaseTkMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SuccessStoryLikeMapper extends BaseTkMapper<SuccessStoryLike> {
    /**
     * 成功故事点赞
     * @param storyId
     * @param userId
     * @return
     */
    SuccessStoryLike selectByStoryIdAndUserId(int storyId,int userId);
    /**
     * 统计点赞数量
     * @param id
     * @return
     */
    Integer selectCountByStoryId(int id);
}