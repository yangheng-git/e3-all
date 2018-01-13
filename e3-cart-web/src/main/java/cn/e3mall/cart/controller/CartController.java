package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

/**
 * 购物车管理Controller
 * 
 * @author yangheng
 *
 */
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private ItemService itemService;
    @Value("${COOKIE_CART_EXPIRE}")
    private Integer COOKIE_CART_EXPIRE;

    /**
     * 添加商品到购物车，购物车放到cookie
     * 
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/cart/add/{itemId}")
    public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num,
            HttpServletRequest request, HttpServletResponse response) {
        // 判断用户是否登录。 如果已经登录，将购物车放入redis中、
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            // 用户已登录，将购物车内容放入redis. 然后返回逻辑视图
            cartService.addCart(user.getId(), itemId, num);

            return "cartSuccess";
        }

        // 从cookie中取商品列表、
        List<TbItem> cartlist = getCartListFromCookie(request);
        // 立个flag，判断商品列表中是否存在要添加的商品。
        Boolean flag = false;
        // 判断商品在商品列表中是否存在。
        for (TbItem tbItem : cartlist) {

            // 如果存在，数量相加。
            if (itemId == tbItem.getId().longValue()) {
                flag = true;
                // 知识点 封装数据类型是对象。 对象用==比较，比较的是地址。 需要将封装数据类型转换为基本数据类型，才能用==比较。
                tbItem.setNum(tbItem.getNum() + num);
                break;
            }
        }
        // 如果不存在。
        if (!flag) {
            // 根据商品id查询商品信息，得到一个tbItem对象。
            TbItem item = itemService.selectByID(itemId);
            // 设置商品数量
            item.setNum(num);
            // 取一张图片
            String image = item.getImage();
            if (StringUtils.isNotBlank(image)) {
                item.setImage(image.split(",")[0]);
            }
            // 添加商品到商品列表。
            cartlist.add(item);
        }
        // 将商品列表写入Cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartlist), COOKIE_CART_EXPIRE, true);

        // 返回成功页面。
        return "cartSuccess";

    }

    /**
     * 获取购物车列表、从kookie中 抽取方法，从cookie中获取购物车列表。 从request中获取商品列表。是json，
     * 如果列表中没东西就返回一个空集合，如果有东西就转化为集合返回。
     * 
     * @param request
     * @return
     */
    private List<TbItem> getCartListFromCookie(HttpServletRequest request) {
        String json = CookieUtils.getCookieValue(request, "cart", true);
        // 判断json是否为空
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        // 把json转换为商品列表。
        List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
        return list;
    }

    /**
     * 展示购物车列表 展示购物车页面,需要将购物车列表展示出来
     * <p>
     * 登录的情况：从服务（redis）中取购物车列表。
     * <p>
     * 未登录的情况：从cookie中取购物车列表。
     * <p>
     * 需要将cookie中购物车列表信息与服务端购物车列表信息进行和合并、
     * 
     * 
     */
    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request, HttpServletResponse response) {
        // 从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);

        // 判断用户是否登录
        TbUser user = (TbUser) request.getAttribute("user");
        // 登录的话，从服务端取购物车列表。
        if (user != null) {
            // 需要将cookie中购物车列表信息与服务端购物车列表信息进行和合并、
            cartService.mageCart(user.getId(), cartList);
            // 合并后，将cookie中的购物车删除。
            CookieUtils.deleteCookie(request, response, "cart");
            // 从服务端取购物车列表。取合并后的购物车列表
            cartList = cartService.getCartList(user.getId());
        }

        // 把列表传递给页面
        request.setAttribute("cartList", cartList);
        // 返回逻辑视图
        return "cart";
    }

    /**
     * 更新购物车商品的数量
     * 
     * springmvc的坑： 在springmvc中认为。如果你请求地址为*.html，那么你必须返回一个html页面，不然会出现406错误。
     * 为了避免这种错误， 添加拦截地址 *.action.
     * 
     */
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public E3Result updateCartItemNum(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request,
            HttpServletResponse response) {
        // 判断用户有没有登录。登陆的话，更新服务端的购物车列表中的数量。
        TbUser user = (TbUser) request.getAttribute("user");
        if(user != null){
            cartService.updataCartItemNum(user.getId(),itemId,num);
            return E3Result.ok();
        }

        // 从cookie中取出商品列表,更改商品数量
        List<TbItem> cartList = getCartListFromCookie(request);
        for (TbItem tbItem : cartList) {
            if (tbItem.getId().longValue() == itemId) {
                tbItem.setNum(num);
                break;
            }

        }
        // 将更改后的商品列表重新返回cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
        return E3Result.ok();
    }

    /**
     * 删除商品 删除商品后，应该刷新购物车的展示页面
     */
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCartByItemId(@PathVariable Long itemId, HttpServletRequest request,
            HttpServletResponse response) {
        //判断用户是否登录
        TbUser user = (TbUser) request.getAttribute("user");
        if(user != null){
            cartService.deleteCartItem(user.getId(),itemId);
            return "redirect:/cart/cart.html";
        }
        
        
        // 从cookie中取出商品列表
        List<TbItem> cartList = getCartListFromCookie(request);
        // 循环中判断，如果商品列表中存在这个商品。删除
        for (TbItem tbItem : cartList) {
            if (tbItem.getId().longValue() == itemId) {
                // 注意： 在循环中删除一个元素后，需要停止循环，不然进行下次循环时会报错。
                cartList.remove(tbItem);
                break;
            }
        }

        // 将更改后的商品列表重新返回cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
        return "redirect:/cart/cart.html";
    }

}
