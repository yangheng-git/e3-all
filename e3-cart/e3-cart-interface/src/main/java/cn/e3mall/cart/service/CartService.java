package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface CartService {

    E3Result addCart(Long userId, Long itemId, int num);

    List<TbItem> getCartList(Long userId);

    E3Result mageCart(Long userId, List<TbItem> cartList);

    E3Result updataCartItemNum(Long userId, Long itemId, int num);

    E3Result deleteCartItem(Long userId,Long itemId);
    
    E3Result clearCartItem(Long userId);
}
