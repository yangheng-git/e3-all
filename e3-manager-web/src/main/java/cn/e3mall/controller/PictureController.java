package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;
/*
 * 图片上传Controller
 */

@Controller
public class PictureController {
	
	
	
	
	//配置文件，-》 springmvc.xml加载 ，--》 取值
	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;

	//指定相应字符集，将返回json 先转化好，符合kindEditor的标标准， 可以和更多浏览器兼容
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/pic/upload" ,produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8" )
	@ResponseBody
	public String  fileupload(MultipartFile uploadFile){
		/*  1、取文件扩展名
			2、创建一个fastDFS客户端
			3、执行上传处理
			4、拼装返回的url和ip地址。 拼装成完整的url
			5、返回MAP
			
		*/
		
		try {
			
			String originalFilename =  uploadFile.getOriginalFilename();
			System.out.println(originalFilename);
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");

			
			String path = fastDFSClient.uploadFile(uploadFile.getBytes(),extName);
			
			String url = IMAGE_SERVER_URL + path;
			
			Map result =new HashMap<>();
			result.put("error", 0);
			result.put("url", url);
			
			String jsonResult =JsonUtils.objectToJson(result);
			return jsonResult;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Map result =new HashMap<>();
			result.put("error", 1);
			result.put("url", "图片上传失败");
			String jsonResult =JsonUtils.objectToJson(result);
			return jsonResult;
				
		}
		
		
	}
	
	
	
}
