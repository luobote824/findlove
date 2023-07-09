package com.hpe.findlover.service;

import com.hpe.findlover.model.UserLife;
import com.hpe.findlover.service.BaseService;

public interface UserLifeService extends BaseService<UserLife> {
    /**
     * 添加用户工作生活
     * @param userLife
     * @return
     */
    boolean insertUserLife(UserLife userLife);
}
