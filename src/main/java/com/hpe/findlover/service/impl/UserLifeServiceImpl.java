package com.hpe.findlover.service.impl;

import com.hpe.findlover.mapper.UserLifeMapper;
import com.hpe.findlover.model.UserLife;
import com.hpe.findlover.service.UserLifeService;
import com.hpe.util.BaseTkMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLifeServiceImpl extends BaseServiceImpl<UserLife> implements UserLifeService {
	private Logger logger = LogManager.getLogger(UserLifeServiceImpl.class);

	private final UserLifeMapper userLifeMapper;

	@Autowired
	public UserLifeServiceImpl(UserLifeMapper userLifeMapper) {
		this.userLifeMapper = userLifeMapper;
	}

	@Override
	public BaseTkMapper<UserLife> getMapper() {
		return userLifeMapper;
	}

	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public boolean insertUserLife(UserLife userLife) {
		boolean result = false;
		if (this.selectByPrimaryKey(userLife.getId()) != null) {//用户修改工作生活信息
			result = this.updateByPrimaryKey(userLife);
		} else {//用户第一次填写工作生活信息
			result = this.insert(userLife);
		}

		return result;
	}
}
