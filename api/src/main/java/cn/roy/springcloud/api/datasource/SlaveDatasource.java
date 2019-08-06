package cn.roy.springcloud.api.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-06 09:52
 * @Version: v1.0
 */
@Component
@ConfigurationProperties(prefix = "slave")
public class SlaveDatasource {

    List<DataSource> dataSourceList;

    public List<DataSource> getDataSourceList() {
        return dataSourceList;
    }

    public void setDataSourceList(List<DataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }
}
