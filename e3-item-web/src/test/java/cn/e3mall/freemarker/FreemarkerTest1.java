package cn.e3mall.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerTest1 {

    @Test
    public void TestFreemarker() throws Exception {
        // 1、 创建一个模板文件 WEB-INFO下的ftl/hello.ftl
        // 2、 创建一个Configuration对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 3、 设置模板文件保存的目录
        configuration.setDirectoryForTemplateLoading(
                new File("E:/apache-workspaces-Eclipse-e3store/e3-all/e3-item-web/src/main/webapp/WEB-INF/ftl/"));
        // 4、 模板文件的编码格式，一般为utf-8
        configuration.setDefaultEncoding("utf-8");
        // 5、 加载一个模板文件，创建一个模板对象
        Template template = configuration.getTemplate("hello.ftl");
        // 6、 创建一个数据集，可以是pojo也可以是map 推荐使用map
        Map data = new HashMap();
        data.put("hello", "hello freemarker");
        // 7、 创建一个Writer对象，。指定输入的路径和文件名、
        Writer out = new FileWriter(new File("E:/apache-workspaces-Eclipse-e3store/e3-all/freemarker/hello.ftl"));
        // 8、 生成静态页面
        template.process(data,out);
        // 9、 关闭流。
        out.close();
    }
}
