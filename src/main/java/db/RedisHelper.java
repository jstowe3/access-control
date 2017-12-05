package db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisHelper {
    private static String _redis_host;
    private static JedisPool _pool = null;

    public static void setRedisHost(String redisHost) {_redis_host = redisHost;}
    public static void setPool(JedisPool pool) {
        _pool = pool;
    }

    // Alias of getJedis()
    public static JedisCommands jedis() {
        return getJedis();
    }

    public static Jedis getJedis() {
        if (_pool == null) {
            _pool = new JedisPool(new JedisPoolConfig(), _redis_host);
        }
        return _pool.getResource();
    }

    public static long getNextId(String key, JedisCommands jedis) {
        return jedis.incr(key);
    }
}
