package com.alankin.center.common.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alankin.center.common.db.helper.Column;
import com.alankin.center.common.db.helper.Table;
import com.alankin.center.common.db.helper.TableFactory;
import com.alankin.center.common.exception.CenterValidateException;
import com.alankin.center.common.global.MapCache;
import com.alankin.center.common.threadPool.CenterThreadPool;
import com.alankin.center.common.utils.StringUtil;
import com.alankin.center.common.utils.StringUtils;
import com.alankin.center.common.utils.VOUtils;
import com.alankin.center.common.vo.TableName;

@Component
public class FieldValidate {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Map<String, Map<String, Column>> fieldConf = null;

	private final static int OPER_TYPE_CREATE = 1;

	private final static int OPER_TYPE_UPDATE = 2;

	@Autowired(required = false)
	DataSource dataSource;

	/**
	 * 初始化
	 */
	@PostConstruct
	public void init() {
		// 异步完成
		CenterThreadPool.asyncProxyExecutor(this, "initFieldConf");
	}

	/**
	 * 启动时自动加载数据库字段信息，用于后续的校验操作
	 */
	public synchronized void initFieldConf() {
		if (dataSource == null) {
			return;
		}
		logger.info("============初始化数据库字段校验信息============");
		if (fieldConf == null) {
			fieldConf = new ConcurrentHashMap<String, Map<String, Column>>();
		}

		List<Table> tableList = null;
		try {
			tableList = TableFactory.getInstance(dataSource).getAllTables();
		} catch (SQLException e) {
			logger.error("获取数据库字段校验信息失败！", e);
			return;
		}

		if (tableList == null || tableList.isEmpty()) {
			return;
		}

		for (Table table : tableList) {
			if (table.getColumns() == null || table.getColumns().isEmpty()) {
				continue;
			}
			Map<String, Column> columnMap = new ConcurrentHashMap<String, Column>(table.getColumns().size());
			fieldConf.put(table.getSqlName(), columnMap);

			for (Column column : table.getColumns()) {

				columnMap.put(column.getColumnNameFirstLower(), column);
			}

		}

		logger.info("============加载成功:{}============", fieldConf.size());
	}

	/**
	 * 新增实体时校验字段非空和超长
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void validateForCreate(Object entity) throws CenterValidateException {
		validateEntity(entity, OPER_TYPE_CREATE);

	}

	/**
	 * 新增实体时校验字段非空和超长
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void validateForCreate(List<?> entitys) {
		for (Object entity : entitys) {
			try {
				validateEntity(entity, OPER_TYPE_CREATE);
			} catch (CenterValidateException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * 修改实体时校验字段非空和超长
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void validateForUpdate(Object entity) throws CenterValidateException {
		validateEntity(entity, OPER_TYPE_UPDATE);
	}

	/**
	 * 新增实体时校验字段非空和超长
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void validateForUpdate(List<?> entitys) {
		for (Object entity : entitys) {
			try {
				validateEntity(entity, OPER_TYPE_UPDATE);
			} catch (CenterValidateException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * 校验字段非空和超长
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void validateEntity(Object entity, int operType) throws CenterValidateException {
		if (fieldConf == null) {
			return;
		}
		TableName param = entity.getClass().getAnnotation(TableName.class);
		if (param == null) {
			return;
		}
		Map<String, Column> columnMap = fieldConf.get(param.value());
		if (columnMap == null) {
			return;
		}

		boolean hasUpdateField = false;

		StringBuffer sb = new StringBuffer();
		Field[] fields = VOUtils.getAllFields(entity.getClass());
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			String fieldName = field.getName();
			Object fieldValue = null;

			try {
				Method method = MapCache.getMethodByType(entity.getClass(), "get" + StringUtil.firstCharUpperCase(fieldName));
				fieldValue = method.invoke(entity);
			} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
				logger.error(e.getMessage(), e);
				continue;
			}

			Column column = columnMap.get(fieldName);
			if (column == null) {
				continue;
			}

			// 校验非空
			if (operType == OPER_TYPE_UPDATE && fieldValue == null) {
				// 修改操作，字段值为空，则不校验
				continue;
			}
			// 修改语句set 字句必须要有内容
			if (operType == OPER_TYPE_UPDATE && !column.isPk()) {
				hasUpdateField = true;
			}

			if (StringUtils.isEmpty(fieldValue)) {
				if (!column.isNullable() && !column.isPk()) {
					sb.append("\r\n").append(column.getNickName()).append(":").append(" 不能为空").append(";");
				}
				continue;
			}

			// 校验长度
			if (!column.getSqlTypeName().startsWith("DATE") && column.getSize() > 0 && column.getSize() < fieldValue.toString().length()) {
				sb.append("\r\n").append(column.getNickName()).append(":").append(" 超过最大长度:").append(column.getSize()).append(";");
				continue;
			}
		}

		if (operType == OPER_TYPE_UPDATE && !hasUpdateField) {
			throw new CenterValidateException(50, param.value() + "的update语句没有填写set内容");
		}

		if (sb.length() > 0) {
			throw new CenterValidateException(50, sb.toString());
		}

	}

}
