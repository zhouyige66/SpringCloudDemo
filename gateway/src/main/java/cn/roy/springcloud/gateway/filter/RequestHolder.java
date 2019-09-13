package cn.roy.springcloud.gateway.filter;

import java.util.Date;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-09-03 13:32
 * @Version: v1.0
 */
public class RequestHolder {
    private static final ThreadLocal<Date> holder = new ThreadLocal<>();

    private RequestHolder() {

    }

    public static void putStartDate(Date date) {
        holder.set(date);
    }

    public static Date getStartDate() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }

}
