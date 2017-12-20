package cn.e3mall.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangheng
 * 搜索结果返回值pojo
 */
public class SearchResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5681099062531575064L;
	/**总记录数*/
	private Long recordCount;
	/**总页数*/
	private int  totalPages;
	/**数据*/
	private  List<SearchItem> itemList;
	
	
	public Long getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(Long recordCount) {
		this.recordCount = recordCount;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public List<SearchItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<SearchItem> itemList) {
		this.itemList = itemList;
	}
	
}
