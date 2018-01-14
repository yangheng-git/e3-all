package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

/**订单管理Controller
 * @author yangheng
 *
 */
@Controller
public class OrderController {

    @Autowired
    private CartService cartService;
    
    @RequestMapping("/order/order-cart")
    public String showOrderCart(HttpServletRequest request){
        //展示结算页面。需要返回逻辑视图，要带上商品列表，从服务中取（redis）
        //获取用户信息。 发起订单一定要登录。在拦截器中进行了拦截操作
        TbUser user= (TbUser) request.getAttribute("user");
        List<TbItem> cartList = cartService.getCartList(user.getId());
        request.setAttribute("cartList", cartList);
        
        return "order-cart";
    }
}
