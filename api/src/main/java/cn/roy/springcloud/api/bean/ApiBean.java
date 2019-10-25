package cn.roy.springcloud.api.bean;

import java.util.List;

/**
 * @Description: 接口相关实体
 * @Author: Roy Z
 * @Date: 2019-09-06 15:33
 * @Version: v1.0
 */
public class ApiBean {

    /**
     * controller : core-role-controller
     * description : 查询当前登录用户的菜单
     * method : get
     * name : /role/getEmployeeMenu
     * parameterids : []
     * body :
     */

    private String controller;
    private String description;
    private String method;
    private String name;
    private List<ParameterBean> parameterBeanList;
    private String bodyDes;
    private String body;

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParameterBean> getParameterBeanList() {
        return parameterBeanList;
    }

    public void setParameterBeanList(List<ParameterBean> parameterBeanList) {
        this.parameterBeanList = parameterBeanList;
    }

    public String getBodyDes() {
        return bodyDes;
    }

    public void setBodyDes(String bodyDes) {
        this.bodyDes = bodyDes;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
