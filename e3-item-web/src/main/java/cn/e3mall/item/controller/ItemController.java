package cn.e3mall.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;

/**
 * 商品详情展示Controller
 * 
 * @author yangheng
 *
 */
@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    public String  showItemInfo(@PathVariable Long itemId, Model model) {
        // 获取商品详情信息
        TbItem tbItem = itemService.selectByID(itemId);
        Item item = new Item(tbItem);
        
        //获取商品描述信息
        TbItemDesc itemDesc = itemService.selectItemDespById(itemId);
        
        //返回逻辑视图
        model.addAttribute("item",item);
        model.addAttribute("itemDesc",itemDesc);
        
        return "item";
    }
}
