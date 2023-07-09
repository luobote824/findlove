package com.hpe.findlover.service;

import com.hpe.findlover.model.*;
import com.hpe.findlover.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

public interface UserService extends BaseService<UserBasic> {
	UserBasic selectByEmail(String email);

	List<UserBasic> selectStarUser(UserPick userPick);

	List<UserBasic> selectUserBySearch(Search search);

	List<UserBasic> selectUserByUserPick(UserPick userPick);

	List<UserBasic> selectUserBySexualAndWorkProvince(Integer id,String sexual,String workProvince);

	/**
	 * 修改用户基本信息
	 * @param userBasic 用户基本信息
	 * @return
	 */
	boolean updateUserBasic(UserBasic userBasic);

	boolean updatePhoto(UserPhoto photo, UserBasic user);

	//--------------------------后台功能------------------------
	/**
	 * 根据关键词查出用户基本信息
	 * @param identity
	 * @param column
	 * @param keyword
	 * @return
	 */
	List<UserBasic> selectAllByIdentity(String identity,String column,String keyword);

	/**
	 * 接收一个UserBasic集合，为集合中每一个UserBasic对象设置年龄等属性
	 *
	 * @param basics
	 */
	void userAttrHandler(List<UserBasic> basics);

	/**
	 * 接收一个UserBasic，为UserBasic对象设置年龄等属性
	 *
	 * @param basic
	 */
	void userAttrHandler(UserBasic basic);
}
