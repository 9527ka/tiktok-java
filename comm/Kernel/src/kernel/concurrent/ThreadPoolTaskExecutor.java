package kernel.concurrent;


public class ThreadPoolTaskExecutor extends org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor {

	private static final long serialVersionUID = 119645098645321188L;

	@Override
    public void execute(Runnable runnable) {
        super.execute(runnable);
    }
    
    
}
