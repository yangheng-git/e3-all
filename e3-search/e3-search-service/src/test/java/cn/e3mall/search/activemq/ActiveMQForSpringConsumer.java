package cn.e3mall.search.activemq;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**activeMQ 消费者测试。 整合spring 
 * @author yangheng
 *
 */
public class ActiveMQForSpringConsumer {

	
	/**
	 * 初始化容器
	 * 等待
	 * @throws IOException 
	 */
	@Test
	public void  activeMQConsumer() throws IOException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		System.in.read();		
	}
}
