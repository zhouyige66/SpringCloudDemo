package cn.roy.springcloud.gateway.es.model;

import java.util.Date;
import java.util.UUID;

/**
 * @Description: ES存储实体基类
 * @Author: Roy Z
 * @Date: 2019-09-17 13:26
 * @Version: v1.0
 */
public class ESBaseModel {
    private String id = UUID.randomUUID().toString();
    private Date createDate;
    private Date updateDate;
    private Boolean del;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getDel() {
        return del;
    }

    public void setDel(Boolean del) {
        this.del = del;
    }
}
