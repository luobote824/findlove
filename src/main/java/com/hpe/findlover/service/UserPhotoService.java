package com.hpe.findlover.service;

import com.hpe.findlover.model.UserPhoto;
import com.hpe.findlover.service.BaseService;



public interface UserPhotoService extends BaseService<UserPhoto> {
    /**
     * 删除用户单张照片
     * @param id
     * @return
     */
    boolean deletePhotoByUserPhotoId(Integer id);
}
