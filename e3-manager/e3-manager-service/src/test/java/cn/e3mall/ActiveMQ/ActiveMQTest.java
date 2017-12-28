package cn.e3mall.ActiveMQ;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 * 测试ActiveMQ
 * 
 * @author yangheng
 *
 */

public class ActiveMQTest {

	/**
	 * Queue:点对点 Producer:生产者
	 * 
	 * @throws Exception
	 */
	@Test
	public void TestQueueProducer() throws Exception {
		// 1、创建一个连接工厂对象需要指定服务的ip和端口号
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 2、创建Connection对象，
		Connection connection = connectionFactory.createConnection();
		// 3、开启连接,调用Connection对象的start方法。
		connection.start();
		// 4、创建一个session
		// 第一个参数：是否开启事务。如果true开启事务，第二个参数无意义。一般不开启事务false。
		// 第二个参数：应答模式。自动应答或者手动应答。一般自动应答。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5、使用Session对象创建一个Destination对象。两种形式queue、topic，现在应该使用queue
		Queue queue = session.createQueue("test-Queue");
		// 6、 使用Sesssion创建一个producer对象，
		MessageProducer producer = session.createProducer(queue);
		// 7 、创建一个Message对象，可以使用TextMessage
		/*
		 * ActiveMQTextMessage message = new ActiveMQTextMessage();
		 * message.setText("创建message的方式1");
		 */
		for (int i = 0; i <= 10; i++) {

			TextMessage textMessage = session.createTextMessage("创建message的方式2");
			// 8、 发送消息
			producer.send(textMessage);
			// 9、 关闭资源
		}
		producer.close();
		session.close();
		connection.close();
	}

	/**
	 * Queue:点对点 Consumer:消费者
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void TestQueueConsumer() throws Exception {
		// 1、创建一个连接工厂对象连接MQ服务器
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 2、创建Connection对象，
		Connection connection = connectionFactory.createConnection();
		// 3、开启连接,调用Connection对象的start方法。
		connection.start();
		// 4、使用connection创建一个session 不添加事物，自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5、 创建一个Destination对象。queue
		Queue queur = session.createQueue("test-Queue");
		// 6、 使用session创建一个消费者对象。
		MessageConsumer consumer = session.createConsumer(queur);
		// 7、 接收消息

		consumer.setMessageListener(new MessageListener() {
			// 监听器，匿名内部类。 onMessage:触发器。
			@Override
			public void onMessage(Message message) {
				// 打印结果
				TextMessage textMessage = (TextMessage) message;
				String text;
				try {
					text = textMessage.getText();
					System.out.println("接受者1-----" + text);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		/*
		 * 生产者发送10条消息，会被两个接受者的分享。 不会每人一份。 被取走就没了
		 * 
		 * 
		 * //接收者2 MessageConsumer consumer2 = session.createConsumer(queur);
		 * consumer2.setMessageListener(new MessageListener() { //监听器，匿名内部类。
		 * onMessage:触发器。
		 * 
		 * @Override public void onMessage(Message message) { //打印结果 TextMessage
		 * textMessage = (TextMessage) message; String text; try {
		 * text=textMessage.getText(); System.out.println("接受者2-----"+text); }
		 * catch (JMSException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * } });
		 */

		// 等待接收消息。
		System.in.read();

		// 8、关闭资源
		consumer.close();
		session.close();
		connection.close();

	}

	/**
	 * Tipic:订阅发布 producer：生产者
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void TestTopicProducer() throws Exception {
		// 1、创建一个连接工厂对象，需要指定服务的ip及端口。
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 2、使用工厂对象创建一个Connection对象。
		Connection connection = connectionFactory.createConnection();
		// 3、开启连接，调用Connection对象的start方法。
		connection.start();
		// 4、创建一个Session对象。
		// 第一个参数：是否开启事务。如果true开启事务，第二个参数无意义。一般不开启事务false。
		// 第二个参数：应答模式。自动应答或者手动应答。一般自动应答。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5、使用Session对象创建一个Destination对象。两种形式queue、topic，现在应该使用topic
		Topic topic = session.createTopic("test-topic");
		// 6、使用Session对象创建一个Producer对象。
		MessageProducer producer = session.createProducer(topic);
		// 7、创建一个Message对象，可以使用TextMessage。
		/*
		 * TextMessage textMessage = new ActiveMQTextMessage();
		 * textMessage.setText("hello Activemq");
		 */
		TextMessage textMessage = session.createTextMessage("topic message");
		// 8、发送消息
		producer.send(textMessage);
		// 9、关闭资源
		producer.close();
		session.close();
		connection.close();

	}

	/**
	 * Topic:订阅发布 Consumer:消费者
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void TestTopicConsumer() throws Exception {

		// 创建一个ConnectionFactory对象连接MQ服务器
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		// 创建一个连接对象
		Connection connection = connectionFactory.createConnection();
		// 开启连接
		connection.start();
		// 使用Connection对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 创建一个Destination对象。topic对象
		Topic topic = session.createTopic("test-topic");
		// 使用session创建一个消费者对象
		MessageConsumer consumer = session.createConsumer(topic);
		// 接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				// 打印结果
				TextMessage textMessage = (TextMessage) message;
				String text;
				try {
					text = textMessage.getText();
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}

			}

		});
		System.out.println("topic消费者2启动。。。。");
		// 等待接收消息
		System.in.read();
		// 关闭资源
		consumer.close();
		session.close();
		connection.close();
	}

}
