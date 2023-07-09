package com.hpe.findlover.test;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class testping {
    public static void main(String[] args) {
        //new Jedis对象
        Jedis jedis = new Jedis();
        //jedis的所有命令就是之前我们之前的指令
        System.out.println(jedis.ping());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello","world");
        jsonObject.put("name","kukangshen");
        //开启事务
        Transaction multi = jedis.multi();
        String result  = jsonObject.toJSONString();
        try {
            multi.set("user1",result);
            multi.set("user2",result);
            multi.set("user3",result);
            multi.exec();//执行事务
        }catch (Exception e){
            multi.discard();//放弃事务
        }
        jedis.close();

    }
}
