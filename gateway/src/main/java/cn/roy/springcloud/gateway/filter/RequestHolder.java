package cn.roy.springcloud.gateway.filter;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-09-03 13:32
 * @Version: v1.0
 */
public class RequestHolder {
    private static final ThreadLocal<Long> holder = new ThreadLocal<>();

    private RequestHolder() {

    }

    public static void putStartTimestamp(Long  timestamp) {
        holder.set(timestamp);
    }

    public static Long getStartTimestamp() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }

}
