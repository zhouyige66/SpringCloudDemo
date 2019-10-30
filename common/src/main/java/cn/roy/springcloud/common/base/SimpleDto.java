package cn.roy.springcloud.common.base;

import java.io.Serializable;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/10/29 13:31
 * @Version: v1.0
 */
public class SimpleDto extends BaseDto{
    private Serializable value;

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

}
