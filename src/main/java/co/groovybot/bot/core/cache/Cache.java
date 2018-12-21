package co.groovybot.bot.core.cache;

import co.groovybot.bot.core.entity.DatabaseEntitiy;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.ISnowflake;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Cache<T extends DatabaseEntitiy> {

    private final Map<Long, T> cacheMap;
    private final Class<T> clazz;

    public Cache(Class<T> clazz) {
        this.cacheMap = new HashMap<>();
        this.clazz = clazz;
    }

    public T get(Long entityId) {
        if (cacheMap.containsKey(entityId))
            return cacheMap.get(entityId);
        else {
            try {
                T out = clazz.getDeclaredConstructor(Long.class).newInstance(entityId);
                cacheMap.put(entityId, out);
                return out;
            } catch (Exception e) {
                log.error("[Cache] An error occurred while updating entity in cache", e);
                return null;
            }
        }
    }

    public T get(ISnowflake snowflake) {
        return get(snowflake.getIdLong());
    }

    public void update(T instance) {
        cacheMap.replace(instance.entityId, instance);
        try {
            instance.updateInDatabase();
        } catch (Exception e) {
            log.error("[Cache] An error occurred while updating entity in cache", e);
        }
    }
}
