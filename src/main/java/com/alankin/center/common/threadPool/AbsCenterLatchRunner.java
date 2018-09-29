package com.alankin.center.common.threadPool;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsCenterLatchRunner implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public CountDownLatch latch;

	public int runIndex;

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public void setRunIndex(int runIndex) {
		this.runIndex = runIndex;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		execete();
		logger.debug("========={}-耗时：{}", runIndex, System.currentTimeMillis() - start);
		latch.countDown();
	}

	public abstract void execete();

}
