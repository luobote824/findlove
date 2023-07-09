package com.hpe.findlover.contoller.front;

import  com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpe.findlover.model.*;
import com.hpe.findlover.service.*;
import com.hpe.findlover.util.Constant;
import com.hpe.findlover.util.LoverUtil;
import com.hpe.findlover.util.SessionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("search")
public class SearchController {
    private Logger logger = LogManager.getLogger(SearchController.class);

    @Autowired
    private DictService dictService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPickService userPickService;

    /**
     *
     *
     * search页面初始化
     *
     */
    @GetMapping
    public String search(Model model, HttpServletRequest request) throws Exception {
        //职业
        List<Dict> jobList = dictService.selectDictByType("job");
        //婚史
        List<Dict> marryStatusList = dictService.selectDictByType("marry_status");
        //学历
        List<Dict> educationList = dictService.selectDictByType("education");
        //住房条件
        List<Dict> liveConditionList = dictService.selectDictByType("live_condition");
        //星座
        List<Dict> zodiacList = dictService.selectDictByType("zodiac");
        //生肖
        List<Dict> animalList = dictService.selectDictByType("animal");
        //信仰
        List<Dict> religionList = dictService.selectDictByType("religion");
        model.addAttribute("jobList",jobList);
        model.addAttribute("marryStatusList",marryStatusList);
        model.addAttribute("educationList",educationList);
        model.addAttribute("liveConditionList",liveConditionList);
        model.addAttribute("zodiacList",zodiacList);
        model.addAttribute("animalList",animalList);
        model.addAttribute("religionList",religionList);
        /*2、搜索位用户择偶条件信息*/
        UserBasic user = SessionUtils.getSessionAttr("user",UserBasic.class);
        UserPick userPick = userPickService.selectByPrimaryKey(user.getId());
        logger.info("userPick.."+userPick);
        model.addAttribute("userPick",userPick);
        /*3、推荐用户信息*/
        List<UserBasic> userBasicStarPick = LoverUtil.getRandomStarUser(userPick,Constant.SEARCH_SHOW_STAR_USER_NUMBER,userService);
        userBasicStarPick.forEach(logger::info);
        model.addAttribute("userBasicStarPick",userBasicStarPick);
        return "front/search";
    }

    //页面加载完成时第一次获取用户择偶条件的基本信息
    @GetMapping("/initUserPick")
    @ResponseBody
    public UserPick initUserPick(HttpServletRequest request) throws Exception {
        UserBasic user = SessionUtils.getSessionAttr("user",UserBasic.class);
        UserPick userPick = null;
        if (user!=null) {
            userPick = userPickService.selectByPrimaryKey(user.getId());
        }
        return userPick;
    }

    //页面加载时，根据user-pick搜索的用户
    @RequestMapping("/initSearchUser")
    @ResponseBody
    public UserInfo initSearchUser(@Param("pageNum")Integer pageNum,HttpServletRequest request) {
        logger.info("pageNum......" + pageNum);
        UserBasic userBasic = SessionUtils.getSessionAttr("user",UserBasic.class);
        logger.info("userBasic===="+userBasic);
        UserPick userPick = userPickService.selectByPrimaryKey(userBasic.getId());
        if (userPick.getWorkplace()!=null) {
            userPick.setWorkplace("%" + userPick.getWorkplace() + "%");
        }
        if (userPick.getBirthplace()!=null) {
            userPick.setBirthplace("%" + userPick.getBirthplace() + "%");
        }
        logger.info("userPick:"+userPick);
        PageHelper.startPage(pageNum,9);
        List<UserBasic> userBasicList = userService.selectUserByUserPick(userPick);
        if(userBasicList.size()>0) {
            //封装用户数据
            LoverUtil.formatUserInfo(userBasicList);
            PageInfo page = new PageInfo(userBasicList);
            return new UserInfo("success", page);
        }else {
            return new UserInfo("error", null);
        }
    }

    @RequestMapping("/getSearchUser")
    @ResponseBody
    public UserInfo getSearchUser(HttpServletRequest request,Search search, @Param("pageNum")Integer pageNum) {
        UserBasic userBasic = SessionUtils.getSessionAttr("user",UserBasic.class);
        search.setId(userBasic.getId());
        logger.info("search......" + search.toString());
        logger.info("pageNum......" + pageNum);
        PageHelper.startPage(pageNum,9);

        List<UserBasic> userBasicList = userService.selectUserBySearch(search);
        if(userBasicList.size()>0) {
            for (UserBasic user:userBasicList){
                logger.info("用户详细"+user.getUserDetail());
            }
            //封装用户数据
            LoverUtil.formatUserInfo(userBasicList);
            PageInfo page = new PageInfo(userBasicList);
            return new UserInfo("success", page);
        }else {
            return new UserInfo("error", null);
        }
    }

    public void formatPick(UserPick userPick){
        if(userPick.getBirthplace()!=null) {
            userPick.setBirthplace(null);
        }
        if (userPick.getEducation()!=null) {
            userPick.setEducation(null);
        }
    }

    public void formatSearch(Search search){
        if (search.getEducation()!=null) {
            search.setEducation(null);
        }
       if (search.getLiveCondition()!=null) {
           search.setLiveCondition(null);
       }
       if (search.getBirthplace()!=null) {
           search.setBirthplace(null);
       }

    }

}
