package cn.e3mall.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.LoginService;

/**
 * 用户登录Service
 * 
 * @author yangheng
 *
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    /**
     * @see cn.e3mall.sso.service.LoginService#userLogin(java.lang.String,
     *      java.lang.String) 1、判断用户名，密码是否正确
     */
    @Override
    public E3Result userLogin(String username, String password) {
        // 判读用户名是否存在。
        TbUserExample userExample = new TbUserExample();
        Criteria userCriteria = userExample.createCriteria();
        userCriteria.andUsernameEqualTo(username);
        List<TbUser> list = userMapper.selectByExample(userExample);
        if (list == null || list.size() == 0) {
            // 用户不存在， 返回错误信息。
            return E3Result.build(400, "用户或密码错误，请检查后登录");
        }
        TbUser user = list.get(0);
        // 判断密码是否正确
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
            // 如果不正确，返回登录失败
            return E3Result.build(400, "用户或密码错误，请检查后登录");
        }

        // 密码正确，生成token
        String token = UUID.randomUUID().toString();
        // 把用户信息写入redis。 key:token value：用户信息
        user.setPassword(null);
        jedisClient.set("SESSION:" + token, JsonUtils.objectToJson(user));
        // 设置session的过期时间
        jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
        // 返回token

        return E3Result.ok(token);
    }

}
