package cn.e3mall.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**freemark 生成 html静态页面测试Controller
 * @author yangheng
 *
 */
@Controller
public class HtmlGenController {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    
    @RequestMapping("/genhtml")
    @ResponseBody
    public String GenHtml() throws Exception{
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
         //加载模板对象
        Template template = configuration.getTemplate("hello.ftl");
        //创建一个数据集
        Map data = new HashMap< >();
        data.put("hello", 123456);
        //指定输出的路径和文件名
        Writer out = new FileWriter(new File("E:/apache-workspaces-Eclipse-e3store/e3-all/freemarker/hello.html"));
        //输出文件
        template.process(data, out);
        out.close();
        return "ok";
        
    }
}
