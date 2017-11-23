package cn.e3mall.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.service.SearchItemService;
/**
 * 索引库维护Controller 
 * @author yang
 *
 */
@Controller
public class SearchIteemController {

	@Resource
	private SearchItemService searchItemService;
	
	/**
	 * 一键导入搜素信息到Solr索引库
	 * @return
	 */
	@RequestMapping("/index/item/import")
	public E3Result ImportSearchItem(){
		E3Result resout =	searchItemService.importAllItems();
		System.err.print(resout.getStatus());
		
		return resout;
	}
	
}
