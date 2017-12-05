package actors;

import db.RedisHelper;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseph.stowe on 12/6/17.
 */
public class Role {

    public static final String ROLE_SET_PREFIX = "sRole:";
    public static final String ROLE_KEY_PREFIX = "Role:";
    public static final String NEXT_ROLE_ID_KEY = "NEXT_ROLE_ID";
    public static final String MASTER_NAME_SET = "sRoleNames";

    private String name;
    private long id;

    public Role(String name) {
        this.name = name;
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
        return ROLE_KEY_PREFIX+id;
    }

    public String getSetKey() {
        return ROLE_SET_PREFIX+id;
    }

    public void addAccess(Feature feature) {
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            jedis.sadd(getSetKey(), feature.getKey());
        }
    }

    public void save() {
        try (Jedis jedis = (Jedis) RedisHelper.jedis()) {
            if(id == 0) {
                id = RedisHelper.getNextId(NEXT_ROLE_ID_KEY, jedis);
            }
            Map<String,String> map = new HashMap<>();
            map.put("id", Long.toString(id));
            map.put("name", name);
            jedis.hmset(ROLE_KEY_PREFIX+id, map);
            jedis.sadd(MASTER_NAME_SET, name);
        }
    }
}
