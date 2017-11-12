package cn.e3mall.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCateGoryService;
/**
 * 内容分类管理Controller
 * @author yang
 *
 */
@Controller
public class ContentCatController {

	@Autowired 
	private ContentCateGoryService contentCateGoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCateGoryTreeNode(@RequestParam(name="id",defaultValue="0") Long parentId){		
		return contentCateGoryService.getContentCatList(parentId);
	}
	
	
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3Result createContentCategory(Long parentId , String name){
		return contentCateGoryService.addContentCategor(parentId, name);
	}
	
	@RequestMapping("/content/category/delete/")
	public E3Result deleteContentCategory(Long id  ,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
			contentCateGoryService.deleteeContentCatgory(id);
			request.getRequestDispatcher("/content/category/list").forward(request, response);
		
		return  null;
	}
	
	
	@RequestMapping("/content/category/update")
	public E3Result updateContentCatgory(Long id , String name){
		
		return contentCateGoryService.updateContentCatgory(id, name);
	}
	
}
