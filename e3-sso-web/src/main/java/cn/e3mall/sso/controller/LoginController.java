package cn.e3mall.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.sso.service.LoginService;

/**
 * 登录controller
 * 
 * @author yangheng
 *
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Value("${TOKEN_KEY}")
    private String TOKEN_KEY;

    @RequestMapping("/page/login")
    public String showLogin(String redirect,Model model ) {
        //将附带的"登录后跳转地址"传到前台,有js完成页面的跳转。
        model.addAttribute("redirect", redirect);
        return "login";
    }
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public E3Result login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        // 判断登录是否成功
        E3Result e3Result = loginService.userLogin(username, password);
        if (e3Result.getStatus() == 200) {
            // 登录成功，将token写入Cookie
            String token = e3Result.getData().toString();
            CookieUtils.setCookie(request, response, TOKEN_KEY, token);
        }

        return e3Result;
    }
}
