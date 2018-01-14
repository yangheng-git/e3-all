package cn.e3mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

/**
 * 订单管理service
 * 
 * @author yangheng
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private JedisClient jedisClient;
    @Autowired 
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;
    
    //订单id的key
    @Value("${ORDER_ID_GEN_KEY}")
    private String ORDER_ID_GEN_KEY;
    //订单id取值的开始字段
    @Value("${ORDER_ID_START}")
    private String ORDER_ID_START;
    //订单明细 id的key
    @Value("${ORDER_DETAIL_ID_GEN_KEY}")
    private String ORDER_DETAIL_ID_GEN_KEY;

    /*
     * (non-Javadoc)
     * 
     * @see
     * cn.e3mall.order.service.OrderService#createOrder(cn.e3mall.order.pojo.
     * OrderInfo) 生成订单。
     */
    @Override
    public E3Result createOrder(OrderInfo orderInfo) {
        // 生成订单号，使用redis的inct生成。 订单号唯一并且是数字。并且不能过长。
        // 首先判断redis服务器中有没有这个key
        if (jedisClient.exists(ORDER_ID_GEN_KEY)) {
            jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_START);
        }
        //订单id.
        String orderId= jedisClient.incr(ORDER_ID_GEN_KEY).toString();
        //补全orderd的属性1
        orderInfo.setOrderId(orderId);
        //1、未付款。 2、已付款。3、未发货。4、已发货。5、交易成功。6、交易关闭
        orderInfo.setStatus(1);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        
        //插入订单表
        orderMapper.insert(orderInfo);
        
        //向订单明细表中插入数据
        List<TbOrderItem> orderItem = orderInfo.getOrderItems();
        for (TbOrderItem tbOrderItem : orderItem) {
            //生成明细id
            String odId = jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
            //补全pojo属性
            tbOrderItem.setId(odId);
            tbOrderItem.setOrderId(orderId);
            //向明细表中插入数据
            orderItemMapper.insert(tbOrderItem);
        }
        //向订单物流表中插入数据
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        orderShipping.setOrderId(orderId);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());
        orderShippingMapper.insert(orderShipping);
        //返回e3Result. 包含订单号。
        return E3Result.ok(orderId);
    }

}
