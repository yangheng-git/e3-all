package cn.e3mall.item.listener;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 监听商品添加信息。 生成静态页面
 * 
 * @author yangheng
 *
 */
public class HtmlGenListener implements MessageListener {

    @Autowired
    private ItemService itemService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${HTML_GEN_PATH}")
    private String HTML_GEN_PATH;

    @Override
    public void onMessage(Message message) {
        try {
            // 创建一个模板。 参考jsp
            // 接收消息，从消息中取id.
            TextMessage textMessage = (TextMessage) message;
            String text;
            text = textMessage.getText();
            Long itemId = new Long(text);
            // 等待添加商品的事务提交
            Thread.sleep(100);
            // 根据id查询商品信息。 商品基本信息和商品描述。
            TbItem tbItem = itemService.selectByID(itemId);
            Item item = new Item(tbItem);
            // 查询商品描述信息
            TbItemDesc itemDesc = itemService.selectItemDespById(itemId);
            // 创建一个数据集。把商品封装起来。
            Map data = new HashMap<>();
            data.put("item", item);
            data.put("itemDesc", itemDesc);
            // 加载模板对象。
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            // 创建一个输出流。 指定输出的目录和文件名。
            Writer out = new FileWriter(HTML_GEN_PATH + itemId + ".html");
            // 生成静态页面
            template.process(data, out);
            // 关闭流
            out.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
