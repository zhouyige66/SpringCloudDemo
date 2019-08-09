package cn.roy.springcloud.api.datasource;

/**
 * @Description: 线程绑定的动态数据源
 * @Author: Roy Z
 * @Date: 2019-08-05 15:58
 * @Version: v1.0
 */
public final class DynamicDataSourceHolder {
    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    private DynamicDataSourceHolder() {

    }

    public static void putDynamicDataSourceType(String  dataSourceType) {
        holder.set(dataSourceType);
    }

    public static String getDynamicDataSourceType() {
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }

}
