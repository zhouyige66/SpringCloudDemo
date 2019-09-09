package cn.roy.springcloud.api.entity;

/**
 * @Description: 参数
 * @Author: Roy Z
 * @Date: 2019-09-05 19:33
 * @Version: v1.0
 */
public class Parame {
    private String name;
    private String in;
    private String description;
    private boolean required;
    private String type;
    private Long entityId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
