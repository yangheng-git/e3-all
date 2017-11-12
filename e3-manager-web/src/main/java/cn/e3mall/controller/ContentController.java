package cn.e3mall.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDatagridResult;
import cn.e3mall.common.pojo.Params;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

/**
 * 内容controller
 * 
 * @author yang
 *
 */
@Controller
public class ContentController {

	@Resource
	ContentService contentService;
	
	@RequestMapping("/content/save")
	@ResponseBody
	public E3Result addContent(TbContent content){
		return contentService.addContent(content);
	}
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public  EasyUIDatagridResult gitList(Integer page ,Integer rows ){
		
		EasyUIDatagridResult result = contentService.gitList(page, rows);
		return result;
	}
	
	@RequestMapping("/content/delete")
	@ResponseBody
	public Params deleteContent(Params  params){
		return contentService.deleteContent(params);
	}
	
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public E3Result edit(TbContent content){
			
		return contentService.edit(content);
	}
	
}
