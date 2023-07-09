package com.hpe.findlover.service.impl;

import com.hpe.findlover.mapper.*;
import com.hpe.findlover.model.*;
import com.hpe.findlover.service.UserService;
import com.hpe.findlover.util.LoverUtil;
import com.hpe.util.BaseTkMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserBasic> implements UserService {
	private Logger logger = LogManager.getLogger(UserServiceImpl.class);

	private final UserBasicMapper userBasicMapper;
	private final UserPhotoMapper userPhotoMapper;
	private final UserDetailMapper userDetailMapper;

	@Autowired
	public UserServiceImpl(UserBasicMapper userBasicMapper,UserPhotoMapper userPhotoMapper, UserDetailMapper userDetailMapper) {
		this.userBasicMapper = userBasicMapper;
		this.userPhotoMapper = userPhotoMapper;
		this.userDetailMapper = userDetailMapper;
	}

	@Override
	public BaseTkMapper<UserBasic> getMapper() {
		return userBasicMapper;
	}

	@Override
	public UserBasic selectByEmail(String email) {
		return userBasicMapper.selectByEmail(email);
	}

	@Override
	public List<UserBasic> selectStarUser(UserPick userPick) {
		userBasicMapper.selectStarUser(userPick).forEach(logger::info);
		return userBasicMapper.selectStarUser(userPick);
	}

	@Override
	public List<UserBasic> selectUserBySearch(Search search) {
		return userBasicMapper.selectUserBySearch(search);
	}

	@Override
	public List<UserBasic> selectUserByUserPick(UserPick userPick) {
		return userBasicMapper.selectUserByUserPick(userPick);
	}

	@Override
	public List<UserBasic> selectUserBySexualAndWorkProvince(Integer id, String sexual, String workProvince) {
		return userBasicMapper.selectUserBySexualAndWorkProvince(id, sexual, workProvince);
	}


	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public boolean updateUserBasic(UserBasic userBasic) {
		return this.updateByPrimaryKeySelective(userBasic);
	}

	//-----------------------------后台------------------------------
	@Override
	public List<UserBasic> selectAllByIdentity(String identity, String column, String keyword) {
		return userBasicMapper.selectAllByIdentity(identity, column, keyword);
	}

	@Override
	public void userAttrHandler(List<UserBasic> basics) {
		basics.forEach(this::userAttrHandler);
	}

	@Override
	public void userAttrHandler(UserBasic user) {
		user.setAge(LoverUtil.getAge(user.getBirthday()));
		UserDetail detail = userDetailMapper.selectByPrimaryKey(user.getId());
		if (detail != null) {
			user.setAuthenticated(detail.getCardnumber() != null && detail.getRealname() != null);
		}
	}

	@Transactional(rollbackFor = RuntimeException.class)
	@Override
	public boolean updatePhoto(UserPhoto photo, UserBasic user) {
		String tmp = user.getPhoto();
		user.setPhoto(photo.getPhoto());
		photo.setPhoto(tmp);
		if (!this.updateByPrimaryKey(user)) {
			return false;
		}
		if (userPhotoMapper.updateByPrimaryKey(photo) < 0) {
			return false;
		}
		return true;
	}
}
