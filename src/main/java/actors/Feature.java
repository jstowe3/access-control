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
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public boolean isNameUnique() {
        if(name == null) {
            return false;
        }
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            return jedis.sismember(MASTER_NAME_SET, name);
        }
    }

    public String getKey() {
        return FEATURE_KEY_PREFIX+getId();
    }

    public String getSetKey() {
        return FEATURE_SET_PREFIX+id;
    }

    public void grantAccess(Role role) {
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
            jedis.sadd(MASTER_NAME_SET, name);
        }
    }
}
