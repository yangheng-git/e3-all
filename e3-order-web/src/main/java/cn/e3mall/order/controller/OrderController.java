package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

/**
 * 订单管理Controller
 * 
 * @author yangheng
 *
 */
@Controller
public class OrderController {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/order/order-cart")
    public String showOrderCart(HttpServletRequest request) {
        // 展示结算页面。需要返回逻辑视图，要带上商品列表，从服务中取（redis）
        // 获取用户信息。 发起订单一定要登录。在拦截器中进行了拦截操作
        TbUser user = (TbUser) request.getAttribute("user");
        // 根据用户id取收货地址列表。
        // 目前使用静态数据
        // 取支付方式
        // 目前是静态数据

        // 根据用户id取购物车列表
        List<TbItem> cartList = cartService.getCartList(user.getId());
        request.setAttribute("cartList", cartList);

        return "order-cart";
    }

    /**
     * 点击提交订单，创建一个订单。
     * 
     * @return
     * 
     * 
     */
    @RequestMapping(value = "/order/create", method = RequestMethod.POST)
    public String createOrder(OrderInfo orderInfo, HttpServletRequest request) {
        // 取用户信息。
        TbUser user = (TbUser) request.getAttribute("user");
        // 把用户信息添加到orderInfo
        orderInfo.setUserId(user.getId());
        orderInfo.setBuyerNick(user.getUsername());
        // 调用服务生成订单
        E3Result e3Result = orderService.createOrder(orderInfo);
        // 如果订单生成成功，需要删除购物车
        if (e3Result.getStatus() == 200) {
            // 清空购物车
            cartService.clearCartItem(user.getId());
        }
        // 把订单号传给页面
        request.setAttribute("orderId", e3Result.getData());
        // 应付金额
        request.setAttribute("payment", orderInfo.getPayment());
        // 返回逻辑试图
        return "success";

    }
}
