package cn.e3mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

/**
 * 用户登录拦截器 在用户点击“去结算”时，判断用户是否登录，
 * 
 * 并且要将购物车列表进行合并。
 * 
 * @author yangheng
 *
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${SSO_URL}")
    private String SSO_URL;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private CartService cartService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 从cookie中取token
        String token = CookieUtils.getCookieValue(request, "token");
        // 判断token是否存在
        if (StringUtils.isBlank(token)) {
            // 如果token不存在，未登录状态。跳转到登录页面。用户登录成功后，再跳转回来。
            response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
            System.out.println( request.getRequestURI());
            return false;
            // 拦截。
        }
        // 如果token存在，需要调用sso系统服务，根据token取用户信息。
        E3Result e3Result = tokenService.getUserByToken(token);
        // 如果取不到用户信息，用户登录已过期，重新登录
        if (e3Result.getStatus() != 200) {
            // 如果token不存在，未登录状态。跳转到登录页面。用户登录成功后，再跳转回来。
            response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
            return false;
            // 拦截。
        }
        // 如果取得用户信息，是登陆状态，把用户信息放入request
        TbUser user= (TbUser) e3Result.getData();
        request.setAttribute("user", user);
        // 判断cokie中是否有购物车数据，如果有就合并到服务器
         String JsoncartList = CookieUtils.getCookieValue(request, "cart",true);
         if(StringUtils.isNotBlank(JsoncartList)){
             cartService.mageCart(user.getId(), JsonUtils.jsonToList(JsoncartList, TbItem.class));
         }
        // 放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

}
