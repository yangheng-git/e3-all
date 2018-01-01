package cn.e3mall.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.RegisterService;

/**
 * 注册功能Controller
 * 
 * @author yangheng
 *
 */
@Controller
public class RegitsterController {

    @Autowired
    private RegisterService registerService;

    /**
     * 展示注册页面
     * 
     * @return
     */
    @RequestMapping("/page/register")
    public String showRegister() {
        return "register";
    }

    /**
     * 前端发来ajax请求。 判断数据的完整性。
     * 
     * @param param
     * @param type
     * @return e3Result 如果填写的数据已村子，返回false
     */
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public E3Result checkData(@PathVariable String param, @PathVariable Integer type) {
        return registerService.checkData(param, type);
    }

    /*
     * 注册
     * 
     * @param user
     * 
     * @return
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    @ResponseBody
    public E3Result register(TbUser user) {
        E3Result e3Result = registerService.register(user);
        return e3Result;
    }
}
