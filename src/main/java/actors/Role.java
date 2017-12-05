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
        this.id = getId(name);
    }

    public Role(long id) {
        this.id = id;
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            this.name = jedis.hget(getKey(), "name");
        }
    }

    public long getId(String name) {
        try (Jedis jedis = (Jedis)RedisHelper.jedis()) {
            if(jedis.zrank(MASTER_NAME_SET, name) == null) {return 0;}
            Double id = jedis.zscore(MASTER_NAME_SET, name);
            return id.longValue();
        }
    }

    public long getId() {
        return id;
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
        return ROLE_KEY_PREFIX+id;
    }

    public String getSetKey() {
        return ROLE_SET_PREFIX+id;
    }

    public void addAccess(Feature feature) {
        if(id == 0) {
            return;
        }
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
            jedis.zadd(MASTER_NAME_SET, id, name);
        }
    }
}
