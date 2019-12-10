package cn.roy.springcloud.gateway.config;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/22 10:17
 * @Version: v1.0
 */
@Component
@Primary
public class SwaggerConfig implements SwaggerResourcesProvider {
    private final RouteLocator routeLocator;

    public SwaggerConfig(RouteLocator routeLocator) {
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
