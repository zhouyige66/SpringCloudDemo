package cn.roy.session.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/3/31 19:03
 * @Version: v1.0
 */
@Service
public class PrintServiceImpl implements PrintService {
    private static final Logger logger = LoggerFactory.getLogger(PrintServiceImpl.class);

    @Override
    public void print(String msg) {
        logger.info(msg);
    }

}
