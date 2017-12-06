package cn.e3mall.search;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

public class SearchQueryTest {
	
	@Test
	public void queryIndex() throws Exception {
		//创建一个SolrServer对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.163:8080/solr/collection1");
		//创建一个SolrQuery对象。
		SolrQuery query = new SolrQuery();
		//设置查询条件。
		//query.setQuery("*:*");
		query.set("q", "*:*");
		//执行查询，QueryResponse对象。
		QueryResponse queryResponse = solrServer.query(query);
		//取文档列表。取查询结果的总记录数
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("查询结果总记录数：" + solrDocumentList.getNumFound());
		//遍历文档列表，从取域的内容。
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_category_name"));
		}
	}
	
	public void simpleQuery() throws Exception{
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.148:8080/solr/collection1");
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		QueryResponse  queryResponse= solrServer.query(solrQuery);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		
		System.out.println("查询总记录数为"+solrDocumentList.getNumFound());
		
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_sell_point"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
			System.out.println(solrDocument.get("item_category_name"));
		}
	}

}
