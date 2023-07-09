package com.hpe.findlover.contoller.front;

import com.github.pagehelper.PageHelper;
import com.hpe.findlover.model.Letter;
import com.hpe.findlover.model.UserBasic;
import com.hpe.findlover.model.UserPick;
import com.hpe.findlover.service.*;
import com.hpe.findlover.util.LoverUtil;
import com.hpe.findlover.util.WordFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 私信
 */
@Controller
@RequestMapping
public class LetterController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LetterService letterService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPickService userPickService;
    @Autowired
    private FollowService followService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private VisitTraceService visitTraceService;

    //私信页面初始化
    @GetMapping("letter")
    public String letter(HttpSession session, Model model) throws Exception {
        UserBasic userBasic = (UserBasic) session.getAttribute("user");
        //获取联系人列表
        Map<String,Object> map= letterService.selectOther(userBasic.getId());
        model.addAttribute("map", map);
        logger.debug("查询结果："+map);
        Integer userId = ((UserBasic) session.getAttribute("user")).getId();
        UserPick userPick = userPickService.selectByPrimaryKey(userId);
        List<UserBasic> stars = LoverUtil.getRandomStarUser(userPick, 4, userService);
        userService.userAttrHandler(stars);
        model.addAttribute("stars", stars);
        logger.debug("stars: " + stars);
        //右侧信息条数
        model.addAttribute("letterCount", letterService.selectUnreadCount(userId));
        model.addAttribute("followCount", followService.selectFollowCount(userId));
        model.addAttribute("noticeCount", noticeService.selectUnReadNotice(userBasic).size());
        model.addAttribute("visitTraceCount",visitTraceService.selectUnreadCount(userId));
        return "front/letter";
    }

    //初始化聊天界面
    @PostMapping("letter")
    @ResponseBody
    public List<Letter> letter(HttpSession session, int currentPage, int lineSize, int otherUserId) throws Exception {
        UserBasic user = (UserBasic) session.getAttribute("user");
        PageHelper.startPage(currentPage, lineSize);
        List<Letter> list = letterService.selectLetter(user.getId(), otherUserId);
        return list;
    }

    //读信息
    @PostMapping("readLetter")
    @ResponseBody
    public String readLetter(HttpSession session, int letterId) {
        UserBasic user = (UserBasic) session.getAttribute("user");
        Letter letter = new Letter();
        letter.setId(letterId);
        letter.setStatus(1);
        letterService.readLetter(letter);
        return "ok";
    }

    //发送信息
    @PostMapping("sendLetter")
    @ResponseBody
    public String sendLetter(HttpSession session,int otherUserId, String content){
        UserBasic user = (UserBasic) session.getAttribute("user");
            Letter letter=this.returnLetter(user,otherUserId,content);
            logger.debug("letter="+letter.toString());
            boolean check = WordFilter.checkWords(content);
            if(check){
                return "对不起您的信息包含敏感词汇，请重新发送";
            }
            else if (letterService.insert(letter)){
                return "ok";
            }
            else{
                return "遇见未知错误";
            }
    }
    //返回私信对象
    public Letter returnLetter(UserBasic user,int otherUserId, String content){
        Letter letter=new Letter();
        letter.setSendId(user.getId());
        letter.setRecieveId(otherUserId);
        letter.setContent(content);
        letter.setSendTime(new Date());
        letter.setStatus(0);
        return letter;
    }
}
