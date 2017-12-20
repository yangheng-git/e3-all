package cn.e3mall.search;

import org.apache.solr.client.solrj.SolrQuery;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

public class SearchDaoTest {
	@org.junit.Before
	public void before(){
	
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
	}

	@Autowired
	private SearchDao searchDao;

	@Test
	public void testSearchDao() throws Exception {
		SolrQuery query = new SolrQuery();
		query.set("手机");
		query.setStart(0);
		query.setRows(20);
		// 设置默认搜索域
		query.set("df", "item_title");
		// 开启高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		SearchResult search = searchDao.search(query);
		System.out.println(search.getRecordCount());
		for (SearchItem list : search.getItemList()) {
			System.out.println(list);
		}

	}
}
