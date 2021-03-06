package cn.roy.springcloud.job.job;

import cn.roy.springcloud.job.annotation.SimpleJobHandler;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/8 13:38
 * @Version: v1.0
 */
@SimpleJobHandler(jobName = "CJob", jobParameter = "C任务参数",
        cron = "0 0/2 * * * ?",
        shardingTotalCount = 3,
        shardingItemParameters = "0=A,1=B,2=C")
public class CJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(CJob.class);

    @Override
    public void execute(ShardingContext shardingContext) {
        logger.info("C定时任务执行了，传递的参数：" + shardingContext);
    }
}
