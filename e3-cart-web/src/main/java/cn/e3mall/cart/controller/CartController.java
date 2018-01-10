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

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
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
    @ResponseBody
    public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num,
            HttpServletRequest request, HttpServletResponse response) {
        // 从cookie中取商品列表、
        List<TbItem> cartlist = getCartListFromCookie(request);
        // 立个flag，判断商品列表中是否存在要添加的商品。
        Boolean flag = false;
        // 判断商品在商品列表中是否存在。
        for (TbItem tbItem : cartlist) {

            if (itemId == tbItem.getId().longValue()) {
                flag = true;
                // 如果存在，数量相加。
                // 知识点 封装数据类型是对象。 对象用==比较，比较的是地址。 需要将封装数据类型转换为基本数据类型，才能用==比较。
                tbItem.setNum(tbItem.getNum() + num);
                break;
            }
        }
        // 如果不存在。
        if(!flag){
            // 把商品添加到商品列表。
            TbItem item = itemService.selectByID(itemId);
            cartlist.add(item);
        }
        // 写入Cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartlist), COOKIE_CART_EXPIRE, true);
        
        //返回成功页面。
        return "cartSuccess";

    }

    /**
     * 抽取方法，从cookie中获取购物车列表。 从request中获取商品列表。是json，
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
}
