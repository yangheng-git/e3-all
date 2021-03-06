package cn.e3mall.content.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
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

	/**
	 * addContentCategor 新增内容分类菜单子菜单。
	 */
	@Override
	public E3Result addContentCategor(Long parentId, String name) {
		// 创建一个tb_content_category表对应的pojo对像并赋值
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setName(name);
		// 1(正常),2(删除)
		contentCategory.setStatus(1);
		contentCategory.setCreated(new Date());
		// 新添加的节点一定是叶子节点
		contentCategory.setIsParent(false);
		// 默认排序就是1
		contentCategory.setSortOrder(1);
		contentCategory.setUpdated(new Date());
		contentCategory.setParentId(parentId);
		E3Result e3Result =null;
		int i = contentCategoryMapping.insert(contentCategory);
		if (i > 0) {
			// 判读父节点的isParaent属性，如果不是true,改为true
			TbContentCategory categoryParent = contentCategoryMapping.selectByPrimaryKey(parentId);
			// 如果 取出的值为false就取反并进行修改。
			if (!categoryParent.getIsParent()) {
				categoryParent.setIsParent(true);
				contentCategoryMapping.updateByPrimaryKeySelective(categoryParent);
			}

			// 因为在mapping中设置的返回主键，所以在插入成功后会将自动生成的id返回的TbContentCateGory中。
			e3Result	 = E3Result.ok(categoryParent);

		}

		return e3Result;
	}

	/**
	 * 删除 节点 如果该节点是叶子节点或者是只包含叶子节点的父节点，就允许删除。 如果该节点中包含父节点就不允许删除
	 */
	@Override
	public E3Result deleteeContentCatgory(Long id) {
		// 本节点
		TbContentCategory IDnode = contentCategoryMapping.selectByPrimaryKey(id);
		int isdelete = 0 ;
		
		// 判断是不是叶子节点，是的话直接删除
		if (!IDnode.getIsParent()) {
			contentCategoryMapping.deleteByPrimaryKey(id);
		} else {
			// 判断其内包含的节点有没有父节点
			TbContentCategoryExample ISparentExample = new TbContentCategoryExample();
			Criteria ISparentcriteria = ISparentExample.createCriteria();
			ISparentcriteria.andParentIdEqualTo(id);
			List<TbContentCategory> categories = contentCategoryMapping.selectByExample(ISparentExample);
			
			
			List<Long> nodeIDs = new ArrayList<>();
			for (TbContentCategory tbContentCategory : categories) {
				nodeIDs.add(tbContentCategory.getId());
				if (tbContentCategory.getIsParent()) {
					// 是父节点就停止，不让删除。并清空nodeIDs中数据
					for (int i = 0; i < nodeIDs.size(); i++) {
						nodeIDs.remove(i);
					}
					break;
				}
				
			}
			if (!nodeIDs.isEmpty()) {
				//如果这个节点中不包含父节点。
				// 该节点中都是叶子节点，批量删除，然后删除该节点，如果该节点的父节点只有这一个子节点，就将父节点的isParent 置为 false
				int i = contentCategoryMapping.batchDelete(nodeIDs);
				contentCategoryMapping.deleteByPrimaryKey(id);
				
			}

			
		}
		// 如果父节点下没有其他节点了，就将父节点的isparent置为false

		// 1 获取该节点的父节点
		TbContentCategory ParrntNode = contentCategoryMapping.selectByPrimaryKey(IDnode.getParentId());
		TbContentCategoryExample Example = new TbContentCategoryExample();
		Criteria criteria = Example.createCriteria();
		criteria.andParentIdEqualTo(ParrntNode.getId());
		List<TbContentCategory> TreeNodes = contentCategoryMapping.selectByExample(Example);
		
		
		if (TreeNodes.isEmpty()) {
			ParrntNode.setIsParent(false);

			contentCategoryMapping.updateByPrimaryKeySelective(ParrntNode);
		}

		return E3Result.ok();
	}

	
	
	/**
	 * 重命名
	 */
	@Override
	public E3Result updateContentCatgory(Long id, String name) {
		TbContentCategory category = new TbContentCategory();
		category.setId(id);
		category.setName(name);
		category.setUpdated(new Date());
		int i = contentCategoryMapping.updateByPrimaryKeySelective(category);

		return E3Result.ok();
	}

}
