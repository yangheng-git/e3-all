package cn.e3mall.search;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

public class SearchQueryTest {
	/** 案例 */
	@Test
	public void queryIndex() throws Exception {
		// 创建一个SolrServer对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.163:8080/solr/collection1");
		// 创建一个SolrQuery对象。
		SolrQuery query = new SolrQuery();
		// 设置查询条件。
		// query.setQuery("*:*");
		query.set("q", "*:*");
		// 执行查询，QueryResponse对象。
		QueryResponse queryResponse = solrServer.query(query);
		// 取文档列表。取查询结果的总记录数
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("查询结果总记录数：" + solrDocumentList.getNumFound());
		// 遍历文档列表，从取域的内容。
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_category_name"));
		}
	}

	/**
	 * 复写案例 
	 * @1、创建solrServer对象。
	 * @2、创建solrQuery对象。 
	 * @3、设置查询条件。 
	 * @4、执行查询。 获得QueryResponse对象。 
	 * @5、获取文档列表 QueryDocumentList。获取查询结果的总记录数。solrDocumentList.getNumFound()。 
	 * @6、遍历文档列表。 从域中取内容。
	 */
	@Test
	public void simpleQuery() throws Exception {

		SolrServer solrServer = new HttpSolrServer("http://192.168.25.148:8080/solr/collection1");

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrDocumentList solrDocumentList = queryResponse.getResults();

		System.out.println("查询总记录数为" + solrDocumentList.getNumFound());

		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_category_name"));
		}
	}

	/**
	 * 复杂搜索 complexSearch
	 * 
	 * @throws SolrServerException
	 * @author yangheng 案例
	 */

	@Test
	public void complexSearch() throws SolrServerException {
		// 创建一个SolrServer对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.148:8080/solr/collection1");
		// 创建一个SolrQuery对象
		SolrQuery solrQuery = new SolrQuery();
		// 设置查询条件。
		solrQuery.setQuery("手机");
		solrQuery.setStart(0);// 开始查询的起点（从第几条开始记录）
		solrQuery.setRows(20);// 查询几条记录。
		solrQuery.set("df", "item_title");
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em>");
		solrQuery.setHighlightSimplePost("</em>");
		// 执行查询
		QueryResponse queryResponse = solrServer.query(solrQuery);
		// 取文档列表，取查询的总记录
		SolrDocumentList queryDocunmentList = queryResponse.getResults();
		System.out.println("查询总记录数是--------：" + queryDocunmentList.getNumFound());
		// 遍历文档列表。取域的内容。
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		for (SolrDocument solrDocument : queryDocunmentList) {
			System.out.println(solrDocument.get("id"));
			// 取高亮显示
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if (list != null && list.size() > 0) {
				title = list.get(0);
			} else {
				title = (String) solrDocument.get("item_title");
			}
			System.out.println(title);
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_catgory_name"));

		}
	}

	/**
	 * 复写案例 
	 * @throws Exception 
	 * @1、创建一个SolrServer对象。 
	 * @2、创建一个SolrQuery对象。
	 * @3、设置查询条件。
	 * @4、执行查询。
	 * @5、遍历文档列表，取查询的总记录。
	 * @6、取高亮显示。
	 */
	public void complexSearch1() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.148:8080/solr/collection1");
		SolrQuery query = new SolrQuery();
		query.set("手机");
		query.setStart(0);
		query.setRows(20);
		query.set("df", "item_title");
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		QueryResponse queryResponse = solrServer.query(query);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("查询总记录数是--------------"+ solrDocumentList.getNumFound());
		
	}

}
