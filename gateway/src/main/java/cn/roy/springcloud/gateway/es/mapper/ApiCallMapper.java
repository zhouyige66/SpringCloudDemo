package cn.roy.springcloud.gateway.es.mapper;

import cn.roy.springcloud.gateway.es.bean.ApiCallBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @Description: api调用统计
 * @Author: Roy Z
 * @Date: 2019-09-13 15:27
 * @Version: v1.0
 */
@Component
public interface ApiCallMapper extends ElasticsearchRepository<ApiCallBean, Long> {

    ApiCallBean queryEmployeeById(Long id);

}
