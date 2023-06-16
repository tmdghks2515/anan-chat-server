package io.klovers.server.common.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // redis 와 connection 을 생성해 주는 객체
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName("127.0.0.1");
        redisConfig.setPort(6379);

        return new JedisConnectionFactory(redisConfig);
    }


    /*
    * RedisTemplate
    * redis 서버와 통신을 처리하고, 사용자로 하여금 redis 모듈을 사용하기 쉽도록 다양한 기능을 제공하기 위해 스프링에서 높은 수준의 추상화를 통해서 오퍼레이션을 제공
    * Redis 는 기본적으로 key, value 를 바이트의 배열(byte[])로 저장한다. StringRedisTemplate 를 사용하면 key, value 를 모두 문자열로 저장할 수 있다.
    * RedisTemplate 은 Redis 저장소에 오브젝트를 저장할 때 기본값으로 정의된 JdkSerializationRedisSerializer 을 이용한다.
    * 따라서 해당 오브젝트는 반드시 java.io.Serializable 인터페이스를 구현해야 한다. 이 방식의 문제점은 다른 언어 환경에서 Redis 저장소에 접근할 경우 값을 인식하지 못한다는 것이다.
    * 또한 오브젝트의 클래스 메타 정보를 저장하다 보니 크기 또한 커진다. 특정 언어에 종속되지 않으려면 저장되는 값으로 JSON 문자열 또는 MessagePack 형식을 고려해야 한다.
    * */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());

        // 일반적인 key:value 의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash 를 사용할 경우 시리얼라이저
        // redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // default
        //redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory());
        return stringRedisTemplate;
    }

}
