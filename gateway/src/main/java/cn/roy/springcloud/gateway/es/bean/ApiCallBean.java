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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
