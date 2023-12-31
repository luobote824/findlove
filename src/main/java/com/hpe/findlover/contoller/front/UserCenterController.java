package com.hpe.findlover.contoller.front;

import com.alibaba.fastjson.JSONObject;
import com.hpe.findlover.anno.MyParam;
import com.hpe.findlover.model.*;
import com.hpe.findlover.service.*;
import com.hpe.findlover.util.Constant;
import com.hpe.findlover.util.LoverUtil;
import com.hpe.findlover.util.PhotoFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;


/*
*个人资料管理
 */
@Controller
@RequestMapping("/usercenter")
@CacheConfig(cacheNames = "user-cache")
public class UserCenterController {

	private Logger logger = LogManager.getLogger(UserCenterController.class);

	private final UserService userService;
	private final UserDetailService userDetailService;
	private final UserLifeService userLifeService;
	private final UserStatusService userStatusService;
	private final UserPickService userPickService;
	private final UploadService uploadService;
	private final UserPhotoService userPhotoService;


	@Autowired
	public UserCenterController(UserService userService, UserDetailService userDetailService, UserLifeService userLifeService, UserStatusService userStatusService, UserPickService userPickService,UploadService uploadService, UserPhotoService userPhotoService) {
		this.userService = userService;
		this.userDetailService = userDetailService;
		this.userLifeService = userLifeService;
		this.userStatusService = userStatusService;
		this.userPickService = userPickService;
		this.uploadService = uploadService;
		this.userPhotoService = userPhotoService;
	}

	/**
	 * 跳转到用户中心界面
	 */
	@GetMapping
	public String userCenter(Model model, HttpSession session,@RequestParam(required = false) String type) {
		UserBasic user = (UserBasic) session.getAttribute("user");
		userService.userAttrHandler(user);
		UserPhoto userPhoto = new UserPhoto();
		userPhoto.setUserId(user.getId());
		//获取用户所有照片
		List<UserPhoto> photos = userPhotoService.select(userPhoto);
		//获取用户头像
		UserPhoto userBasicPhoto = new UserPhoto();
		userBasicPhoto.setId(0);
		userBasicPhoto.setPhoto(user.getPhoto());
		userBasicPhoto.setUserId(user.getId());
		photos.add(0, userBasicPhoto);
		session.setAttribute("user", user);
		logger.info("userAuth:" + user.isAuthenticated());
		model.addAttribute("photos", photos);
		model.addAttribute("type", type);
		return "front/user_center";
	}

	/**
	 * 用户图片上传
	 *
	 * @param request
	 * @return 严格返回JSON格式
	 */
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody
	public Object upload(HttpServletRequest request, HttpSession session) throws Exception {
		//先检查图片数量是否已经上传至最大，即8张
		UserBasic userBasic = (UserBasic) session.getAttribute("user");
		UserPhoto checkPhoto = new UserPhoto();
		checkPhoto.setUserId(userBasic.getId());
		List<UserPhoto> validatPhotos = userPhotoService.select(checkPhoto);
		if (validatPhotos.size() >= 8) {
			return "{\"result\":\"false\"}";
		}
		Map<String, Object> resultMap = null;
		//如果符合以上条件就给予上传
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest mrequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> photos = mrequest.getFiles("photos");
			logger.debug("上传总数====>" + photos.size());
			logger.debug("上传总数====>" + (validatPhotos.size() + photos.size()));
			if ((validatPhotos.size() + photos.size()) > 8) {
				return "{\"result\":\"fill\"}";
			}
			resultMap = new HashMap<>();
			List<UserPhoto> paths = new ArrayList<>();
			Iterator<MultipartFile> iterator = photos.iterator();
			while (iterator.hasNext()) {
				MultipartFile photo = iterator.next();
				if (photo != null) {
					logger.debug("文件名称====>" + photo.getName());
					logger.debug("文件类型====>" + photo.getContentType());
					logger.debug("文件大小====>" + photo.getSize());
					String photoPath = uploadService.uploadFile(photo);
					//图片审核
					Map<String,String> checkmap = PhotoFilter.checkPhoto(photoPath);
					for (Map.Entry<String, String> entry : checkmap.entrySet()) {
						if(!entry.getValue().equals("pass")){
							resultMap.put("result", entry.getKey());
							return resultMap;
						}
					}
					UserPhoto userPhoto = new UserPhoto();
					userPhoto.setUserId(userBasic.getId());
					userPhoto.setPhoto(photoPath);
					userPhotoService.insertUseGeneratedKeys(userPhoto);
					paths.add(userPhoto);
					logger.debug("上传完成!");
					logger.debug("==================================");
				}
			}
			//查询出所有的照片
			resultMap.put("photos", paths);

		}
		resultMap.put("result", "true");
		return resultMap;
	}

	/**
	 * 在相册中设置头像
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "photo/head/{id}", method = RequestMethod.PUT)
	@ResponseBody
	@CachePut(key = "'photo-'+#id")
	public Object updateUserPhoto(@PathVariable Integer id, HttpSession session) {
		boolean result = false;
		if (id != null) {
			UserPhoto photo = userPhotoService.selectByPrimaryKey(id);
			UserBasic user = (UserBasic) session.getAttribute("user");
			result = userService.updatePhoto(photo, user);
			session.setAttribute("user", user);
		}
		return result;
	}

	/**
	 * 删除照片
	 *
	 * @param id 要删除照片的id
	 * @return
	 */
	@RequestMapping(value = "photo/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object uploadPhoto(@PathVariable Integer id) {
		boolean result = false;
		if (id != null) {
			logger.debug("删除的图片id为：" + id);
			result = userPhotoService.deletePhotoByUserPhotoId(id);
		}
		return result;
	}

	/**
	 * 用户头像上传
	 *
	 * @param photo
	 * @return 严格返回JSON格式
	 */
	@RequestMapping(value = "photo", method = RequestMethod.POST)
	@ResponseBody
	public Object uploadPhoto(MultipartFile photo, HttpSession session) {
		JSONObject obj = new JSONObject();
		if (photo == null) {
			return "{\"result\":\"false\"}";
		}
		String uploadPhotoPath = null;
		UserBasic userBasic = (UserBasic) session.getAttribute("user");
		UserPhoto userPhoto = null;

		try {
			//获取用户头像
			logger.debug("文件名称====>" + photo.getName());
			logger.debug("文件类型====>" + photo.getContentType());
			logger.debug("文件大小====>" + photo.getSize());
			uploadPhotoPath = uploadService.uploadFile(photo);
			//图片审核
			Map<String,String> checkmap = PhotoFilter.checkPhoto(uploadPhotoPath);
			for (Map.Entry<String, String> entry : checkmap.entrySet()) {
				if(!entry.getValue().equals("pass")){
					obj.put("result", entry.getKey());
					return obj;
				}
			}
			userBasic.setPhoto(uploadPhotoPath);
			logger.debug(uploadPhotoPath);
			//更新sesion中图片路径
			session.setAttribute("user", userBasic);
			userService.updateByPrimaryKeySelective(userBasic);
			logger.debug("上传完成!");
			logger.debug("==================================");
			userPhoto = new UserPhoto();
			userPhoto.setId(0);
			userPhoto.setPhoto(uploadPhotoPath);
			userPhoto.setUserId(userBasic.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		obj.put("result", "true");
		obj.put("photo",userPhoto);
		return obj;
	}

	/**
	 * 查询用户的基本资料
	 *
	 * @return 返回Json格式
	 */
	@RequestMapping("userbasic")
	@ResponseBody
	public Object selectUserBasic(HttpSession session) {
		UserBasic user = (UserBasic) session.getAttribute("user");
		//int useriId = user.getId();
		UserBasic userBasic = null;
		try {
			userBasic = userService.selectByPrimaryKey(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userBasic;
	}


	/**
	 * 查询用户的详细资料
	 *
	 * @return 返回Json格式
	 */
	@RequestMapping("userdetail")
	@ResponseBody
	public Object selectUserDetail(HttpSession session) {
		UserBasic user = (UserBasic) session.getAttribute("user");
		UserDetail userDetail = null;
		try {
			userDetail = userDetailService.selectByPrimaryKey(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userDetail;
	}

	/**
	 * 查询出用户的工作生活
	 *
	 * @return 返回Json格式
	 */
	@RequestMapping("userlife")
	@ResponseBody
	public Object selectUserLife(HttpSession session) {
		UserBasic user = (UserBasic) session.getAttribute("user");
		UserLife userLife = null;
		try {
			userLife = userLifeService.selectByPrimaryKey(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userLife;
	}

	/**
	 * 查询用户的婚姻观
	 *
	 * @return 返回Json格式
	 */
	@RequestMapping("userstatus")
	@ResponseBody
	public Object selectUserStatus(HttpSession session) {
		UserBasic user = (UserBasic) session.getAttribute("user");
		UserStatus userStatus = null;
		try {
			userStatus = userStatusService.selectByPrimaryKey(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userStatus;
	}

	/**
	 * 查询用户的择偶条件
	 *
	 * @return 返回Json格式
	 */
	@RequestMapping("userpick")
	@ResponseBody
	public Object selectUserPick(HttpSession session) {
		UserBasic user = (UserBasic) session.getAttribute("user");
		UserPick userPick = null;
		if (user != null) {
			userPick = userPickService.selectByPrimaryKey(user.getId());
		}
		return userPick;
	}

	/**
	 * 用户基本信息维护
	 *
	 * @return 返回Json格式
	 */
	@PutMapping("basic")
	@ResponseBody
	@CachePut(key = "'basic-'+#userBasic.id")
	public Object updateUserBasic(@MyParam UserBasic userBasic, @MyParam String work_province,@MyParam String work_city, HttpSession session) {
		boolean result = false;
		if (userBasic != null) {
			if (work_city != null) {
				userBasic.setWorkplace(work_province + "-" + work_city);
			} else {
				userBasic.setWorkplace(work_province);
			}
			if (userBasic.getPassword() != null) {
				userBasic.setPassword(new Md5Hash(userBasic.getPassword(),userBasic.getEmail()).toString());
			}
			//new Md5Hash(user.getPassword(), ByteSource.Util.bytes(user.getEmail())).toString()
			logger.debug(userBasic);
			if(userBasic.getSexual()!=null) {
				UserPick userPick = new UserPick();
				userPick.setId(userBasic.getId());
				userPick.setSex(userBasic.getSexual());
				userPickService.updateByPrimaryKeySelective(userPick);
			}
			result = userService.updateUserBasic(userBasic);
			userBasic = userService.selectByPrimaryKey(userBasic.getId());
			userService.userAttrHandler(userBasic);
			session.setAttribute("user", userBasic);
		}
		return result;
	}

	//修改密码
	@PutMapping("modifyPwd")
	@ResponseBody
	public Object resetPwd(@MyParam UserBasic userBasic,HttpSession session){
		boolean result = false;
		UserBasic user = (UserBasic) session.getAttribute("user");
		userBasic.setPassword(new Md5Hash(userBasic.getPassword(), ByteSource.Util.bytes(user.getEmail())).toString());
        result = userService.updateByPrimaryKeySelective(userBasic);
		return result;
	}

	/**
	 * 用户详情维护
	 *
	 * @return 返回Json格式
	 */
	@PutMapping("detail")
	@ResponseBody
	@CachePut(key = "'detail-'+#userDetail.id")
	public Object updateUserDetail(@MyParam UserDetail userDetail,@MyParam String graduation0,@MyParam String graduation1,
								   @MyParam  String birthplace_province,@MyParam String birthplace_city) {
		boolean result = false;
		if (graduation1 != null) {
			userDetail.setGraduation(graduation0 + "-" + graduation1);
		} else {
			userDetail.setGraduation(graduation0);
		}
		if (birthplace_city != null) {
			userDetail.setBirthplace(birthplace_province + "-" + birthplace_city);
		} else {
			userDetail.setBirthplace(birthplace_province);
		}
		logger.debug(userDetail);
		if (userDetailService.selectByPrimaryKey(userDetail) != null) {
			result = userDetailService.updateByPrimaryKey(userDetail);
		} else {
			result = userDetailService.insertSelective(userDetail);
		}
		return result;
	}

	/**
	 * 用户身份认证
	 *
	 * @return 返回Json格式
	 */
	@PutMapping("confirm")
	@ResponseBody
	@CachePut(key = "'detail-'+#userDetail.id")
	public Object UserConfirm(@MyParam UserDetail userDetail, HttpSession session) {
		boolean result = false;
		result = userDetailService.updateByPrimaryKeySelective(userDetail);
		UserBasic user = (UserBasic) session.getAttribute("user");
		user.setAuthenticated(true);
		session.setAttribute("user", user);
		return result;
	}

	/**
	 * 用户生活工作维护
	 *
	 * @return 返回Json格式
	 */
	@PutMapping("life")
	@ResponseBody
	@CachePut(key = "'life-'+#userLife.id")
	public Object updateUserLife(@MyParam UserLife userLife) {
		boolean result = false;
		logger.debug(userLife);
		if (userLife != null) {
			result = userLifeService.insertUserLife(userLife);
		}
		return result;
	}

	/**
	 * 用户婚姻观维护
	 *
	 * @return 返回Json格式
	 */
	@PutMapping("status")
	@ResponseBody
	@CachePut(key = "'status-'+#userStatus.id")
	public Object updateUserStatus(@MyParam UserStatus userStatus) {
		boolean result = false;
		logger.debug(userStatus);
		if (userStatusService.selectByPrimaryKey(userStatus) != null) {
			result = userStatusService.updateByPrimaryKey(userStatus);
		} else {
			result = userStatusService.insertSelective(userStatus);
		}
		return result;
	}

	/**
	 * 用户择偶条件维护
	 *
	 * @return 返回Json格式
	 */
	@PutMapping("pick")
	@ResponseBody
	@CachePut(key = "'pick-'+#userPick.id")
	public Object updateUserPick(@MyParam UserPick userPick,@MyParam String workplace_province1,@MyParam String workplace_city1
			,@MyParam String birthplace_province1,@MyParam String birthplace_city1) {
		boolean result;
		if (workplace_city1 != null) {
			userPick.setWorkplace(workplace_province1 + "-" + workplace_city1);
		} else {
			userPick.setWorkplace(workplace_province1);
		}
		if (birthplace_city1 != null) {
			userPick.setBirthplace(birthplace_province1 + "-" + birthplace_city1);
		} else {
			userPick.setBirthplace(birthplace_province1);
		}
		logger.debug(userPick);
		if (userPickService.selectByPrimaryKey(userPick) != null) {
			result = userPickService.updateByPrimaryKey(userPick);
		} else {
			result = userPickService.insertSelective(userPick);
		}
		return result;
	}

}
