package cn.roy.springcloud.eureka;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-12-27 17:22
 * @Version: v1.0
 */
public class User {

    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void print() {
        System.out.println("用户名：" + name);
    }

    public void print(String prefix) {
        System.out.println(prefix + name);
    }
}
