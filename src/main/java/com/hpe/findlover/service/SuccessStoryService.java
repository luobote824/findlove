package com.hpe.findlover.service;

import com.hpe.findlover.model.SuccessStory;
import com.hpe.findlover.model.UserBasic;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 成功故事
 */
public interface SuccessStoryService extends BaseService<SuccessStory> {
    /**
     * 根据状态查找成功故事
     *
     * @return
     */
    List<SuccessStory> selectByKeywordAndStatus(String column, String keyword, int status);

    /**
     * 查找出成功故事用户，展示在光荣脱榜单
     *
     * @return
     */
    Map<UserBasic, Integer> selectNotSingle();

    /**
     * 插入成功故事并发通知
     */
    boolean insertStory(SuccessStory successStory,int userId,HttpServletRequest request);
    List<SuccessStory> selectAllByStatus();

    /**
     * 判断用户是否合规
     * @param userId
     * @return
     */
    boolean checkUser(int userId,int left);
}
