package cn.roy.springcloud.api;

import cn.roy.springcloud.api.datasource.DynamicDataSource;
import cn.roy.springcloud.api.datasource.SlaveDatasourcePropertyContainer;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.ibatis.annotations.Property;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


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
@EnableConfigurationProperties({SlaveDatasourcePropertyContainer.class})
public class DruidDataSourceConfig implements EnvironmentAware {
    @Autowired
    SlaveDatasourcePropertyContainer slaveDatasourcePropertyContainer;

    public void setEnvironment(Environment env) {

    }

    @Bean
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        Map<Object, Object> targetDatasource = new HashedMap<>();
        if (!CollectionUtils.isEmpty(slaveDatasourcePropertyContainer.getDatasource())) {
            int i = 0;
            for (Map<String, Properties> map : slaveDatasourcePropertyContainer.getDatasource()) {
                Object[] keys = map.keySet().toArray();
                String key = (String) keys[0];
                Properties properties = map.get(key);
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.configFromPropety(properties);
                if (key.equals("master")) {
                    dynamicDataSource.setDefaultTargetDataSource(dataSource);
                }
                targetDatasource.put(key, dataSource);
                i++;
            }
        }
        dynamicDataSource.setTargetDataSources(targetDatasource);
        return dynamicDataSource;
    }

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");
        Map<String, String> initParameters = new HashMap<String, String>();
        // initParameters.put("loginUsername", "druid");// 用户名
        // initParameters.put("loginPassword", "druid");// 密码
        initParameters.put("resetEnable", "false");// 禁用HTML页面上的“Reset All”功能
        initParameters.put("allow", "127.0.0.1"); // IP白名单 (没有配置或者为空，则允许所有访问)
        // initParameters.put("deny", "192.168.20.38");// IP黑名单
        // (存在共同时，deny优先于allow)
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    // 按照BeanId来拦截配置 用来bean的监控
    @Bean(value = "druid-stat-interceptor")
    public DruidStatInterceptor DruidStatInterceptor() {
        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
        return druidStatInterceptor;
    }

    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setProxyTargetClass(true);
        // 设置要监控的bean的id
        //beanNameAutoProxyCreator.setBeanNames("sysRoleMapper","loginController");
        beanNameAutoProxyCreator.setInterceptorNames("druid-stat-interceptor");
        return beanNameAutoProxyCreator;
    }

}