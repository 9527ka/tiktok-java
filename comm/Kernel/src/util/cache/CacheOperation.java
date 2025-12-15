package util.cache;

import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.redis.RedisHandler;
import util.concurrent.gofun.GoFun;
import util.concurrent.gofun.core.FunParams;
import util.concurrent.gofun.core.GoFunResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CacheOperation {
    private static final Logger logger = LoggerFactory.getLogger(CacheOperation.class);

    private static final Map<String, Long> LastOperateExpireTimeMap = new HashMap<>();

    private static final long maxWaitTime = 3000L; // 最多等 3 秒

    public static <R> R execute(String funKey, boolean syncWaitResult, long timeout, FunParams params, Function<FunParams, R> fun) {
        Date now = new Date();

        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        RedisHandler redisHandler = wac.getBean(RedisHandler.class);

        String resultCacheKey = funKey + ":result";
        Object lastCacheResult = redisHandler.get(resultCacheKey);

        Long lastExpireTime = LastOperateExpireTimeMap.get(funKey);
        lastExpireTime = lastExpireTime == null ? 0L : lastExpireTime;

        if (lastExpireTime > now.getTime()) {
            // 上次执行结果仍然有效
            if (lastCacheResult == null) {
                // 如果用户并发多次触发方法执行，为了减少重复执行的次数，此处是否需要优化，等待其他线程的执行结果？TODO
                // 暂时不考虑对这种并发场景的优化，简单处理。

                // 因意外原因，上次的执行结果丢失，需要强制再次触发一次执行
                LastOperateExpireTimeMap.put(funKey, now.getTime() + timeout);
                logger.info("CacheOperation#execute funKey:{} 相关的上次执行结果丢失，立即触发一次真实执行，并同步等待结果...", funKey);
                R result = fun.apply(params);

                long endTime = System.currentTimeMillis();
                logger.info("CacheOperation#execute funKey:{} 相关的真实执行调用结束，耗时:{} 毫秒", funKey, (endTime - now.getTime()));

                GoFunResult cacheValue = new GoFunResult();
                cacheValue.setExecuteTime(now.getTime());
                cacheValue.setFunKey(funKey);
                cacheValue.setFinishTime(endTime);
                cacheValue.setResult(result);
                try {
                    redisHandler.setSync(resultCacheKey, cacheValue);
                } catch (Exception e) {
                    logger.error("CacheOperation#execute 缓存 funKey:{} 的结果:{} 报错: ", funKey, result, e);
                }

                return result;
            } else {
                logger.info("CacheOperation#execute funKey:{} 相关的上次执行结果仍然处于有效期内，直接返回结果", funKey);
                GoFunResult cacheValue = (GoFunResult)lastCacheResult;
                if (StrUtil.isNotBlank(cacheValue.getErrClassName())) {
                    throw new BusinessException("执行抛出异常:" + cacheValue.getErrClassName() + "\n\t" + cacheValue.getErrMsg());
                }
                return (R)cacheValue.getResult();
            }
        } else {
            // 上次执行结果过期，需要触发再次执行
            if (lastCacheResult == null) {
                // 因意外原因，上次的执行结果丢失，需要强制再次触发一次执行
                LastOperateExpireTimeMap.put(funKey, now.getTime() + timeout);
                logger.info("CacheOperation#execute 结果过期的 funKey:{} 相关的上次执行结果丢失，立即触发一次真实执行，并同步等待结果...", funKey);
                R result = fun.apply(params);

                long endTime = System.currentTimeMillis();
                logger.info("CacheOperation#execute 结果过期的 funKey:{} 相关的真实执行调用结束，耗时:{} 毫秒", funKey, (endTime - now.getTime()));

                GoFunResult cacheValue = new GoFunResult();
                cacheValue.setFunKey(funKey);
                cacheValue.setExecuteTime(now.getTime());
                cacheValue.setFinishTime(endTime);
                cacheValue.setResult(result);
                try {
                    redisHandler.setSync(resultCacheKey, cacheValue);
                } catch (Exception e) {
                    logger.error("CacheOperation#execute 缓存 funKey:{} 的结果:{} 报错: ", funKey, result, e);
                }

                return result;
            } else {
                LastOperateExpireTimeMap.put(funKey, now.getTime() + timeout);

                if (syncWaitResult) {
                    // 虽然存在上次执行的缓存结果，但是参数要求使用最新结果，需要同步等待执行结果
                    logger.info("CacheOperation#execute 结果过期的 funKey:{} 根据要求需要同步等待最新执行结果，将立即触发一次真实执行，并同步等待结果...", funKey);
                    R result = fun.apply(params);

                    long endTime = System.currentTimeMillis();
                    logger.info("CacheOperation#execute 结果过期的 funKey:{} 相关的真实执行调用结束，耗时:{} 毫秒", funKey, (endTime - now.getTime()));

                    GoFunResult cacheValue = new GoFunResult();
                    cacheValue.setFunKey(funKey);
                    cacheValue.setExecuteTime(now.getTime());
                    cacheValue.setFinishTime(endTime);
                    cacheValue.setResult(result);
                    try {
                        redisHandler.setSync(resultCacheKey, cacheValue);
                    } catch (Exception e) {
                        logger.error("CacheOperation#execute 缓存 funKey:{} 的结果:{} 报错: ", funKey, result, e);
                    }

                    return result;
                } else {
                    // 虽然上次的结果过期，但是允许立即返回旧结果，同时异步触发一次执行
                    GoFun.go(params, funKey, fun);
                    logger.info("CacheOperation#execute 结果过期的 funKey:{} 根据需求无需同步等待最新执行结果，先直接返回上次结果", funKey);
                    GoFunResult cacheValue = (GoFunResult)lastCacheResult;
                    if (StrUtil.isNotBlank(cacheValue.getErrClassName())) {
                        throw new BusinessException("执行抛出异常:" + cacheValue.getErrClassName() + "\n\t" + cacheValue.getErrMsg());
                    }
                    return (R)cacheValue.getResult();
                }
            }
        }
    }
}
