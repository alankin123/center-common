package com.alankin.center.common.mapper;

import java.util.List;

import com.alankin.center.common.entity.BaseEntity;
import com.alankin.center.common.vo.ListVo.ListReqVO;

public interface IBaseMapper<T extends BaseEntity> {

	/**
	 * 新增记录
	 * 
	 * @param entity
	 */
	void create(T entity);
	
	/**
	 * 批量新增记录
	 * <p>
	 * 
	 * @param entitys
	 *            实体对象
	 * @return 插入记录数
	 */
	int batchSave(List<? extends T> entitys);

	/**
	 * 更新记录
	 * <p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return 更新记录数
	 */
	int update(T entity);

	/**
	 * 批量更新记录
	 * <p>
	 * 
	 * @return 更新记录数
	 * @param entitys
	 *            实体对象
	 */
	int batchUpdate(List<? extends T> entitys);



	/**
	 * 按ID删除记录
	 * <p>
	 * 
	 * @param id
	 *            对象ID
	 * @return 删除记录数
	 */
	int deleteById(long id);

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	int batchDeleteById(long[] ids);

	int batchDeleteById(Long[] ids);

	/**
	 * 按ID删除记录
	 * <p>
	 * 
	 * @param id
	 *            对象ID
	 * @return 删除记录数
	 */
	int deleteById(String id);

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	int batchDeleteById(String[] ids);

	/**
	 * 按ID查询单条记录
	 * <p>
	 * 
	 * @param id
	 *            实体对象ID
	 * @return 实体对象
	 */
	T queryById(long id);

	/**
	 * 按实体条件查询单条记录
	 * <p>
	 * 
	 * @param id
	 *            实体对象ID
	 * @return 实体对象
	 */
	List<T> queryByEntity(T entity);

	/**
	 * 分页查询
	 * 
	 * @param reqVO
	 * @return
	 */
	List<T> queryByPage(ListReqVO<?> reqVO);

	/**
	 * 查询记录条数
	 * 
	 * @param reqVO
	 * @return
	 */
	int queryCount(ListReqVO<?> reqVO);

}
