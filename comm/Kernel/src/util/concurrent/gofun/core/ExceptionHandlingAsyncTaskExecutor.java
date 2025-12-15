package util.concurrent.gofun.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.lang.Nullable;

/**
 * 一个线程池类.
 *
 */
public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor.class);

    /**
     * 原始线程池对象.
     */
    private final AsyncTaskExecutor executor;

    public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    /**
     * 执行任务.
     *
     * @param task the {@code Runnable} to execute (never {@code null})
     */
    public void execute(@Nullable Runnable task) {
        this.executor.execute(createWrappedRunnable(task));
    }


    /**
     * 执行任务.
     * @param task task the {@code Runnable} to execute (never {@code null})
     * @param startTimeout the time duration (milliseconds) within which the task is
     *                     supposed to start. This is intended as a hint to the executor, allowing for
     *                     preferred handling of immediate tasks. Typical values are {@link #TIMEOUT_IMMEDIATE}
     *                     or {@link #TIMEOUT_INDEFINITE} (the default as used by {@link #execute(Runnable)}).
     */
    public void execute(@Nullable Runnable task, long startTimeout) {
        this.executor.execute(createWrappedRunnable(task), startTimeout);
    }

    /**
     * 基于 callable 对象创建一个包装的 callable 对象，并可以在主线程接收其抛出的异常.
     * @param task .
     * @return
     */
    private <T> Callable<T> createCallable(Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                handle(e);
                throw e;
            }
        };
    }

    /**
     * 基于 Runnable 对象创建一个包装的 Runnable 对象，并可以在主线程接收其抛出的异常.
     *
     * @param task .
     * @return
     */
    private Runnable createWrappedRunnable(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                handle(e);
            }
        };
    }

    /**
     * 处理异常.
     *
     * @param e .
     */
    private void handle(Exception e) {
        log.error("Caught async exception", e);
    }

    /**
     * 提交任务.
     *
     * @param task the {@code Runnable} to execute (never {@code null})
     * @return
     */
    public Future<?> submit(@Nullable Runnable task) {
        return this.executor.submit(createWrappedRunnable(task));
    }

    /**
     * 提交任务.
     *
     * @param task the {@code Callable} to execute (never {@code null})
     * @return
     */
    public <T> Future<T> submit(@Nullable Callable<T> task) {
        return this.executor.submit(createCallable(task));
    }

    /**
     * 结束任务.
     *
     * @throws Exception Exception
     */
    public void destroy() throws Exception {
        if (this.executor instanceof DisposableBean) {
            DisposableBean bean = (DisposableBean)this.executor;
            bean.destroy();
        }
    }

    /**
     * bean 初始化.
     *
     * @throws Exception Exception
     */
    public void afterPropertiesSet() throws Exception {
        log.info("---------> ExceptionHandlingAsyncTaskExecutor.afterPropertiesSet 执行...");
        if (this.executor instanceof InitializingBean) {
            InitializingBean bean = (InitializingBean)this.executor;
            bean.afterPropertiesSet();
        }
    }
}
