package kernel.cache;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCachePool {

    private static Map<String, Cache> cacheBucket = new ConcurrentHashMap<>();

    public static <K, V> Cache<K, V> buildCache(String bucket, int size, int secondsTimeout, CacheLoader loader) {
        if (StrUtil.isBlank(bucket)) {
            throw new RuntimeException("错误的参数");
        }
        if (size <= 0) {
            size = 256;
        }

        if (cacheBucket.containsKey(bucket)) {
            return cacheBucket.get(bucket);
        }

        Caffeine<K, V> caffeine = Caffeine.newBuilder()
                //最大个数限制
                .maximumSize(size)
                //初始化容量
                .initialCapacity(size / 2)
                // 访问后过期（包括读和写）
                .expireAfterAccess(secondsTimeout, TimeUnit.SECONDS)
                // 写后过期
                .expireAfterWrite(secondsTimeout, TimeUnit.SECONDS)
                // 写后自动异步刷新
                //.refreshAfterWrite(1, TimeUnit.HOURS)
                //记录下缓存的一些统计数据，例如命中率等
                .recordStats()
                //cache对缓存写的通知回调
                .writer(new CacheWriter() {
                    @Override
                    public void write(Object key, Object value) {
                        //log.info("key={}, CacheWriter write", key);
                    }

                    @Override
                    public void delete(Object key, Object value, RemovalCause cause) {
                        //log.info("key={}, cause={}, CacheWriter delete", key, cause);
                    }
                });
        if (loader != null) {
            caffeine.build(loader);
        }

        Cache cache = caffeine.build();

        Cache existCache = cacheBucket.putIfAbsent(bucket, cache);
        if (existCache == null) {
            return cache;
        }

        return existCache;
    }
}
