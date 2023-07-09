package com.hpe.findlover.mapper;

import com.hpe.findlover.model.SuccessStory;
import com.hpe.util.BaseTkMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SuccessStoryMapper extends BaseTkMapper<SuccessStory> {
    /**
     * 通过关键词和状态查询成功故事
     * @param column
     * @param keyword
     * @param status
     * @return
     */
    List<SuccessStory> selectByKeywordAndStatus(String column, String keyword, int status);

    /**
     * 通过关键词查询成功故事
     * @param column
     * @param keyword
     * @return
     */
    List<SuccessStory> selectByKeyword(String column, String keyword);

    /**
     * 查找出成功故事用户
     * @return
     */
    List<SuccessStory> selectNotSingle();

    /**
     * 通过状态查找成功用户
     * @return
     */
    List<SuccessStory> selectAllByStatus();
    /**
     * 检查用户是否合规
     * @param right
     * @param left
     * @return
     */
    SuccessStory checkUser(@Param("right") int right,@Param("left") int left);
}