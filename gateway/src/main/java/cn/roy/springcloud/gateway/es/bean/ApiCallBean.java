package cn.roy.springcloud.gateway.es.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.UUID;

/**
 * @Description: Api调用
 * @Author: Roy Z
 * @Date: 2019-09-13 15:21
 * @Version: v1.0
 */
@Document(indexName = "ApiCall", type = "doc")
public class ApiCallBean {
    @Id
    private String id = UUID.randomUUID().toString();// id必须

    private String name;
    private Date startTime;
    private Date endTime;
    private Long costTime;
    private String remoteIp;

}
