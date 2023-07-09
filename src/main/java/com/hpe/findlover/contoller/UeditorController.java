package com.hpe.findlover.contoller;

import com.alibaba.fastjson.JSONObject;
import com.hpe.findlover.service.UploadService;
import com.hpe.findlover.util.LoverUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("ueditor")
public class UeditorController {
    private Logger logger = LogManager.getLogger(UeditorController.class);

    @Autowired
    private UploadService uploadService;

    /**
     * ueditor返回的配置文件，该config.json文件从资源目录下读取
     * @return
     */
    @RequestMapping("uploadconfig")
    @ResponseBody
    public String uploadConfig(HttpServletRequest request){
        BufferedReader reader = null;
        StringBuilder laststr = new StringBuilder();
        try{
            ClassPathResource resource = new ClassPathResource("static/plugins/ueditor/jsp/config.json");
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr.append(tempString);
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject result = JSONObject.parseObject(laststr.toString());
        result.put("imageUrlPrefix", LoverUtil.getBasePath(request) + "/file?path=");
        logger.debug("返回的ueditor配置文件内容："+JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }

    /**
     * 选择图片上传时对应的响应映射方法
     * @param upfile
     * @return Object
     * @throws Exception
     */
    @RequestMapping("uploadfile")
    @ResponseBody
    public Object uploadFile(MultipartFile upfile) throws Exception {
        //@RequestParam(value = "editormd-image-file", required = false)
        String uploadPhotoPath = null;
        if (upfile != null) {
            uploadPhotoPath = uploadService.uploadFile(upfile);
            logger.debug("文件名字" + upfile.getName());
            logger.debug("文件类型" + upfile.getContentType());
            logger.debug("文件大小" + upfile.getSize());
            logger.debug("源文件名称" + upfile.getOriginalFilename());
            logger.debug("上传路径" + uploadPhotoPath);
        }
        logger.debug("上传路径" + uploadPhotoPath);
        Map<String, Object> map = new HashMap<>();
        //uedit要求返回格式
        if(uploadPhotoPath!=null) {
            map.put("url", uploadPhotoPath);
            map.put("title", upfile.getOriginalFilename());
            map.put("original", upfile.getOriginalFilename());
            map.put("state", "SUCCESS");
        }
        return map;
    }
}
