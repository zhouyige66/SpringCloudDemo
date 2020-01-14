package cn.roy.springcloud.gateway.model;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020-01-10 10:01
 * @Version: v1.0
 */
public class User {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
