package com.alankin.center.common.threadPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程池工具
 *
 * @author lansongtao
 * @Date 2018年1月30日
 * @since 1.0
 */
public class CenterThreadPool {

	private static final Logger logger = LoggerFactory.getLogger(CenterThreadPool.class);

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
	        new ThreadPoolExecutor.CallerRunsPolicy());

	/**
	 * 利用公用线程池异步执行方法
	 * 
	 * @param obj	执行法法的对象
	 * @param methodName	方法名字
	 * @param args	参数
	 */
	public static void asyncProxyExecutor(Object obj, String methodName, Object... args) {
		executor.execute(new ProxyExecuteThread(obj, methodName, args));
	}

	/**
	 * 执行确定数量的任务，会阻塞等待所有线程任务都执行完之后再返回
	 * 
	 * @param threadCount
	 *            任务数量
	 * @param runnerList
	 *            具体执行的实现
	 */
	public static void executeByLatch(int threadCount, Class<?> queryThreadType, Class<?>[] parameterTypes, Object[] parameters) {
		List<AbsCenterLatchRunner> runnerList = initQueryThread(threadCount, queryThreadType, parameterTypes, parameters);
		executeByLatch(threadCount, runnerList, 60);
	}

	/**
	 * 执行确定数量的任务，会阻塞等待所有线程任务都执行完之后再返回
	 * 
	 * @param threadCount
	 *            任务数量
	 * @param runnerList
	 *            具体执行的实现
	 * @param timeout
	 *            等到超时时间，单位（秒）
	 */
	private static void executeByLatch(int threadCount, List<AbsCenterLatchRunner> runnerList, int timeout) {
		CountDownLatch latch = new CountDownLatch(threadCount);
		int runIndex = 1;
		for (AbsCenterLatchRunner runner : runnerList) {
			runner.setLatch(latch);
			runner.setRunIndex(runIndex++);
			executor.execute(runner);
		}
		// 等待所有线程执行完
		try {
			latch.await(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

	/**
	 * 初始化线程对象
	 * 
	 * @param runIndex
	 * @param queryThreadType
	 * @param parms
	 * @return
	 */
	private static List<AbsCenterLatchRunner> initQueryThread(int runIndex, Class<?> queryThreadType, Class<?>[] parameterTypes, Object[] parameters) {
		List<AbsCenterLatchRunner> resutList = null;
		try {
			// 获取构造方法
			Constructor<?> c = null;
			if (parameterTypes == null || parameters == null) {
				c = queryThreadType.getConstructor();
			} else {
				c = queryThreadType.getConstructor(parameterTypes);
			}

			resutList = new ArrayList<AbsCenterLatchRunner>(runIndex);
			for (int i = 1; i < runIndex + 1; i++) {
				resutList.add((AbsCenterLatchRunner) c.newInstance(parameters));
			}

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
		        | SecurityException e) {
			e.printStackTrace();
		}

		return resutList;
	}

}
