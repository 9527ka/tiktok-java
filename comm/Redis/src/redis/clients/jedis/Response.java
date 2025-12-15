package redis.clients.jedis;

import redis.clients.jedis.exceptions.JedisDataException;
public class Response<T> {
    protected T response = null;
    private boolean built = false;
    private boolean set = false;
    private Builder<T> builder;
    private Object data;

    public Response(Builder<T> b) {
        this.builder = b;
    }

    public void set(Object data) {
        this.data = data;
        set = true;
    }

    public T get() {
        if (!set) {
            throw new JedisDataException(
                    "Please close pipeline or multi block before calling this method.");
        }
        if (!built) {
            //  数据为空是直接返回null，避免 builder.build(data)产生空指针异常
            if(data == null) {
                return null;
            }
            response = builder.build(data);
            this.data = null;
            built = true;
        }
        return response;
    }

    public String toString() {
        return "Response " + builder.toString();
    }

}
