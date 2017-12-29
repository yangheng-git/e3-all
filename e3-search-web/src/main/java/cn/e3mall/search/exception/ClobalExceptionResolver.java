package cn.e3mall.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


/**全局异常处理器
 * @author yangheng
 *slf4j : 对多个log平台进行封装。 更好管理、 直接用log4j也可以。 
 *
 *需要在容器中注入Bean . 
 *注入bean的方式
 *			1： 在xml文件中<bean>的形式
 *			2：在xml配置注解扫描范围，使其扫描到这个package。然后在ben类上加上@Component注解（普通组件）
 *
 *需要放入log4j.properties文件，在文件中修改日志的级别和打印文件的地址。
 */
@Component
public class ClobalExceptionResolver implements HandlerExceptionResolver{
	
	//获取到logger对象
	private static final Logger logger = LoggerFactory.getLogger(ClobalExceptionResolver.class);
	
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//打印控制台
		ex.printStackTrace();
		//写日志
		logger.debug("测试输出的日志-------debug级别日志-------");
		logger.info("测试输出的日志-------info级别日志-------");
		logger.error("测试输出的日志-------debug级别日志-------");
		//发邮件
		//使用jmail工具包。发短信使用第三方的Webservice .
		//显示错误页面
		ModelAndView model = new ModelAndView();
		model.setViewName("error/exception");
		return model;
	}

}
