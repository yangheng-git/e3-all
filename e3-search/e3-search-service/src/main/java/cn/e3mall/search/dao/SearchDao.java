package cn.e3mall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

/**
 * 商品搜索Dao . 搜索索引库
 * 
 * @author yangheng
 *
 */
@Repository
public class SearchDao {
    @Autowired
    private SolrServer solrServer;

    /**
     * 根据查询条件查询索引库
     * 
     * @throws Exception
     * 
     */
    public SearchResult search(SolrQuery query) throws Exception {

        /*
         * 根据query查询索引库。 取查询结果 取查询总记录数 取商品列表，取高亮 返回结果。
         */
        QueryResponse queryResponse = solrServer.query(query);
        SolrDocumentList solrDocumentList = queryResponse.getResults();

        SearchResult searchResult = new SearchResult();

        long numFound = solrDocumentList.getNumFound();
        searchResult.setRecordCount(numFound);

        // 商品列表集合 。需要取高亮显示
        List<SearchItem> itemList = new ArrayList<>();
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
        for (SolrDocument solrDocument : solrDocumentList) {
            SearchItem searchItem = new SearchItem();

            searchItem.setId((String) solrDocument.get("id"));
            searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
            searchItem.setPrice((long) solrDocument.get("item_price"));
            searchItem.setImage((String) solrDocument.get("item_image"));
            searchItem.setCategory_name((String) solrDocument.get("item_category_name"));
            // 取高亮
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String title = "";
            if (list != null && list.size() > 0) {
                title = list.get(0);
            } else {
                title = (String) solrDocument.get("item_title");
            }
            searchItem.setId(title);

            itemList.add(searchItem);
        }

        searchResult.setItemList(itemList);
        return searchResult;
    }
}
