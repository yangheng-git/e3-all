package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;

/**
 * 商品购物车处理服务
 * 
 * @author yangheng
 *
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private TbItemMapper itemMapper;
    @Value("${REDIS_CART_PRE}")
    private String REDIS_CART_PRE;

    /*
     * (non-Javadoc)
     * 
     * @see cn.e3mall.cart.service.CartService#addCart(java.lang.Long,
     * java.lang.Long, int) 添加购物车信息到redis中 数据类型是hash key: 用户id。field:商品id。
     * value：商品信息
     */
    @Override
    public E3Result addCart(Long userId, Long itemId, int num) {
        // 判断商品是否存在。
        Boolean hexists = jedisClient.hexists(REDIS_CART_PRE + ":" + userId, itemId + "");
        // 如果存在，数量相加
        if (hexists) {
            String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
            // 将json转为tbItem
            TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
            // 商品数量相加
            tbItem.setNum(num + tbItem.getNum());
            // 写回redis
            jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
            // 返回ok
            return E3Result.ok();
        }
        // 如果不存在，根据商品id取商品信息。
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        // 写入商品数量
        tbItem.setNum(num);
        // 只取一张图片
        if (StringUtils.isNotBlank(tbItem.getImage())) {
            tbItem.setImage(tbItem.getImage().split(",")[0]);
        }
        // 将商品信息添加到购物车
        jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));

        return E3Result.ok();
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.e3mall.cart.service.CartService#getCartList(java.lang.Long)
     * 从redis中获取购物车列表
     */
    @Override
    public List<TbItem> getCartList(Long userId) {
        // 获取hash中所有的值
        List<String> jsonList = jedisClient.hvals(REDIS_CART_PRE + ":" + userId);
        List<TbItem> cartList = new ArrayList<>();
        for (String string : jsonList) {
            TbItem item = JsonUtils.jsonToPojo(string, TbItem.class);
            cartList.add(item);
        }

        return cartList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.e3mall.cart.service.CartService#mageCart(java.lang.Long,
     * java.util.List) 合并cookie中的购物车和服务中的购物车。
     * 
     * 因为和并的逻辑和添加购物车列表的思路一样。所以直接调用添加的方法。
     */
    @Override
    public E3Result mageCart(Long userId, List<TbItem> cartList) {
        // 遍历商品列表。
        // 把列表添加到购物车
        // 判断购物车中是否有此商品。
        // 如果有，数量相加
        // 如果没有，添加新的商品。

        for (TbItem tbItem : cartList) {
            addCart(userId, tbItem.getId(), tbItem.getNum());
        }
        return E3Result.ok();
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.e3mall.cart.service.CartService#updataCartItemNum(java.lang.Long,
     * java.lang.Long, int) 更新商品数量
     */
    @Override
    public E3Result updataCartItemNum(Long userId, Long itemId, int num) {
        String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
        TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
        item.setNum(num);
        jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(item));
        return E3Result.ok();
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.e3mall.cart.service.CartService#deleteCartItem(java.lang.Long,
     * java.lang.Long) 删除服务(redis)中的购物车中商品信息
     * 
     */
    @Override
    public E3Result deleteCartItem(Long userId, Long itemId) {
        jedisClient.hdel(REDIS_CART_PRE + ":" + userId, itemId + "");

        return E3Result.ok();
    }

    /* (non-Javadoc)
     * @see cn.e3mall.cart.service.CartService#clearCartItem(java.lang.Long)
     * 清空购物车
     */
    @Override
    public E3Result clearCartItem(Long userId) {
            jedisClient.del(REDIS_CART_PRE+":"+userId);
        return null;
    }

}
