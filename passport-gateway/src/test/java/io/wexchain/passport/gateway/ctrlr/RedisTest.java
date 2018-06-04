package io.wexchain.passport.gateway.ctrlr;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration({ "/context-env.xml", "/META-INF/passport-gateway/spring/context-redis.xml" })
public class RedisTest extends AbstractTestNGSpringContextTests {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	public void testget() {
		String string = stringRedisTemplate.opsForValue().get("abc");
		System.out.println(string);
	}

	@Test
	public void testge2t() {
		String string = stringRedisTemplate.opsForValue().get("abc2");
		System.out.println(string);
	}

	@Test
	public void testset() {
		stringRedisTemplate.opsForValue().set("abc", "def", 60, TimeUnit.SECONDS);
	}

	@Test
	public void testset2() {
		stringRedisTemplate.opsForValue().set("abc", "", 60, TimeUnit.SECONDS);
	}
}
