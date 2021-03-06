package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;

public interface ContentCateGoryService {

	List<EasyUITreeNode> getContentCatList(Long parentId);

	E3Result addContentCategor(Long parentId, String name);

	E3Result deleteeContentCatgory(Long id);
	
	E3Result updateContentCatgory(Long id ,String name);

}
