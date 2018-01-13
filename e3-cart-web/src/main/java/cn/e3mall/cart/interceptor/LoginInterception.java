package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

/**
 * 用户登录拦截处理器
 * 
 * @author yangheng
 *
 */
public class LoginInterception implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 前处理。执行handler之前执行此方法。
        // 返回true，放行。 返回flase，拦截。
        // 用户登录的拦截过程
        // 1、从cookie中取token
        String token = CookieUtils.getCookieValue(request, "token");
        // 2、 如果没有token,是未登录状态，直接放行。
        if (StringUtils.isBlank(token)) {
            return true;
        }
        // 3、 取得token,需要调用sso系统的服务，根据token 取用户信息
        E3Result result = tokenService.getUserByToken(token);
        // 4、 如果没有取到用户信息，是登录过期，直接放行，
        if (result.getStatus() != 200) {
            return true;
        }
        // 5、 取到用户信息，是登录状态。
        TbUser user = (TbUser) result.getData();
        // 6、 把用户信息放到request中，只需要在Controller中判断request中是否包含user信息。 放行。
        request.setAttribute("user", user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // handler执行之后，返回modelAndView之前

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 完成处理。返回modelandView 之后。
        // 可以在此处理异常。

    }

}
