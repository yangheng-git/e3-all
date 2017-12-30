package cn.e3mall.search.mapper;

import java.util.List;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

public interface ItemMapper {

    List<SearchItem> getItemList();

    SearchItem getSearchItem(Long itemid);
}
