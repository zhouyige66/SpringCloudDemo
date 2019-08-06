package cn.roy.springcloud.api.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-06 09:52
 * @Version: v1.0
 */
@Component
@ConfigurationProperties(prefix = "spring")
public class SlaveDatasourcePropertyContainer {

    private List<Map<String,Properties>> datasource;

    public List<Map<String, Properties>> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<Map<String, Properties>> datasource) {
        this.datasource = datasource;
    }
}

