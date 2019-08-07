package cn.roy.springcloud.api.datasource;

import com.alibaba.druid.pool.DruidDataSource;

import java.util.List;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-06 17:35
 * @Version: v1.0
 */
public class SlaveDatasource {

    List<DruidDataSource> slave;

    public List<DruidDataSource> getSlave() {
        return slave;
    }

    public void setSlave(List<DruidDataSource> slave) {
        this.slave = slave;
    }
}
