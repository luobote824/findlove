package com.hpe.findlover.contoller.back;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpe.findlover.model.*;
import com.hpe.findlover.service.BaseService;
import com.hpe.findlover.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  用户管理
 */
@Controller
@RequiresRoles("user")
@RequestMapping("admin/user")
public class UserControllerBack {
	private Logger logger = LogManager.getLogger(UserControllerBack.class);
	private final UserService userBasicService;
	private final UserDetailService userDetailService;
	private final UserLifeService userLifeService;
	private final UserStatusService userStatusService;
	private final UserPickService userPickService;
	private final FollowService followService;
	private final VisitTraceService visitTraceService;
	private final UserPhotoService userPhotoService;

	@Autowired
	public UserControllerBack(UserService userBasicService, UserDetailService userDetailService, UserLifeService userLifeService, UserStatusService userStatusService, UserPickService userPickService,FollowService followService, VisitTraceService visitTraceService, UserPhotoService userPhotoService) {
		this.userBasicService = userBasicService;
		this.userDetailService = userDetailService;
		this.userLifeService = userLifeService;
		this.userStatusService = userStatusService;
		this.userPickService = userPickService;
		this.followService = followService;
		this.visitTraceService = visitTraceService;
		this.userPhotoService = userPhotoService;
	}

	@GetMapping("basic")
	@ResponseBody
	public PageInfo<UserBasic> userBasicList(Page<UserBasic> page, @RequestParam String identity, @RequestParam String column, @RequestParam String keyword) {
		logger.info("接收参数：identity=" + identity + ",pageNum=" + page.getPageNum() + ",pageSize=" + page.getPageSize() + ",column=" + column + ",keyword=" + keyword);
		PageHelper.startPage(page.getPageNum(), page.getPageSize());
		List<UserBasic> basics = userBasicService.selectAllByIdentity(identity, column, "%" + keyword + "%");
		userBasicService.userAttrHandler(basics);
		PageInfo<UserBasic> pageInfo = new PageInfo<>(basics);
		logger.info(JSONObject.toJSON(pageInfo));
		return pageInfo;
	}

	@GetMapping("{type}/{id}")
	@ResponseBody
	@Cacheable(value = "user-cache", key = "#type+'-'+#id")
	public Object userBasic(@PathVariable int id, @PathVariable String type) throws NoSuchFieldException, IllegalAccessException {
		logger.debug("获取ID为" + id + "的User" + StringUtils.capitalize(type) + "数据...");
		Field declaredField = this.getClass().getDeclaredField("user" + StringUtils.capitalize(type) + "Service");
		declaredField.setAccessible(true);
		return ((BaseService) declaredField.get(this)).selectByPrimaryKey(id);
	}

	@GetMapping("/follower")
	@ResponseBody
	public PageInfo follower(Page<UserBasic> page,@RequestParam("id") int id){
		logger.info("关注我的人接收参数：pageNum=" + page.getPageNum() + ",pageSize=" + page.getPageSize());
		PageHelper.startPage(page.getPageNum(),page.getPageSize());
		List<Follow> followers = followService.selectFollower(id);
		followers.forEach(logger::info);
		PageInfo pageInfo = new PageInfo(followers);
		return  pageInfo;
	}

	@GetMapping("/follow")
	@ResponseBody
	public PageInfo follow(Page<UserBasic> page,@RequestParam("id") int id){
		logger.info("我关注的人接收参数：pageNum=" + page.getPageNum() + ",pageSize=" + page.getPageSize());
		PageHelper.startPage(page.getPageNum(),page.getPageSize());
		List<Follow> followers = followService.selectFollow(id);
		followers.forEach(logger::info);
		PageInfo pageInfo = new PageInfo(followers);
		return  pageInfo;
	}

	@GetMapping("/trace")
	@ResponseBody
	public PageInfo trace(Page<UserBasic> page,@RequestParam("id") int id){
		logger.info("我看过的人接收参数：pageNum=" + page.getPageNum() + ",pageSize=" + page.getPageSize());
		PageHelper.startPage(page.getPageNum(),page.getPageSize());
		List<VisitTrace> followers = visitTraceService.selectVisitTrace(id);
		followers.forEach(logger::info);
		PageInfo pageInfo = new PageInfo(followers);
		return  pageInfo;
	}

	@GetMapping("/tracer")
	@ResponseBody
	public PageInfo tracer(Page<UserBasic> page,@RequestParam("id") int id){
		logger.info("看过我的人接收参数：pageNum=" + page.getPageNum() + ",pageSize=" + page.getPageSize());
		PageHelper.startPage(page.getPageNum(),page.getPageSize());
		List<VisitTrace> followers = visitTraceService.selectVisitTracer(id);
		followers.forEach(logger::info);
		PageInfo pageInfo = new PageInfo(followers);
		return  pageInfo;
	}

	@GetMapping("details/{id}")
	public String userDetail(@ModelAttribute @PathVariable int id,Model model) {
		UserPhoto userPhoto = new UserPhoto();
		userPhoto.setUserId(id);
		List<UserPhoto> userPhotos = userPhotoService.select(userPhoto);
		List<String> photos = userPhotos.stream().map(UserPhoto::getPhoto).collect(Collectors.toList());
		photos.add(0,userBasicService.selectByPrimaryKey(id).getPhoto());
		model.addAttribute("photos", photos);
		logger.debug("photos:"+photos);
		return "back/user/user_detail";
	}

	@GetMapping("detail")
	public String userDetailPre() {
		return "back/user/user_detail_pre";
	}

	@GetMapping("list")
	public String userList() {
		return "back/user/user_list";
	}


	@PutMapping("basic/{id}")
	@ResponseBody
	@CachePut(value = "user-cache", key = "'basic-'+#id")
	public boolean updateUser(@PathVariable int id, UserBasic userBasic) {
		userBasic.setId(id);
		return userBasicService.updateByPrimaryKeySelective(userBasic);
	}


	//验证用户id
	@RequestMapping("check_id")
	@ResponseBody
	public String checkid(@RequestParam("userId")int userId){
		logger.info("验证用户id");
		UserBasic user= userBasicService.selectByPrimaryKey(userId);
		 if(user!=null){
			return "{\"ok\":\"\"}";
		}else {
			return "{\"error\":\"该id不存在！\"}";
		}
	}
}
