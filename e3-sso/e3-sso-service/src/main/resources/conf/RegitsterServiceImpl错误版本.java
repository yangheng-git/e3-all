package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.RegisterService;

/**
 * @author yangheng 用户注册Service
 */
/**
 * @author yangheng
 *
 */
@Service
public class RegitsterServiceImpl错误版本 /*implements RegisterService */{

    @Autowired
    private TbUserMapper userMapper;

    /**
     * 检查用户注册 用户名，手机号，邮箱有没有被占用
     * 
     * @author yangheng
     *
     */
    
    public E3Result checkdata(String param, Integer type) {
        // 判断param的类型。 type=1 ：用户名 2：手机号 3：邮箱 。然后进行判断。 是否填写
        TbUserExample userExample = new TbUserExample();
        Criteria criteria = userExample.createCriteria();
        if (type == 1) {
            criteria.andUsernameEqualTo(param);
        } else if (type == 2) {
            criteria.andPhoneEqualTo(param);
        } else if (type == 3) {
            criteria.andEmailEqualTo(param);
        } else {
            return E3Result.build(400, "错误的数据，请检查");
        }
        // 执行查询
        List<TbUser> list = userMapper.selectByExample(userExample);
        // 判断结果中是否包含数据。
        if (list != null && list.size() > 0) {
            // 如果有数据返回flase
            return E3Result.ok(false);
        }
        // 如果没有数据返回true
        return E3Result.ok(true);
    }

    /**
     * (non-Javadoc) 注册Service
     * 
     * @see cn.e3mall.sso.service.RegisterService#regitster(cn.e3mall.pojo.TbUser)
     */
    
    public E3Result regitster(TbUser user) {
        // 注册前，判断数据的完整性。 和再次确认有没有被占用
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())
                || StringUtils.isBlank(user.getPhone())) {
            // 有任意一项缺少，就不进行注册
            return E3Result.build(400, "数据项缺少，请检查后重新注册");
        }
        // 判断数据项是否重复。 避免前端js验证失败的问题
        E3Result checkdataResult= checkdata(user.getUsername(), 1);
        if((boolean) checkdataResult.getData()){
            return E3Result.build(400, "用户名被占用");
        }
        checkdataResult =  checkdata(user.getPhone(), 2);
        if((boolean) checkdataResult.getData()){
            return E3Result.build(400, "手机号被占用");
        }
        checkdataResult =  checkdata(user.getEmail(), 3);
        if((boolean) checkdataResult.getData()){
            return E3Result.build(400, "邮箱被占用");
        }
        //补全pojo属性
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //对密码进行md5加密
        String  md5pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5pass);
        //把数据插入到数据库
        userMapper.insert(user);
        return E3Result.ok();
    }

}
