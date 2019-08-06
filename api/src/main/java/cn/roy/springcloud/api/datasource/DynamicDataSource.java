package cn.roy.springcloud.api.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Description: 多数据源
 * @Author: Roy Z
 * @Date: 2019-08-05 15:52
 * @Version: v1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String dynamicDataSourceType = DynamicDataSourceHolder.getDynamicDataSourceType();
        if (dynamicDataSourceType == null) {
            dynamicDataSourceType = DynamicDataSourceType.MASTER.type;
        }

        return dynamicDataSourceType;
    }

    public enum DynamicDataSourceType {
        MASTER("master"), SLAVE("slave");

        private String type;

        DynamicDataSourceType(String type) {
            this.type = type;
        }

        public static String getMasterType() {
            return MASTER.type;
        }

        public static String getSlaveType(int order) {
            return SLAVE.type + order;
        }
    }

}
