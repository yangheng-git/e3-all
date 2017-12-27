package cn.e3mall.ActiveMQ;

import org.junit.Test;

/**测试ActiveMQ
 * @author yangheng
 *
 */
public class ActiveMQTest {

	/**
	 * Queue:点对点
	 * Producer:生产者
	 */
	@Test
	public void TestQueueProducer(){
		//1、创建一个连接工厂对象需要指定服务的ip和端口号
		ConnectionFactory connectionFactory =new ActiveMQConnectionFactory("tcp://192.168.25.148:61616");
		//2、创建Connection对象，
		//3、开启连接
		//4、获取session
	}
}
