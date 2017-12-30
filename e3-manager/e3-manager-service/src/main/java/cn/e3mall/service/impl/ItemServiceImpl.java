package cn.e3mall.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDatagridResult;
import cn.e3mall.common.pojo.Params;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Service Title: ItemServiceImpl
 * 
 * 
 * 要把搜索详情页添加到缓存中，并设置过期时间
 * 
 * Description:
 * 
 * Company: www.itcast.cn
 * 
 * @version 1.0
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemDescMapper itemdescMapping;
    @Resource
    private JmsTemplate jmsTemplate;
    @Resource
    private Destination topicDestination;

    @Autowired
    private SolrServer solrServer;
    
    @Autowired
    private JedisClient jedisClient;
    @Value("${REDIS_ITEM_PRE}")
    private String REDIS_ITEM_PRE;
    @Value("${ITEM_CACHE_EXPIRE}")
    private Integer ITEM_CACHE_EXPIRE;

    /**
     * 添加缓存。 先从缓存中查找数据，没有数据再去数据库查询， 并将查询的数据放入缓存
     *
     * 需要给放入缓存的数据设置过期时间。一小时后过期 ，避免浪费内存。
     *
     * 需要给修改
     *
     * @param itemID
     * @return 根据id查询商品
     */
    @Override
    public TbItem selectByID(Long itemID) {
        // 查询缓存
        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemID + ":BASE");
            if (StringUtils.isNoneBlank(json)) {
                TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
                return tbItem;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 没有查到缓存数据， 从数据库中取出数据，并发送到缓存，。
        TbItem item = itemMapper.selectByPrimaryKey(itemID);
        try {
            jedisClient.set(REDIS_ITEM_PRE + ":" + itemID + ":BASE", JsonUtils.objectToJson(item));
            // 设置过期时间
            jedisClient.expire(REDIS_ITEM_PRE + ":" + itemID + ":BASE", ITEM_CACHE_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;

        /**
         * 较为复杂的方法
         * 
         * TbItemExample example = new TbItemExample(); Criteria criteria
         * =example.createCriteria(); //设置查询条件 criteria.andIdEqualTo(itemId);
         * //执行查询 List<TbItem> list = itemMapper.selectByExample(example);
         * if(list != null && list.size() > 0) { return list.get(0); } return
         * null;
         * 
         */
    }

    /**
     * 
     * @param page
     *            当前页
     * @param rows
     *            每页显示行数
     * @return 带分页的list
     * 
     */
    @Override
    public EasyUIDatagridResult getItemList(int page, int rows) {
        PageHelper.startPage(page, rows);

        TbItemExample example = new TbItemExample();
        List<TbItem> list = itemMapper.selectByExample(example);
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);

        EasyUIDatagridResult result = new EasyUIDatagridResult();
        result.setTotal(pageInfo.getTotal());// 每页多少条数据
        result.setRows(list);
        return result;
    }

    /**
     * 添加商品 。需要往 两个表中添加数据 item itemdesc
     */

    @Override
    public E3Result addItem(TbItem item, String desc) {
        // 生成商品id
        final long itemID = IDUtils.genItemId();
        // 补全item的属性
        item.setId(itemID);
        // 1-正常，2-下架，3-删除
        item.setStatus((byte) 1);

        // 生成时间，修改时间
        item.setCreated(new Date());
        item.setUpdated(new Date());

        // 向表单插入数据
        itemMapper.insert(item);

        // 创建商品描述表的pojo对象
        TbItemDesc itemDesc = new TbItemDesc();

        // 补全属性
        itemDesc.setItemId(itemID);
        itemDesc.setItemDesc(desc);
        // 生成时间，修改时间
        item.setCreated(new Date());
        item.setUpdated(new Date());
        // 向商品描述表插入数据
        itemdescMapping.insert(itemDesc);

        // 向activeMQ发送消息
        jmsTemplate.send(topicDestination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {

                TextMessage textMessage = session.createTextMessage(itemID + "");
                return textMessage;
            }
        });

        return E3Result.ok();
    }

    /**
     * 下架商品
     * 
     * 将下架的商品从缓存中删除。 从索引库中删除。 
     */

    @Override
    public Params instock(Params params) {
        /*
         * long id = Integer.parseInt(params.getIds());
         * 
         * TbItem item =new TbItem(); item.setId(id); //1-正常，2-下架，3-删除
         * item.setStatus((byte)2); item.setUpdated(new Date()); int result =
         * itemMapper.updateByPrimaryKeySelective(item);
         * 
         * Params par = new Params(); if (result>0) { par.setStatus("200"); }
         * 
         * return par;
         */
        // 第二版 实现批量下架
        String[] ids = params.getIds().split(",");
        
        //不论下架是否成功。都删除。
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            //从缓存中删除
            jedisClient.del(REDIS_ITEM_PRE+":"+id+":BASE");
            
            //从索引库中删除。 
            try {
                solrServer.deleteByQuery("id:"+id);
            } catch (SolrServerException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
        }
        
        
        int result = itemMapper.batchInstock(ids);
        Params par = new Params();

        par.setStatus(result > 0 ? "200" : null);
        return par;

    }

    @Override
    public Params reshelf(Params params) {
        /*
         * long id = Integer.parseInt(params.getIds()); TbItem item =new
         * TbItem(); item.setId(id); //1-正常，2-下架，3-删除 item.setStatus((byte)1);
         * item.setUpdated(new Date());
         * 
         * int result = itemMapper.updateByPrimaryKeySelective(item);
         * 
         * Params par = new Params(); if (result>0) { par.setStatus("200"); }
         * 
         * return par;
         */

        // 第二版，实现批量上架

        /*
         * split()将字符串转换成数组， Arrays.asList()将数组转换成list集合。然后将这个集合放到一个新的list集合中，
         * aslist 转换的list集合不能对集合增删。 但放到一个新的list中就可以了
         */
        List<String> ids = new ArrayList<>(Arrays.asList(params.getIds().split(",")));
        int result = itemMapper.batchReshelf(ids);

        Params par = new Params();
        par.setStatus(result > 0 ? "200" : null);
        return par;

    }

    /**
     * 自定义上架下架 status 1-正常，2-下架，3-删除
     */
    @Override
    public Params batchReshelfandInstock(Map map) {
        int result = itemMapper.batchReshelfandInstock(map);
        Params par = new Params();

        par.setStatus(result > 0 ? "200" : null);
        return par;
    }

    @Override
    public Params batchDel(Params params) {
        String[] ids = params.getIds().split(",");
        Params par = new Params();
        par.setStatus(itemMapper.batchDel(ids) > 0 ? "200" : null);
        return par;
    }

    @Override
    public TbItemDesc selectItemDespById(Long itemId) {
        // 查询缓存
        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
            if (StringUtils.isNotBlank(json)) {
                TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                return tbItemDesc;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 查询不到缓存。从疏浚库中取，并放到缓存中
        TbItemDesc itemDesc = itemdescMapping.selectByPrimaryKey(itemId);
        try {
            String json = JsonUtils.objectToJson(itemDesc);
            jedisClient.set(REDIS_ITEM_PRE+":"+itemId+":DESC", json);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemDesc;
    }

}
