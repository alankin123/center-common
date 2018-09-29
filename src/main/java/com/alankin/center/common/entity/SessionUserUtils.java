package com.alankin.center.common.entity;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alankin.center.common.service.ThreadContext;
import com.alankin.center.common.utils.VOUtils;

public class SessionUserUtils {

	static Set<String> createIdFieldSet = new HashSet<String>();
	static Set<String> createNameFieldSet = new HashSet<String>();
	static Set<String> updateIdFieldSet = new HashSet<String>();
	static Set<String> updateNameFieldSet = new HashSet<String>();

	static {
		createIdFieldSet.add("createdPersonId");
		createIdFieldSet.add("createUserId");
	}

	static {
		createNameFieldSet.add("createdPersonName");
		createNameFieldSet.add("createUserName");
	}

	static {
		updateIdFieldSet.add("lastModifyPersonId");
	}

	static {
		updateNameFieldSet.add("lastModifyPersonName");
	}

	/**
	 * 新增实体时，从session中注入创建人信息
	 * 
	 * @param entity
	 */
	public static void putSessionUserForCreate(Object entity) {
		putSessionUser(entity, createIdFieldSet, createNameFieldSet);
	}

	/**
	 * 新增实体时，从session中注入创建人信息
	 * 
	 * @param entity
	 */
	public static void putSessionUserForCreate(List<Object> entitys) {
		for (Object entity : entitys) {
			putSessionUserForCreate(entity);
		}
	}

	/**
	 * 新增实体时，从session中注入创建人信息
	 * 
	 * @param entity
	 */
	public static void putSessionUserForUpdate(Object entity) {
		putSessionUser(entity, updateIdFieldSet, updateNameFieldSet);
	}

	/**
	 * 新增实体时，从session中注入创建人信息
	 * 
	 * @param entity
	 */
	public static void putSessionUserForUpdate(List<Object> entitys) {
		for (Object entity : entitys) {
			putSessionUserForUpdate(entity);
		}
	}

	/**
	 * 从session中注入操作人信息
	 * 
	 * @param entity
	 */
	private static void putSessionUser(Object entity, Set<String> idFieldSet, Set<String> nameFieldSet) {
		// 从session中注入数据权限信息
		SessionUser sessionUser = null;
		try {
			sessionUser = ThreadContext.getSessionloginUser();

			Field[] fields = VOUtils.getAllFields(entity.getClass());
			for (Field field : fields) {
				if (idFieldSet.contains(field.getName())) {
					VOUtils.setFieldValue(field, entity, sessionUser.getUserId());
				}
				if (nameFieldSet.contains(field.getName())) {
					VOUtils.setFieldValue(field, entity, sessionUser.getNickName());
				}
			}
		} catch (Exception e) {
			return;
		}
	}

}
