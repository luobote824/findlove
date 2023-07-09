package com.hpe.findlover.service.impl;

import com.hpe.findlover.mapper.LetterMapper;
import com.hpe.findlover.model.Letter;
import com.hpe.findlover.model.LetterUser;
import com.hpe.findlover.model.UserBasic;
import com.hpe.findlover.service.LetterService;
import com.hpe.util.BaseTkMapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LetterServiceImpl extends BaseServiceImpl<Letter> implements LetterService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LetterMapper letterMapper;
    @Autowired
    public LetterServiceImpl(LetterMapper letterMapper) {
        this.letterMapper = letterMapper;
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String,Object> selectOther(int userid) {
        Map<String,Object> map=new HashMap<>();
        List<LetterUser> list=letterMapper.selectOther(userid);
        int sum=0;
        List<Integer> num=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            int count=letterMapper.selectAmount(list.get(i).getId(),userid);
            num.add(count);
            sum+=count;
        }
        num.add(sum);
        map.put("list",list);
        map.put("num",num);
        return map;
    }

    @Override
    public List<Letter> selectLetter(@Param("uid") int uid, int otherId) {
        return letterMapper.selectLetter(uid,otherId);
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean readLetter(Letter letter) {
        if (letterMapper.updateByPrimaryKeySelective(letter)<=0){
            return false;
        }
        return true;
    }

    @Override
    public Integer selectUnreadCount(int userid) {
        return letterMapper.selectUnreadCount(userid);
    }

    @Override
    public BaseTkMapper<Letter> getMapper() {
        return letterMapper;
    }
}
