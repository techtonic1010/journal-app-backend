package entities;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest(classes = JournalApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testKeyValue() {
//        redisTemplate.opsForValue().set("email", "vipul@gmail.com");
//        String email = redisTemplate.opsForValue().get("email");
//        System.out.println("Fetched from Redis: " + email);
        Object salary = redisTemplate.opsForValue().get("salary");
        System.out.println(salary);
        int a= 1;
    }
}
