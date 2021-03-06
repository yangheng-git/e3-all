package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.jmx.LoadBalanceConnectionGroupManager;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDatagridResult;
import cn.e3mall.common.pojo.Params;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;
/**
 * 内容管理 service 
 * @author yang
 *
 *在增删改操作中加入缓存同步 ， 更改的数据从缓存中删除，让其在查询时从数据库中取值。
 *放在handler的最前面。保证每进行一次操作就更新一次缓存，不考虑操作成功与否
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	TbContentMapper  contentMapper;
	
	@Autowired
	JedisClient jedisClient;
	
	@Value("${CONTENT_LIST}")
	private String  CONTENT_LIST;
	
	
	@Override
	public E3Result addContent(TbContent content) {
		
		
		
		try {//删除缓存中该数据的缓存。
		
			jedisClient.hdel(CONTENT_LIST, content.getCategoryId()+"");	
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("同步缓存失败");
		}	
		
		content.setCreated(new Date());
		content.setUpdated(new Date());
		int insert = contentMapper.insert(content);

		return E3Result.ok();
	}

	@Override
	public EasyUIDatagridResult gitList(int page, int rows ) {
		PageHelper.startPage(page, rows);	
		
		TbContentExample  example  = new TbContentExample();
		Criteria criteria = example.createCriteria();
		List<TbContent>  list = contentMapper.selectByExampleWithBLOBs(example);
		
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		EasyUIDatagridResult result = new EasyUIDatagridResult();
		result.setTotal(pageInfo.getTotal());// 每页多少条数据
		result.setRows(list);
		return result;
	}

	@Override
	public Params deleteContent(Params  params) {
		String[] strings = params.getIds().split(",");
		
		try {//删除缓存中该数据的缓存。
			//根据id取cid 
			for (int i = 0; i < strings.length; i++) {
				Long   id = (long) Integer.valueOf(strings[i]);
				Long cid = contentMapper.selectByPrimaryKey(id).getCategoryId();
				
				jedisClient.hdel(CONTENT_LIST,cid+"");
			}	
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("同步缓存失败");
		}
	
		
		int i =contentMapper.batchDel(strings);
		Params par = new Params();
		par.setStatus(i>0? "200":null);
		return par;
	}

	@Override
	public E3Result edit(TbContent content) {
		try {//删除缓存中该数据的缓存。
			
			jedisClient.hdel(CONTENT_LIST, content.getCategoryId()+"");	
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("同步缓存失败");
		}
	
		 contentMapper.updateByPrimaryKeySelective(content);
		
		return E3Result.ok();
	}
	
	
	/**
	 * 根据内容分类id获取内容
	 *  parent-web调用的方法，用来获取某个菜单下的内容
	 * WithBLOBS 要不要加上大文本内容
	 * @return
	 */
	public List<TbContent> getTbContentByCid(Long cid ){
		//从缓存中取值，有的话直接返回，没有再从数据库中取值，并放到缓存中。
		//redis中只有字符串，所以需要转换成字符串。
		try {
			 String json=	jedisClient.hget(CONTENT_LIST, cid+"");
			 if(StringUtils.isNotBlank(json)){
				 List<TbContent>  list = JsonUtils.jsonToList(json, TbContent.class );
				 return list;
			 }
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("查询缓存失败");
		}
		
		
		TbContentExample example = new TbContentExample();
		Criteria  criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		
		try {
			//如果缓存中没有数据，将数据库中取出的数据放入缓存
			jedisClient.hset(CONTENT_LIST, cid+"", JsonUtils.objectToJson(list));	
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("添加 缓存失败");
		}
		return list;
	}

}
