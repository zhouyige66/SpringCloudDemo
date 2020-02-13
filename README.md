# SpringCloudDemo，一个综合Spring Cloud各组件的综合Demo
## 项目结构说明
* base
    1. api返回实体基类
    2. 包含mybatis generator插件
    3. yml共用配置
    4. pom公用模块
* base-config
    1. 微服务配置中心
    2. 包含配置中心安全配置
* base-eureka
    1. 服务注册中心
    2. 包含agent代码替换功能测试
* base-gateway
    1. 服务网关
    2. 集成shiro,redis
    3. 提供接口调用统计样板
* java-agent-demo
    1. javassist动态编程测试
    2. 提供测试同一微服务部署多实例，session重置导致重复登录问题。
* service-api
    1. 动态数据源
    2. 服务熔断与降级
    3. FeignClient
    4. swagger资源导入生成word接口文档
    5. ActiveMQ消费端
* service-api2
    1. RabbitMQ消费端
    2. Docx4J测试
    3. POI测试
* service-job
    1. Elastic Job分布式任务
* service-mq
    1. RabbitMQ生成者
    2. AcitveMQ生成者
* service-multiple-cache
    1. 多级缓存实现Redis+EhCache
* web-test
    1. 前后端分离测试
    2. Nginx负载测试，部署多套前段项目
## 关于作者
* Email： <751664206@qq.com>
* 有任何建议或者使用中遇到问题都可以给我发邮件。
