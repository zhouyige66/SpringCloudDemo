package cn.roy.springcloud.api.entity;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-09-06 16:03
 * @Version: v1.0
 */
public class ParameterEntity {
    /**
     * description : 工单编码
     * name : workRequestId
     * required : true
     * type : string
     */

    private String description;
    private String name;
    private boolean required;
    private String type;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
