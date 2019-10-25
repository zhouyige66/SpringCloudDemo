package cn.roy.springcloud.api.http;

import cn.roy.springcloud.common.base.BaseDto;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-10-25 11:03
 * @Version: v1.0
 */
public class TestDto extends BaseDto {
    private String name;

    public TestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
