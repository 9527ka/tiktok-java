package server;

/**
 * Server生命周期服务，在应用启动和停止时调用
 */
public interface Server extends Comparable<Server>{
	public void start();
	public void stop();
	public void pause();
	public void restart();
	public boolean isRunning();
}
