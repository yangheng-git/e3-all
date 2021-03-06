package cn.e3mall.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;

public interface TbItemMapper {
    int countByExample(TbItemExample example);

    int deleteByExample(TbItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbItem record);

    int insertSelective(TbItem record);

    List<TbItem> selectByExample(TbItemExample example);

    TbItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbItem record, @Param("example") TbItemExample example);

    int updateByExample(@Param("record") TbItem record, @Param("example") TbItemExample example);

    int updateByPrimaryKeySelective(TbItem record);

    int updateByPrimaryKey(TbItem record);
    
    /*
     * 批量下架
     */
    int  batchInstock(String[] ids );
    
    //批量上架
    int  batchReshelf(List<String> ids);
    
    //使用map集合，可以手动通过status 选择上架，下架
    @SuppressWarnings("rawtypes")
	int  batchReshelfandInstock(Map map);
    
    //批量删除
    int batchDel(String[] ids);
}