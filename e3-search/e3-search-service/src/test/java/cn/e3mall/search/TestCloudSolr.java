package cn.e3mall.search;

import javax.lang.model.SourceVersion;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.aspectj.lang.annotation.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class TestCloudSolr {

	
	/**
	 * 创建一个集群的连接，应该使用CloudSolrServer创建
	 * 			参数 zkhost : zookeeper的地址列表
	 * 设置一个默认Collection属性
	 * 创建一个文档对象
	 * 向文档中添加域
	 * 将文件写入索引库
	 * 提交
	 * @throws Exception 
	 * @throws SolrServerException 
	 */
	@Test
	public void testAddDocumentSolr() throws SolrServerException, Exception{
		String zkhost="192.168.25.148:2181,192.168.25.148:2182,192.168.25.148:2183";
		CloudSolrServer solrServer = new CloudSolrServer(zkhost);
		
		solrServer.setDefaultCollection("collection2");
		
		SolrInputDocument solrDocument = new SolrInputDocument();
		solrDocument.setField("id", "solrCloud01");
		solrDocument.setField("item_title","测试商品01");
		solrDocument.setField("item_price", 123);
		
		solrServer.add(solrDocument);
		solrServer.commit();
		
	}
	
	
	
	/**
	 * 创建一个CloudSolrServer对象。 
	 * 设置默认的Collection
	 * 创建一个查询对象
	 * 设置查询条件， 
	 * 执行查询
	 * 取查询结果
	 * 
	 * @throws Exception
	 */
	@Test
	public void testQueryCloudSolr() throws Exception{
		String zkhost="192.168.25.148:2181,192.168.25.148:2182,192.168.25.148:2183";
		CloudSolrServer solrServer = new CloudSolrServer(zkhost);
		
		solrServer.setDefaultCollection("collection2");
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrDocumentList results = queryResponse.getResults();
		
		System.out.println("总记录数：-------"+results.getNumFound());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_image"));

		}
	}
	/**加载spring容器*/
	@org.junit.After
	public void After(){
		String xmlfile = "classpath:spring/applicationContext-*.xml";
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlfile);
	}
	
	@Autowired
	private SolrServer solrServer;
	@Test
	public void testBeanSolrCloud() throws Exception{
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("总记录数----"+solrDocumentList.getNumFound());
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_image"));
		}
		
	}
	
}
