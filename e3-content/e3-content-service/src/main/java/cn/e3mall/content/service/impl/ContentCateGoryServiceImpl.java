package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.content.service.ContentCateGoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容分类管理Service
 * 
 * @author yang
 *
 */
@Service
public class ContentCateGoryServiceImpl implements ContentCateGoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapping;

	@Override
	public List<EasyUITreeNode> getContentCatList(Long parentId) {
		// 根据parentid查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		// 设置查条件
		criteria.andParentIdEqualTo(parentId);

		// 执行查询
		List<TbContentCategory> catList = contentCategoryMapping.selectByExample(example);
		// 转换成Treenode的列表
		List<EasyUITreeNode> nodeList = new ArrayList<>();

		for (TbContentCategory tbContentCategory : catList) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setState(tbContentCategory.getIsParent() ? "closed" : "open");
			node.setText(tbContentCategory.getName());

			nodeList.add(node);
		}
		return nodeList;
	}

}
