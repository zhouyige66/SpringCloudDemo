package cn.roy.springcloud.api.service;

import cn.roy.springcloud.api.dao.bean.Processor;

import java.util.List;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-08 15:55
 * @Version: v1.0
 */
public interface ProcessorService {

    List<Processor> compare();

    int compareAndSwap();

}
