package com.alankin.center.common.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alankin.center.common.exception.CenterException;
import com.alankin.center.common.utils.StringUtils;

@Component
public class RedisLocker {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String prefix = "LOKER_";

	@Autowired(required = false)
	private RedisTemplate<String, String> redisTemplate;

	private RedisTemplate<String, String> getRedisTemplate() {
		if (this.redisTemplate == null) {
			logger.error("spring 容器中没有配置redisTemplate ");
			throw new RuntimeException("spring 容器中没有配置redisTemplate ");
		}
		return redisTemplate;

	}

	/***
	 * 加锁(默认超时时间60秒)
	 * 
	 * @param key
	 * @return 锁住返回解锁密钥，否则返回null
	 * @throws CenterException
	 */
	public String lock(String key) throws CenterException {
		return lock(key, 60);
	}

	/***
	 * 加锁
	 * 
	 * @param key
	 * @param expireTime
	 *            超时时间(单位：秒)
	 * @return 锁住返回解锁密钥，否则返回null
	 * @throws CenterException
	 */
	public String lock(String key, long expireTime) throws CenterException {
		key = prefix + key;
		String value = (System.currentTimeMillis() + (expireTime * 1000)) + "";
		// setNX 返回boolean
		if (getRedisTemplate().opsForValue().setIfAbsent(key, value)) {
			// 设置过期
			getRedisTemplate().expire(key, expireTime, TimeUnit.SECONDS);
			return value;
		}
		// 如果锁超时 ***
		String currentValue = getRedisTemplate().opsForValue().get(key);
		if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
			// 获取上一个锁的时间
			String oldvalue = getRedisTemplate().opsForValue().getAndSet(key, value);
			if (!StringUtils.isEmpty(oldvalue) && oldvalue.equals(currentValue)) {
				// 设置过期
				getRedisTemplate().expire(key, expireTime, TimeUnit.MINUTES);
				return value;
			}
		}
		return null;
	}

	/***
	 * 解锁
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void unlock(String key, String lockKey) {
		key = prefix + key;
		try {
			String currentValue = getRedisTemplate().opsForValue().get(key);
			if (!StringUtils.isEmpty(currentValue) && currentValue.equals(lockKey)) {
				getRedisTemplate().opsForValue().getOperations().delete(key);
			} else {
				logger.debug("解锁异常密钥不正确");
			}
		} catch (Exception e) {
			logger.error("解锁异常", e);
		}
	}
}