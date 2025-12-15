package util.cache;

import com.alibaba.fastjson.JSON;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.redis.RedisHandler;
import util.concurrent.gofun.core.GoFunExecuteResultEvent;
import util.concurrent.gofun.core.GoFunResult;

/**
 * 监听 functionEvent 事件，并将结果保存到 redis 缓存中
 */
public class GoFunExecuteResultEventListener implements ApplicationListener<GoFunExecuteResultEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(GoFunExecuteResultEvent event) {
        GoFunResult funResult = event.getResult();
        logger.info("监听到 functionEvent 事件:" + JSON.toJSONString(funResult));

        try {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            RedisHandler redisHandler = wac.getBean(RedisHandler.class);

            String cacheKey = funResult.getFunKey() + ":result";
            redisHandler.setSync(cacheKey, funResult);
        } catch (Exception e) {
            logger.error("functionEvent 事件处理报错: {} ", JsonUtils.getJsonString(funResult), e);
        }
    }

}
