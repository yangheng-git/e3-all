package cn.e3mall.service;

import java.util.Map;

import cn.e3mall.common.pojo.EasyUIDatagridResult;
import cn.e3mall.common.pojo.Params;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface ItemService {
	
	
	/**
	 * 根据id取得商品
	 * @param itemID
	 * @return
	 */
	public TbItem selectByID(Long itemID);
	
	/**
	 * 分页展示商品
	 * @param page
	 * @param rows
	 * @return
	 */
	public EasyUIDatagridResult getItemList(int page ,int rows);
	
	/**
	 * 新增商品
	 * @param item
	 * @param desc
	 * @return
	 */
	public E3Result addItem(TbItem item ,String desc);
	
	/**
	 * 上架，下架
	 * @param params
	 * @return
	 */
	public Params instock(Params params);
	public Params reshelf(Params params);
	
	
	/**
	 * 自定义上架下架
	 * status 1-正常，2-下架，3-删除
	 */
	@SuppressWarnings("rawtypes")
	public Params  batchReshelfandInstock(Map map);
}
