package util.concurrent.gofun;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import util.concurrent.gofun.core.ExceptionHandlingAsyncTaskExecutor;
import util.concurrent.gofun.core.FunParams;
import util.concurrent.gofun.core.GoFunExecuteResultEvent;
import util.concurrent.gofun.core.GoFunResult;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public class GoFun {

    private static final int poolSize = 20;
    private static final Executor funExecutor;

    static {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(2 * poolSize);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("GoFun");

        // ThreadPoolTaskExecutor 不会自动创建ThreadPoolExecutor需要手动调initialize才会创建
        // 如果@Bean 就不需手动，会自动InitializingBean的afterPropertiesSet来调initialize
        // 参考：ThreadPoolExecutor， ThreadPoolTaskExecutor 和 AsyncListenableTaskExecutor 的区别：
        // http://www.manongjc.com/detail/25-fpqudkswujcxmkw.html
        executor.initialize();

        funExecutor = (Executor)new ExceptionHandlingAsyncTaskExecutor((AsyncTaskExecutor)executor);
    }

    /**
     * 异步执行一个没有返回值的方法.
     *
     * @param outParams .
     * @param fun .
     */
    public static <R> void go(FunParams outParams, String funKey, Function<FunParams, R> fun) {
        final WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();

        funExecutor.execute(() -> {
            long beginTime = System.currentTimeMillis();
            GoFunResult eventResult = new GoFunResult();
            eventResult.setFunKey(funKey);
            eventResult.setExecuteTime(beginTime);

            R result = null;
            try {
                result = fun.apply(outParams);
            } catch (Exception e) {
                eventResult.setErrMsg(e.getMessage());
                eventResult.setErrClassName(e.getClass().getName());
            }

            long endTime = System.currentTimeMillis();
            eventResult.setFinishTime(endTime);
            eventResult.setResult(result);

            wac.publishEvent(new GoFunExecuteResultEvent(fun, eventResult));
        });
    }
}
