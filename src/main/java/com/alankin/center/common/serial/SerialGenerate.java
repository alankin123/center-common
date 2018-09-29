package com.alankin.center.common.serial;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.alankin.center.common.utils.DateUtilSafe;
import com.alankin.center.common.utils.StringUtils;

public abstract class SerialGenerate {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired(required = false)
	RedisTemplate<String, RedisAtomicLong> redisTemplate;

	private RedisTemplate<String, RedisAtomicLong> getRedisTemplate() {
		if (this.redisTemplate == null) {
			logger.error("spring 容器中没有配置redisTemplate ");
			throw new RuntimeException("spring 容器中没有配置redisTemplate ");
		}
		return redisTemplate;

	}

	public long getSerialForDay(String key, String dayStr, boolean nextFlag) {
		if (nextFlag) {
			return getSerialByMap(key, dayStr).incrementAndGet();
		} else {
			return getSerialByMap(key, dayStr).get();
		}

	}

	/**
	 * 获取自增序列
	 * 
	 * @param key
	 * @param dayStr
	 * @param expireTime
	 *            超时时间（单位：秒）
	 * @return
	 */
	private RedisAtomicLong getSerialByMap(String key, long expireTime) {
		String redisKey = "SerialGenerate-" + key;

		RedisAtomicLong redisAtomicLong = new RedisAtomicLong(redisKey, getRedisTemplate().getConnectionFactory());
		if (redisAtomicLong.getExpire() > -1) {
			return redisAtomicLong;
		} else if (expireTime > 0) {
			redisAtomicLong.expire(expireTime, TimeUnit.SECONDS);
		}

		return redisAtomicLong;
	}

	private RedisAtomicLong getSerialByMap(String key, String dayStr) {
		String redisKey = "SerialGenerate-" + key;
		if (!StringUtils.isEmpty(dayStr)) {
			redisKey += dayStr;
		}
		RedisAtomicLong redisAtomicLong = new RedisAtomicLong(redisKey, getRedisTemplate().getConnectionFactory());
		if (redisAtomicLong.getExpire() > -1) {
			return redisAtomicLong;
		} else if (!StringUtils.isEmpty(dayStr)) {
			redisAtomicLong.expire(1, TimeUnit.DAYS);
		}

		return redisAtomicLong;
	}

	/**
	 * 根据前缀，按日期{yyMMdd},生成序列号
	 * 
	 * @param key
	 *            前缀
	 * @param serialLength
	 *            序列号长度（不包含前缀和日期）
	 * @return
	 */
	public String generateNoForDay(String key, int serialLength) {
		return generateNoForDay(key, serialLength, true);
	}

	/**
	 * 根据前缀，按日期{yyMMdd},生成序列号
	 * 
	 * @param key
	 *            前缀
	 * @param serialLength
	 *            序列号长度（不包含前缀和日期）
	 * @return
	 */
	public String generateNoForDay(String key, int serialLength, String dateFormat) {
		return generateNoForDay(key, serialLength, dateFormat, true);
	}

	/**
	 * 根据前缀，按日期{yyMMdd},生成序列号
	 * 
	 * @param key
	 *            前缀
	 * @param serialLength
	 *            序列号长度（不包含前缀和日期）
	 * @return
	 */
	public String generateNoForDay(String key, int serialLength, boolean nextFlag) {
		return generateNoForDay(key, serialLength, DateUtilSafe.dtLong, nextFlag);
	}

	/**
	 * 根据前缀，按日期{yyMMdd},生成序列号
	 * 
	 * @param key
	 *            前缀
	 * @param serialLength
	 *            序列号长度（不包含前缀和日期）
	 * @return
	 */
	public String generateNoForDay(String key, int serialLength, String dateFormat, boolean nextFlag) {
		return generateNoForDay(key, serialLength, dateFormat, nextFlag, true);
	}

	public String generateNoForDay(String key, int serialLength, String dateFormat, boolean nextFlag, boolean keyFlag) {
		String dayStr = DateUtilSafe.format(new Date(), dateFormat);
		long serial = getSerialForDay(key, dayStr, nextFlag);

		try {
			if (keyFlag) {
				return key + dayStr + StringUtils.supplyValue(serialLength, false, serial + "", "0", null);
			} else {
				return dayStr + StringUtils.supplyValue(serialLength, false, serial + "", "0", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 生成指定长度的序列号
	 * 
	 * @param key
	 *            前缀
	 * @param serialLength（不包含前缀）
	 *            序列号长度
	 * @return
	 */
	public String generateNo(String key, int serialLength) {
		return generateNo(key, serialLength, 0);
	}

	/**
	 * 生成指定长度的序列号
	 * 
	 * @param key
	 *            前缀
	 * @param serialLength（不包含前缀）
	 *            序列号长度
	 * @return
	 */
	public String generateNo(String key, int serialLength, long expireTime) {
		return generateNo(key, serialLength, true, expireTime);
	}

	/**
	 * 生成指定长度的序列号
	 * 
	 * @param key
	 *            前缀
	 * @param serialLength（不包含前缀）
	 *            序列号长度
	 * @return
	 */
	public String generateNo(String key, int serialLength, boolean nextFlag, long expireTime) {
		long serial = getSerialForDay(key, nextFlag, expireTime);

		try {
			return key + StringUtils.supplyValue(serialLength, false, serial + "", "0", null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public long getSerialForDay(String key, boolean nextFlag, long expireTime) {
		if (nextFlag) {
			return getSerialByMap(key, expireTime).incrementAndGet();
		} else {
			return getSerialByMap(key, expireTime).get();
		}
	}

}
