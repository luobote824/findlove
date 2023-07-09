package com.hpe.findlover.contoller.front;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpe.findlover.model.*;
import com.hpe.findlover.service.SuccessStoryLikeService;
import com.hpe.findlover.service.SuccessStoryReplyService;
import com.hpe.findlover.service.SuccessStoryService;
import com.hpe.findlover.service.UploadService;
import com.hpe.findlover.util.SessionUtils;
import com.hpe.findlover.util.WordFilter;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("success_story")
public class SuccessStoryController {
    @Autowired
    private SuccessStoryService successStoryService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private SuccessStoryLikeService successStoryLikeService;
    @Autowired
    private SuccessStoryReplyService successStoryReplyService;
    Logger logger = LoggerFactory.getLogger(SuccessStoryController.class);


    //写成功故事
    @PostMapping("write_story")
    public String uploadEssay(HttpSession session, String essays, int otherId, String title,
                              MultipartFile ephoto, String tcontent,Model model,HttpServletRequest request) throws Exception{
        SuccessStory successStory=new SuccessStory();
        UserBasic user= (UserBasic) session.getAttribute("user");
        successStory.setLeftUser(user.getId());
        successStory.setRightUser(otherId);
        successStory.setTitle(title);
        successStory.setStatus(2);
        successStory.setSuccessTime(new Date());
        successStory.setContent(uploadService.uploadFile(essays,"txt"));
        successStory.setPhoto(uploadService.uploadFile(ephoto));
        successStory.setBrief(tcontent);
        successStory.setLikeCount(0);
        successStory.setReplyCount(0);
        model.addAttribute("msg", essays);
        if (successStoryService.insertStory(successStory,user.getId(),request)){
            return "front/story_view";
        }
        return "front/error";
    }
    @GetMapping("write_story")
    public String writeStory(){
        return "front/write_story";
    }

    //初始化成功故事页面
    @GetMapping
    public String successStory(Model model){
        PageHelper.startPage(1,8,"success_time desc");
        List<SuccessStory> list= successStoryService.selectAllByStatus();
        model.addAttribute("list",list);
        return"front/success_story";
    }

    //加载更多成功故事
    @PostMapping("load_more")
    @ResponseBody
    public Object loadMore(int lineSize,int currentPage){
        PageHelper.startPage(currentPage,lineSize,"success_time desc");
        PageInfo<SuccessStory> pageInfo= new PageInfo<>(successStoryService.selectAllByStatus());
        return pageInfo;
    }

    //加载更多评论
    @PostMapping("story_reply/load_more")
    @ResponseBody
    public Object loadMoreReply(int storyId,int currentPage,int lineSize){
        PageHelper.startPage(currentPage,lineSize,"reply_time desc");
        PageInfo<SuccessStoryReply> pageInfo=new PageInfo<>(successStoryReplyService.selectByStoryId(storyId));
        return pageInfo;
    }

    //成功故事详情
    @GetMapping("success_story_info/{id}")
    public String successStoryInfo(@PathVariable int id,HttpSession session,Model model) throws Exception{
        logger.debug("访问success_story_info Controller");
        SuccessStory successStory=successStoryService.selectByPrimaryKey(id);
        String filename = successStory.getContent();
        byte[] bytes = uploadService.downloadFile(filename);
        String content = new String(bytes, "utf-8");
        UserBasic userBasic= (UserBasic) session.getAttribute("user");
        Boolean like=successStoryLikeService.selectByStoryIdAndUserId(id,userBasic.getId());
        int likeCount=successStoryLikeService.selectCountByStoryId(id);
        PageHelper.startPage(1,5,"reply_time desc");
        PageInfo<SuccessStoryReply> pageInfo=new PageInfo<>(successStoryReplyService.selectByStoryId(id));
        model.addAttribute("successStory",successStory);
        model.addAttribute("content", content);
        model.addAttribute("like",like?"true":"false");
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("reply",pageInfo);
        return "front/success_story_info";
    }


    @GetMapping("story_view")
    public String successStoryView() {
        return "front/story_view";
    }
    //评论
    @PostMapping("reply")
    @ResponseBody
    public String reply(String content, int storyId,HttpSession session){
        UserBasic userBasic= SessionUtils.getSessionAttr("user",UserBasic.class);
        SuccessStoryReply successStoryReply=new SuccessStoryReply();
        successStoryReply.setContent(content);
        successStoryReply.setSsId(storyId);
        successStoryReply.setUserId(userBasic.getId());
        successStoryReply.setReplyTime(new Date());
        //敏感词汇检测
        boolean check = WordFilter.checkWords(content);
        if(check){
            return "对不起您的评论包含敏感词汇，请重新评论";
        }
        else if (successStoryReplyService.insert(successStoryReply)){
            return "true";
        }
        return "false";
    }

    //点赞
    @PostMapping("like")
    @ResponseBody
    public String like(int storyId,HttpSession session){
        UserBasic userBasic= SessionUtils.getSessionAttr("user",UserBasic.class);
        SuccessStoryLike successStoryLike=new SuccessStoryLike();
        successStoryLike.setLikeTime(new Date());
        successStoryLike.setSuccessStoryId(storyId);
        successStoryLike.setUserId(userBasic.getId());
        if (successStoryLikeService.insert(successStoryLike)){
            return "true";
        }
        return "false";
    }

    //成功故事确认
    @GetMapping("confirmSuccessStory/{id}")
    public String confirm(@PathVariable int id, Model model){
        logger.debug("需要确认的id:"+id);
        SuccessStory successStory = successStoryService.selectByPrimaryKey(id);
        String filename = successStory.getContent();
        byte[] bytes = uploadService.downloadFile(filename);
        try {
            String content = new String(bytes, "utf-8");
            model.addAttribute("essay", content);
            model.addAttribute("successStory", successStory);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "error";
        }
        return "front/confirm_success_story";
    }

    //用户对象审核通过
    @PutMapping("pass")
    @ResponseBody
    public String pass(SuccessStory successStory,@RequestParam int left){
        logger.debug("通过的成功故事："+successStory);
        UserBasic userBasic = SessionUtils.getSessionAttr("user",UserBasic.class);
        if (successStoryService.checkUser(userBasic.getId(),left)){
            boolean result = successStoryService.updateByPrimaryKeySelective(successStory);
            if (result){
                return "success";
            }else {
                return "error";
            }
        }else {
            return "notSinglle";
        }
    }
}
