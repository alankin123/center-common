package com.alankin.center.common.redis;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis客户端<p>
 * @author lansongtao
 * @Date 2015年11月6日
 * @since 1.0
 */
public class RedisClient {

	/** 日志. */
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	/**
	 * 回调接口.<p>
	 */
	public interface RedisCallBack<T> {
		/**
		 * 回调执行.
		 * @param jedis		jedis
		 * @return			回调结果
		 */
		T execute(Jedis jedis);
	}

	/** 连接池 . */ 
	private JedisPool jedisPool;
	
	/**
	 * 设置连接池 .
	 * @param jedisPool		连接池
	 */
	public void setJedisPool(final JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	/**
	 * Redis回调处理
	 * @param callBack		回调接口
	 * @return 		结果
	 */
	private <T> T run(final RedisCallBack<T> callBack) {
		T value = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			value = callBack.execute(jedis);
		} catch (Exception e) {
			returnResource(jedis);
//			LOGGER.error(e.getMessage(), e);
			throw new JedisException(e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 释放连接资源.
	 * @param redis			Jedis
	 */
	public void returnResource(final Jedis redis) {
		if (redis != null) {
			redis.close();
		}
	}
	
	/**
	 * 获取键值.
	 * @param key		键
	 * @return			值
	 */
	public String get(final String key) {
		return run(new RedisCallBack<String>() {
			@Override
			public String execute(final Jedis jedis) {
				return jedis.get(key);
			}
		});
	}
	
	/**
	 * 设置键值(字符串).
	 * @param key		键
	 * @param value		值
	 * @return			返回码
	 */
	public String set(final String key, final String value) {
		return run(new RedisCallBack<String>() {
			@Override
			public String execute(final Jedis jedis) {
				return jedis.set(key, value);
			}
		});
	}
	
	/**
	 * 删除键值.
	 * @param key		键
	 * @return			值
	 */
	public Long del(final String key) {
		return run(new RedisCallBack<Long>() {
			@Override
			public Long execute(final Jedis jedis) {
				return jedis.del(key);
			}
		});
	}
	
	/**
	 * 关闭数据流
	 * @param closeable
	 */
	private void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 反序列化二进制为对象.
	 * @param bytes
	 * @return
	 */
	private Object unSerializ(byte[] bytes) {
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return inputStream.readObject();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} finally {
			close(inputStream);
		}
	}
	
	/**
	 * 获取键值.
	 * @param key		键
	 * @return			值
	 */
	public Object getObject(final String key) {
		return run(new RedisCallBack<Object>() {
			@Override
			public Object execute(final Jedis jedis) {
				return unSerializ(jedis.get(key.getBytes()));
			}
		});
	}
	
	/**
	 * 序列化对象为二进制.
	 * @param obj
	 * @return
	 */
	private byte[] serializ(Object obj) {
		ByteArrayOutputStream byteOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			byteOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
			return byteOutputStream.toByteArray();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} finally {
			close(objectOutputStream);
			close(byteOutputStream);
		}
	}
	
	/**
	 * 设置键值(对象).
	 * @param key		键
	 * @param value		对象
	 * @return			返回码
	 */
	public String setObject(final String key, final Object value) {
		return run(new RedisCallBack<String>() {
			@Override
			public String execute(final Jedis jedis) {
				return jedis.set(key.getBytes(), serializ(value));
			}
		});
	}
	
	/**
	 * 设置Map值
	 * @param key		Map的key
	 * @param field		Map的字段名
	 * @param value		Map的字段field对应的值
	 * @return			是否设置成功
	 */
	public boolean hset(final String key, final String field, final String value) {
		return run(new RedisCallBack<Boolean>() {
			@Override
			public Boolean execute(final Jedis jedis) {
				return jedis.hset(key, field, value) == 1;
			}
		});
	}
	
	/**
	 * 获取Map值
	 * @param key		Map的key
	 * @param field		Map的字段名
	 * @return			Map的字段field对应的值
	 */
	public String hget(final String key, final String field) {
		return run(new RedisCallBack<String>() {
			@Override
			public String execute(final Jedis jedis) {
				return jedis.hget(key, field);
			}
		});
	}
	
	/**
	 * 消息发布
	 * @param channel		订阅频道
	 * @param message		消息
	 * @return				是否发布成功
	 */
	public boolean publish(final String channel, final String message) {
		return run(new RedisCallBack<Boolean>() {
			@Override
			public Boolean execute(final Jedis jedis) {
				return jedis.publish(channel, message) == 1;
			}
		});
	}
	
	/**
	 * 消息订阅
	 * @param channel		订阅频道
	 * @param jedisPubSub	订阅监听器
	 */
	public boolean subscribe(final String channel, final JedisPubSub jedisPubSub) {
		return run(new RedisCallBack<Boolean>() {
			@Override
			public Boolean execute(final Jedis jedis) {
				jedis.subscribe(jedisPubSub, channel);
				return true;
			}
		});
	}
	
	public boolean lock(final String key, final String value) {
	    return run(new RedisCallBack<Boolean>() {
	        @Override
	        public Boolean execute(Jedis jedis) {
	            Boolean result = false;
	            if("OK".equalsIgnoreCase(jedis.watch(key))) {
	                Transaction transaction = jedis.multi();
	                transaction.set(key, value);
	                if(transaction.exec() !=null) {
	                    result = true;
	                }
	            }
	            return result;
	        }
        });
	}
}