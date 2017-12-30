package cn.e3mall.search.message;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;

/**
 * 商品添加消息接收类。activeMQ 消费者
 * 
 * 收到消息商品id . 睡一会，等待商品添加事务提交，
 * 
 * 然后根据id查询 searchResult 。提交到索引库。
 * 
 * @author yangheng
 *
 */
public class ItemAaddMessageListener implements MessageListener {
        @Autowired
        private ItemMapper itemMapper;
        @Autowired
        private SolrServer solrServer;
    
    @Override
    public void onMessage(Message message) {
        try {
            //接收消息
            TextMessage textMessage = (TextMessage) message;
            //睡1秒钟
            Thread.sleep(1000);
            
            //去数据库查询searchResult
            String text;
            text = textMessage.getText();
            Long itemid = new Long(text);
            SearchItem  searchItem = itemMapper.getSearchItem(itemid);
            
            //上传到索引库。 
            SolrInputDocument document = new SolrInputDocument();
            
            document.setField("id", searchItem.getId());
            document.addField("item_title", searchItem.getTitle());
            document.addField("item_sell_point", searchItem.getSell_point());
            document.addField("item_price", searchItem.getPrice());
            document.addField("item_image", searchItem.getImage());
            document.addField("item_category_name", searchItem.getCategory_name());
            
            solrServer.add(document);
            solrServer.commit();
        } catch (JMSException | SolrServerException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
