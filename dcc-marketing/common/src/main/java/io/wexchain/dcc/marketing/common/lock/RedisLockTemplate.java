package io.wexchain.dcc.marketing.common.lock;

import com.wexmarket.topia.commons.basic.competition.LockCallback;
import com.wexmarket.topia.commons.basic.competition.LockTemplate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.time.Duration;
import java.util.concurrent.locks.Lock;

public class RedisLockTemplate implements LockTemplate {

	private RedisLockRegistry redisLockRegistry;

	/**
	 * 分布式锁,相同的或者不同场景共用一个lockKey,相同的场景执行有冷却时间,不同的场景无冷却时间
	 * 
	 * @param lockKey.
	 *            既被用作锁名，也被用作锁信息名
	 * @param scenario
	 *            场景
	 * @param lockCallback
	 * @return
	 */
	public <T> T execute(String lockKey, String scenario, LockCallback<T> lockCallback) {

		Lock lock = null;
		try {
			lock = redisLockRegistry.obtain(lockKey);
			boolean tryLock = lock.tryLock();
			if (tryLock) {
				T locked = lockCallback.locked();
				return locked;
			} else {
				return null;
			}

		} finally {
			try {
				if (lock != null) {
					lock.unlock();
				}
			} catch (Exception e) {
			}
			redisLockRegistry.expireUnusedOlderThan(Duration.ofMinutes(5).toMillis());
		}

	}

	@Required
	public void setRedisLockRegistry(RedisLockRegistry redisLockRegistry) {
		this.redisLockRegistry = redisLockRegistry;
	}

}
