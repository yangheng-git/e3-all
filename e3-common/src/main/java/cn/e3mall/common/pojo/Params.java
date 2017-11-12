package cn.e3mall.common.pojo;

import java.io.Serializable;
/**
 * 实现上下架功能。 删除也可以用
 * status =200 表示成功
 */
public class Params implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8577732643312917682L;
	
	String ids;
	String status;
	
	
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	

}
