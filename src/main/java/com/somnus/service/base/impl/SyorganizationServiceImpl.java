package com.somnus.service.base.impl;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.somnus.dao.base.SyresourceDao;
import com.somnus.dao.base.SyuserDao;
import com.somnus.model.base.Syorganization;
import com.somnus.model.base.Syresource;
import com.somnus.model.base.Syuser;
import com.somnus.service.base.SyorganizationService;
import com.somnus.service.impl.BaseServiceImpl;
import com.somnus.util.base.BeanUtils;
import com.somnus.util.base.HqlFilter;

/**
 * 机构业务
 * 
 * @author Somnus
 * 
 */
@Service
@Transactional
public class SyorganizationServiceImpl extends BaseServiceImpl<Syorganization> implements SyorganizationService {

	@Autowired
	private SyresourceDao resourceDao;

	@Autowired
	private SyuserDao userDao;

	public void updateOrganization(Syorganization syorganization) {
		if (!StringUtils.isBlank(syorganization.getId())) {
			Syorganization t = getById(syorganization.getId());
			Syorganization oldParent = t.getSyorganization();
			BeanUtils.copyNotNullProperties(syorganization, t, new String[] { "createdatetime" });
			if (syorganization.getSyorganization() != null) {// 说明要修改的节点选中了上级节点
				Syorganization pt = getById(syorganization.getSyorganization().getId());// 上级节点
				isParentToChild(t, pt, oldParent);// 说明要将当前节点修改到当前节点的子或者孙子下
				t.setSyorganization(pt);
			} else {
				t.setSyorganization(null);
			}
		}
	}

	/**
	 * 判断是否是将当前节点修改到当前节点的子节点下
	 * 
	 * @param t
	 *            当前节点
	 * @param pt
	 *            要修改到的节点
	 * 
	 * @param oldParent
	 *            原上级节点
	 * @return
	 */
	@Transactional(readOnly = false)
	private boolean isParentToChild(Syorganization t, Syorganization pt, Syorganization oldParent) {
		if (pt != null && pt.getSyorganization() != null) {
			if (StringUtils.equals(pt.getSyorganization().getId(), t.getId())) {
				pt.setSyorganization(oldParent);
				return true;
			} else {
				return isParentToChild(t, pt.getSyorganization(), oldParent);
			}
		}
		return false;
	}

	public void grant(String id, String resourceIds) {
		Syorganization organization = getById(id);
		if (organization != null) {
			organization.setSyresources(new HashSet<Syresource>());
			for (String resourceId : resourceIds.split(",")) {
				if (!StringUtils.isBlank(resourceId)) {
					Syresource resource = resourceDao.getById(resourceId);
					if (resource != null) {
						organization.getSyresources().add(resource);
					}
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public List<Syorganization> findOrganizationByFilter(HqlFilter hqlFilter) {
		String hql = "select distinct t from Syorganization t join t.syusers user";
		return find(hql + hqlFilter.getWhereAndOrderHql(), hqlFilter.getParams());
	}

	public void saveOrganization(Syorganization syorganization, String userId) {
		save(syorganization);

		Syuser user = userDao.getById(userId);
		user.getSyorganizations().add(syorganization);
	}

}
