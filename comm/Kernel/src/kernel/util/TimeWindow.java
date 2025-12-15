package kernel.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("unchecked")
public class TimeWindow implements Runnable {
	private static Log logger = LogFactory.getLog(TimeWindow.class.getName());

	private TimeWindow instance = null;

	private boolean stop = false;

	protected int timeSize = 600; // 600s

	protected HashMap[] cache = null;

	protected byte[] cacheLock = new byte[1];

	private String name = "TimeWindow";

	private void init() {
		cache = new HashMap[timeSize];

		for (int i = 0; i < cache.length; i++) {
			cache[i] = new HashMap();
		}
	}

	public void start() {
		if (instance == null) {
			instance = this;
			this.init();
			if (name == null || name.trim().length() == 0)
				name = "TimeWindow";
			new Thread(this, name).start();
		}
	}

	public void stop() {
		if (instance != null) {
			instance = null;
			cache = null;
			this.setStop(true);
		}
	}

	public void run() {
		while (!stop) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			synchronized (cacheLock) { // 030316 cacheLock
				if (cache != null) {
					for (int i = 0; i < timeSize - 1; i++) {
						cache[i] = cache[i + 1];
					}

					HashMap m = new HashMap();
					cache[timeSize - 1] = m;
				}
			}
		}

		logger.warn("Time window stop.");
	}

	public int find(Object key) {
		synchronized (cacheLock) {
			for (int i = 0; cache != null && i < timeSize; i++) {
				HashMap m = (HashMap) cache[i];
				if (m.containsKey(key))
					return i;
			}
		}

		return -1;
	}

	public Object findObject(Object key) {
		synchronized (cacheLock) {
			for (int i = 0; cache != null && i < timeSize; i++) {
				HashMap m = (HashMap) cache[i];
				if (m.containsKey(key)) {
					Object object = m.get(key);
					return object;
				}
			}
		}
		return null;
	}

	public Map getObjectAll() {
		HashMap map = new HashMap();
		synchronized (cacheLock) {
			for (int i = 0; cache != null && i < timeSize; i++) {
				HashMap m = (HashMap) cache[i];
				map.putAll(m);
			}
		}

		return map;
	}

	public boolean add(Object key, Object o) {
		this.remove(key);
		return add(key, o, new Date());
	}

	public void remove(Object key) {
		synchronized (cacheLock) {
			for (int i = 0; cache != null && i < timeSize; i++) {
				HashMap m = (HashMap) cache[i];
				if (m.containsKey(key)) {
					m.remove(key);
				}
			}
		}
	}

	public void remove(Object key, int index) {
		synchronized (cacheLock) {
			if (cache != null)
				cache[index].remove(key);
		}
	}

	public boolean remove(Object key, Date d) {

		if (d == null)
			d = new Date();

		int index = (int) ((d.getTime() / 1000) % timeSize);

		synchronized (cacheLock) {
			if (cache != null)
				cache[index].remove(key);
		}

		return true;

	}

	public boolean add(Object key, Object o, Date d) {
		if (d == null)
			d = new Date();
		// 随机
		// int index = (int) ((d.getTime() / 1000) % timeSize);
		
		int index = timeSize - 1;
		if(d.before(new Date())) {//d<当前时间
			index = index - (int) ((new Date().getTime()-d.getTime() )/ 1000);
			if(index<=0) return false;
		}
		synchronized (cacheLock) {
			if (cache != null)
				cache[index].put(key, o);
		}

		return true;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public int getTimeSize() {
		return timeSize;
	}

	public void setTimeSize(int timeSize) {
		this.timeSize = timeSize;
	}

	public int size() {
		int size = 0;

		synchronized (cacheLock) {
			for (int i = 0; cache != null && i < timeSize; i++) {
				HashMap m = cache[i];
				if (m != null)
					size += m.size();
			}
		}

		return size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
