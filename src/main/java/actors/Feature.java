package actors;

import db.RedisHelper;
import redis.clients.jedis.Jedis;
import java.util.Map;
import java.util.HashMap;

public class Feature {

    public static final String FEATURE_SET_PREFIX = "sFeature:";
    public static final String FEATURE_KEY_PREFIX = "Feature:";
    public static final String NEXT_FEATURE_ID_KEY = "NEXT_FEATURE_ID";
    public static final String MASTER_NAME_SET = "sFeatureNames";

    private String name;
    private long id;

    public Feature(String name) {
        this.name = name;
        this.id = getId(name);
    }

    public Feature(long id) {
        this.id = id;
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            this.name = jedis.hget(getKey(), "name");
        }
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public long getId(String name) {
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            if(jedis.zrank(MASTER_NAME_SET, name) == null) {return 0;}
            Double id = jedis.zscore(MASTER_NAME_SET, name);
            return id.longValue();
        }
    }

    public boolean isNameUnique() {
        if(name == null) {
            return false;
        }
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            return jedis.zrank(MASTER_NAME_SET, name) == null;
        }
    }

    public String getKey() {
        return FEATURE_KEY_PREFIX+getId();
    }

    public String getSetKey() {
        return FEATURE_SET_PREFIX+id;
    }

    public void grantAccess(Role role) {
        if(id == 0 && getId(name) == 0) {
            return;
        }
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            jedis.sadd(getSetKey(), role.getKey());
        }
    }

    public boolean isAuthorized(Role role) {
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            return jedis.sismember(getSetKey(), role.getKey());
        }
    }

    public void save() {
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            if(id == 0) {
                id = RedisHelper.getNextId(NEXT_FEATURE_ID_KEY, jedis);
            }
            Map<String,String> map = new HashMap<>();
            map.put("id", Long.toString(id));
            map.put("name", name);
            jedis.hmset(FEATURE_KEY_PREFIX+id, map);
            jedis.zadd(MASTER_NAME_SET, id, name);
        }
    }
}
