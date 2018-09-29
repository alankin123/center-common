/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * List工具类 .<p>
 * @author lansongtao
 * @Date 2015年9月28日
 * @since 1.0
 */
public class ListUtils {
	
	/**
	 * 按最大记录数拆分List.
	 * @param entityList			记录List
	 * @param pageSize				拆分记录数
	 * @return						拆分List
	 */
	public static <T> List<List<? extends T>> splitListByChild(final List<? extends T> entityList, final int pageSize) {
		List<List<? extends T>> lists = new ArrayList<List<? extends T>>();
		if (EmptyUtils.isEmpty(entityList)) {
			return lists;
		}
		if (entityList.size() <= pageSize) {
			lists.add(entityList);
			return lists;
		}
		int count = 0;
		List<T> list = new ArrayList<T>();
		for (T entity : entityList) {
			list.add(entity);
			if (++count % pageSize == 0) {
				lists.add(list);
				list = new ArrayList<T>();
			}
		}
		if (list.size() > 0) {
			lists.add(list);
		}
		return lists;
	}
	
	/**
	 * 按最大记录数拆分List.
	 * @param entityList			记录List
	 * @param pageSize				拆分记录数
	 * @return						拆分List
	 */
	public static <T> List<List<T>> splitList(final List<T> entityList, final int pageSize) {
		List<List<T>> lists = new ArrayList<List<T>>();
		if (EmptyUtils.isEmpty(entityList)) {
			return lists;
		}
		if (entityList.size() <= pageSize) {
			lists.add(entityList);
			return lists;
		}
		int count = 0;
		List<T> list = new ArrayList<T>();
		for (T entity : entityList) {
			list.add(entity);
			if (++count % pageSize == 0) {
				lists.add(list);
				list = new ArrayList<T>();
			}
		}
		if (list.size() > 0) {
			lists.add(list);
		}
		return lists;
	}
	
	/**
	 * 按最大记录数拆分List.<p>
	 * 最大记录数默认为100
	 * @param entityList			记录List
	 * @return						拆分List
	 */
	public static <T> List<List<T>> splitList(final List<T> entityList) {
		return splitList(entityList, 100);
	}
	
	/**
	 * 分页拆分
	 * @param pageSize		每页最大记录数
	 * @param count			总记录数
	 * @return
	 */
	public static List<PageIndex> splitPages(int pageSize, int count) {
	    List<PageIndex> pageIndexs = new ArrayList<PageIndex>();
	    if (count > pageSize) {
	    	int size = count / pageSize;
	    	for (int i = 0; i <= size; i++) {
	    		int beginIndex = i * pageSize + 1;
	    		if (beginIndex > count) {
	    			break;
	    		}
				int endIndex = beginIndex + pageSize - 1;
				if (endIndex > count) {
					endIndex = count;
				}
				pageIndexs.add(new PageIndex(beginIndex, endIndex, i + 1));
	        }
	    } else {
	    	pageIndexs.add(new PageIndex(1, count, 1));
	    }
	    return pageIndexs;
    }
	

	public static class PageIndex {

		/** 起始页 */
		private int beginIndex;

		/** 结束页 */
		private int endIndex;

		/** 页数 */
		private int index;

		public int getBeginIndex() {
			return beginIndex;
		}

		public void setBeginIndex(int beginIndex) {
			this.beginIndex = beginIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}

		public void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		/**
         * @param beginIndex
         * @param endIndex
         * @param index
         */
        public PageIndex(int beginIndex, int endIndex, int index) {
	        super();
	        this.beginIndex = beginIndex;
	        this.endIndex = endIndex;
	        this.index = index;
        }

		@Override
        public String toString() {
	        return "PageIndex [beginIndex=" + beginIndex + ", endIndex=" + endIndex + ", index="
	                + index + "]";
        }
	}
}
