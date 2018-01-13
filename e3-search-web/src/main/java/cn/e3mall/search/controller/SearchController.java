package cn.e3mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;

/**
 * @author yangheng 商品搜索Controller
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Value("${SEARCH_RESULT_ROWS}")
    private Integer rows;

    @RequestMapping("/search")
    public String search(String keyword, @RequestParam(defaultValue = "1") Integer page, Model model) throws Exception {
        keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");
        // 查询商品列表
        SearchResult searchResult = searchService.search(keyword, page, rows);

        // 把结果返回页面
        model.addAttribute("query", keyword);
        model.addAttribute("totalPages", searchResult.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("recourdCound", searchResult.getRecordCount());
        model.addAttribute("itemList", searchResult.getItemList());

        // int a = 1/0;
        // 返回逻辑视图
        return "search";

    }
}
