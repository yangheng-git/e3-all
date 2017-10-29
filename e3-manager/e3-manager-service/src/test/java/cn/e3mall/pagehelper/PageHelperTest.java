package cn.e3mall.pagehelper;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;

public class PageHelperTest {
	@SuppressWarnings("resource")
	@Test
	public void TestPageHelper(){
		//初始化spring容器
		//从容器中获取Mapper代理对象
		//执行sql语句之前设置分页信息使用pageHelper的startPage方法
		//执行查询
		//取分页信息，PageInfo 1、总记录数，2、总页数、3、当前页
		ApplicationContext applicationContext =new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		TbItemMapper itemMapper=applicationContext.getBean(TbItemMapper.class);
		PageHelper.startPage(1, 10);
	
		TbItemExample example =new TbItemExample();
		java.util.List<TbItem> list= itemMapper.selectByExample(example);
		PageInfo<TbItem> pageInf =new PageInfo<>(list);
		
		System.out.println(pageInf.getTotal()+"总记录数");
		System.out.println(pageInf.getPages()+"总页数");
	}
}
