package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.service.ItemCatService;

@Controller
public class ItemCatController {
	
	/**
	 * 商品分类管理Controller
	 * <p>Title: ItemCatController</p>
	 * <p>Description: </p>
	 * <p>Company: www.itcast.cn</p> 
	 * @version 1.0
	 */

	@Autowired
	private ItemCatService itemCatService;
	
	@RequestMapping("/item/cat/list")
	@ResponseBody
 	public List<EasyUITreeNode> getItemCatList(@RequestParam(value="id", defaultValue="0")Long parentId){
		return itemCatService.getCatList(parentId);
	}
	
	
}
