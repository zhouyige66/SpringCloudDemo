package cn.roy.springcloud.job.config;

import cn.roy.springcloud.job.annotation.SimpleJobHandler;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/8 09:43
 * @Version: v1.0
 */
@Configuration
public class EJobConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${zookeeper.url}")
    private String zookeeperUrl;

    @Value("${zookeeper.port}")
    private String zookeeperPort;

    @Value("${zookeeper.namespace}")
    private String zookeeperNamespace;

    @Bean
    public CoordinatorRegistryCenter createRegistryCenter() {
        String serverLists = zookeeperUrl.concat(":").concat(zookeeperPort);
        ZookeeperConfiguration configuration = new ZookeeperConfiguration(serverLists, zookeeperNamespace);
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(configuration);
        regCenter.init();

        return regCenter;
    }

    @Bean
    public void config() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(SimpleJobHandler.class);
        if (!beansWithAnnotation.isEmpty()) {
            Set<Map.Entry<String, Object>> entries = beansWithAnnotation.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                Object value = entry.getValue();
                if(!(value instanceof SimpleJob)){
                    continue;
                }

                SimpleJobHandler annotation = AnnotationUtils.findAnnotation(value.getClass(), SimpleJobHandler.class);

                // 定义作业核心配置
                JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder(annotation.jobName(),
                        annotation.cron(), annotation.shardingTotalCount()).build();
                // 定义SIMPLE类型配置
                SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig,
                        value.getClass().getCanonicalName());
                // 定义Lite作业根配置
                LiteJobConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).build();

                // 配置JobScheduler
                JobScheduler jobScheduler = new JobScheduler(createRegistryCenter(), simpleJobRootConfig);
                jobScheduler.init();
            }
        }
    }

}
