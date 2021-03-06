package com.somnus.action.base;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import com.somnus.action.BaseAction;
import com.somnus.model.base.Syonline;
import com.somnus.service.base.SyonlineService;

@Namespace("/base")
@Action
public class OnlineAction extends BaseAction<Syonline> {

	private static final long serialVersionUID = -2378343644242373911L;

	/**
	 * 注入业务逻辑，使当前action调用service.xxx的时候，直接是调用基础业务逻辑
	 * 
	 * 如果想调用自己特有的服务方法时，请使用((TServiceI) service).methodName()这种形式强转类型调用
	 * 
	 * @param service
	 */
	@Autowired
	public void setService(SyonlineService service) {
		this.service = service;
	}

}
