package cn.e3mall.service.impl;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.EasyUIDatagridResult;
import cn.e3mall.common.pojo.Params;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Service Title: ItemServiceImpl
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

    /**
     * 
     * @param itemID
     * @return 根据id查询商品
     */
    @Override
    public TbItem selectByID(Long itemID) {
        return itemMapper.selectByPrimaryKey(itemID);

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
        TbItemDesc itemDesc = itemdescMapping.selectByPrimaryKey(itemId);
        return itemDesc;
    }

}
