package cn.roy.springcloud.api.dao.model;

import java.io.Serializable;
import java.util.Date;

public class ApiStatisticsModel implements Serializable {
    private Long id;

    private Long apiId;

    private Long costTime;

    private Boolean delete;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public ApiStatisticsModel withId(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiId() {
        return apiId;
    }

    public ApiStatisticsModel withApiId(Long apiId) {
        this.setApiId(apiId);
        return this;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getCostTime() {
        return costTime;
    }

    public ApiStatisticsModel withCostTime(Long costTime) {
        this.setCostTime(costTime);
        return this;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public Boolean getDelete() {
        return delete;
    }

    public ApiStatisticsModel withDelete(Boolean delete) {
        this.setDelete(delete);
        return this;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ApiStatisticsModel withCreateTime(Date createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public ApiStatisticsModel withUpdateTime(Date updateTime) {
        this.setUpdateTime(updateTime);
        return this;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", apiId=").append(apiId);
        sb.append(", costTime=").append(costTime);
        sb.append(", delete=").append(delete);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}