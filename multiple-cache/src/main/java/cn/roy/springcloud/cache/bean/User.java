package cn.roy.springcloud.cache.bean;

import java.io.Serializable;

/**
 * @Description: 用户
 * @Author: Roy Z
 * @Date: 2019-11-21 17:32
 * @Version: v1.0
 */
public final class User implements Serializable {
    private static final long serialVersionUID = 1906647446112495707L;

    Integer id;
    String name;

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
