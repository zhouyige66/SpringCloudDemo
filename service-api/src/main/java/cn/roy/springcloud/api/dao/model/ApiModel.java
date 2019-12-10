package cn.roy.springcloud.api.dao.model;

import java.io.Serializable;
import java.util.Date;

public class ApiModel implements Serializable {
    private Long id;

    private String name;

    private String controller;

    private String description;

    private String route;

    private String method;

    private String parameterids;

    private Long body;

    private Boolean del;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public ApiModel withId(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ApiModel withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getController() {
        return controller;
    }

    public ApiModel withController(String controller) {
        this.setController(controller);
        return this;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getDescription() {
        return description;
    }

    public ApiModel withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoute() {
        return route;
    }

    public ApiModel withRoute(String route) {
        this.setRoute(route);
        return this;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getMethod() {
        return method;
    }

    public ApiModel withMethod(String method) {
        this.setMethod(method);
        return this;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParameterids() {
        return parameterids;
    }

    public ApiModel withParameterids(String parameterids) {
        this.setParameterids(parameterids);
        return this;
    }

    public void setParameterids(String parameterids) {
        this.parameterids = parameterids;
    }

    public Long getBody() {
        return body;
    }

    public ApiModel withBody(Long body) {
        this.setBody(body);
        return this;
    }

    public void setBody(Long body) {
        this.body = body;
    }

    public Boolean getDel() {
        return del;
    }

    public ApiModel withDel(Boolean del) {
        this.setDel(del);
        return this;
    }

    public void setDel(Boolean del) {
        this.del = del;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ApiModel withCreateTime(Date createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public ApiModel withUpdateTime(Date updateTime) {
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
        sb.append(", name=").append(name);
        sb.append(", controller=").append(controller);
        sb.append(", description=").append(description);
        sb.append(", route=").append(route);
        sb.append(", method=").append(method);
        sb.append(", parameterids=").append(parameterids);
        sb.append(", body=").append(body);
        sb.append(", del=").append(del);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}