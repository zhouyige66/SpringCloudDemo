package cn.roy.springcloud.gateway;

import cn.roy.springcloud.gateway.filter.PostFilter;
import cn.roy.springcloud.gateway.filter.PreFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@EnableSwagger2
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public PreFilter preFilter() {
        return new PreFilter();
    }

    @Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }

    @Bean(name = "mainCacheManager")
    @Primary
    public CacheManager multiCacheManager(){
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        return compositeCacheManager;
    }

    @Component
    @Primary
    class DocumentationConfig implements SwaggerResourcesProvider {
        private final RouteLocator routeLocator;

        public DocumentationConfig(RouteLocator routeLocator) {
            this.routeLocator = routeLocator;
        }

        @Override
        public List<SwaggerResource> get() {
            List<SwaggerResource> resources = new ArrayList<>();
            List<Route> routes = routeLocator.getRoutes();
            for (Route route:routes) {
                resources.add(swaggerResource(route.getId(),
                        route.getFullPath().replace("**", "v2/api-docs")));
            }
            return resources;
        }

        private SwaggerResource swaggerResource(String name, String location) {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName(name);
            swaggerResource.setLocation(location);
            swaggerResource.setSwaggerVersion("2.0");
            return swaggerResource;
        }
    }

}
