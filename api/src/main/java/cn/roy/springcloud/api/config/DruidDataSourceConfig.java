package cn.roy.springcloud.api.config;

import cn.roy.springcloud.api.datasource.DynamicDataSource;
import cn.roy.springcloud.api.datasource.DynamicDataSourceTransactionManager;
import cn.roy.springcloud.api.datasource.SlaveDatasource;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @Description: Druid配置
 * <p>
 * 凡是被Spring管理的类，实现接口 EnvironmentAware 重写方法 setEnvironment 可以在工程启动时，
 * 获取到系统环境变量和application配置文件中的变量。 还有一种方式是采用注解的方式获取 @value("${变量的key值}")
 * 获取application配置文件中的变量。 这里采用第一种要方便些
 * @Author: Roy Z
 * @Date: 2019-08-05 14:48
 * @Version: v1.0
 */
@Configuration
@EnableTransactionManagement
public class DruidDataSourceConfig implements EnvironmentAware {

    public void setEnvironment(Environment env) {

    }

    @Bean(name = "master")
    @Primary
    @ConfigurationProperties(prefix = "datasource.master")
    public DataSource master() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }

    @Bean(name = "slave")
    @ConfigurationProperties(prefix = "datasource")
    public SlaveDatasource slave() {
        SlaveDatasource slaveDatasource = new SlaveDatasource();
        return slaveDatasource;
    }

    @Bean
    public DynamicDataSource dynamicDataSource(@Qualifier("master") DataSource masterDataSource,
                                        @Qualifier("slave") SlaveDatasource slaveDatasource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDatasource = new HashedMap<>();
        targetDatasource.put(DynamicDataSource.DynamicDataSourceType.getMasterType(), masterDataSource);
        List<DruidDataSource> slave = slaveDatasource.getSlave();
        if (!CollectionUtils.isEmpty(slave)) {
            int i = 0;
            for (DruidDataSource dataSource : slave) {
                targetDatasource.put(DynamicDataSource.DynamicDataSourceType.getSlaveType(i), dataSource);
                i++;
            }
        }
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        dynamicDataSource.setTargetDataSources(targetDatasource);
        return dynamicDataSource;
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) throws Exception {
        return new DynamicDataSourceTransactionManager(dataSource);
    }

//    @Bean
//    public ServletRegistrationBean druidServlet() {
//        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
//        servletRegistrationBean.setServlet(new StatViewServlet());
//        servletRegistrationBean.addUrlMappings("/druid/*");
//        Map<String, String> initParameters = new HashMap<String, String>();
//        // initParameters.put("loginUsername", "druid");// 用户名
//        // initParameters.put("loginPassword", "druid");// 密码
//        initParameters.put("resetEnable", "false");// 禁用HTML页面上的“Reset All”功能
//        initParameters.put("allow", "127.0.0.1"); // IP白名单 (没有配置或者为空，则允许所有访问)
//        // initParameters.put("deny", "192.168.20.38");// IP黑名单
//        // (存在共同时，deny优先于allow)
//        servletRegistrationBean.setInitParameters(initParameters);
//        return servletRegistrationBean;
//    }
//
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(new WebStatFilter());
//        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
//        return filterRegistrationBean;
//    }
//
//    // 按照BeanId来拦截配置 用来bean的监控
//    @Bean(value = "druid-stat-interceptor")
//    public DruidStatInterceptor DruidStatInterceptor() {
//        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
//        return druidStatInterceptor;
//    }
//
//    @Bean
//    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
//        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
//        beanNameAutoProxyCreator.setProxyTargetClass(true);
//        // 设置要监控的bean的id
//        //beanNameAutoProxyCreator.setBeanNames("sysRoleMapper","loginController");
//        beanNameAutoProxyCreator.setInterceptorNames("druid-stat-interceptor");
//        return beanNameAutoProxyCreator;
//    }

}