package cn.e3mall.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDatagridResult;
import cn.e3mall.common.pojo.Params;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;


/**
 * 商品管理Controller
 * <p>Title: ItemController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */

@Controller
public class ItemController {
	@Autowired
	public ItemService itemService;
					   
	@RequestMapping("/item/{itemID}")
	@ResponseBody
	public TbItem selectByID(@PathVariable Long itemID) {

		return itemService.selectByID(itemID);
	
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDatagridResult getItemList(Integer page ,Integer rows){
	/**
	 * 返回值result中的rows是一个Page类型的对象
	 * Page类型是PageHellp中定义的， 继承了ArrayList
	 * 在展示层没有PageHelper的依赖，但是可以当作List来使用，所以是警告而不是异常
	 * 添加PageHelper的依赖即可解决
	 */
		
		EasyUIDatagridResult result =itemService.getItemList(page,rows);
		return result;
		
	}
	
	/**
	 * 
	 * @param item
	 * @param desc
	 * @return	添加商品
	 */
	@RequestMapping("/item/save")
	@ResponseBody
	public E3Result saveItem(TbItem item ,String desc){
		
		E3Result result = itemService.addItem(item, desc);
		
		return result;
	}
	
	/**
	 * 删除功能
	 */
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	public Params Delete(Params params){
		//实现批量删除 batchDel();
		return itemService.batchDel(params);
	}
	
	/**
	 * 
	 * @param params 参数
	 * 			instock 有存货的
	 * @return 下架
	 */
	
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public Params  instock(Params  params ){
		//第二版 实现批量下架
		return itemService.instock(params);		
	}
	
	/**
	 * 
	 * @param params 参数集
	 * @return
	 	reshelf  重新上架
	 */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public Params reshelf(Params params){
		//第二版 实现批量上架
		//return itemService.reshelf(params);
		
		//使用map集合进行批量操作
		List<String> ids =new ArrayList<>( Arrays.asList( params.getIds().split(",")));
		
		Map map =new HashMap<>();
		map.put("ids", ids);
		map.put("status", 1);
		return itemService.batchReshelfandInstock(map);
		
	
	}
	
	 
	
	
	
	
	
}
