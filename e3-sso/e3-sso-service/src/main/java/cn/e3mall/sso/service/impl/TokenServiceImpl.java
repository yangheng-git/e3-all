package cn.e3mall.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

/**
 * 根据token取用户信息 前台传入token ，从redis取用户信息、
 * 
 * @author yangheng
 *
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JedisClient jedisClient;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;
    
    @Override
    public E3Result getUserByToken(String token) {
        //根据token 从redis中取用户信息。 
        String json = jedisClient.get("SESSION:"+token);
        //如果没取到信息，返回用户已过期。
        if (StringUtils.isBlank(json)) {
            return E3Result.build(201, "用户已过期，请重新登录");
        }
        //取到信息后，更新过期时间
        jedisClient.expire("SESSION:"+token, SESSION_EXPIRE);
        //返回取到的用户信息。 用e3Result包装一下。 
        TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
        
        return E3Result.ok(user);
    }

}
