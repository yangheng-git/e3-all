package cn.e3mall.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 服务层和表现层通用的pojo
 * @author yang
 *
 */
public class EasyUIDatagridResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5387622715170453384L;
	private long total;
	@SuppressWarnings("rawtypes")
	private List rows;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	@SuppressWarnings("rawtypes")
	public List getRows() {
		return rows;
	}
	@SuppressWarnings("rawtypes")
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
	
}
