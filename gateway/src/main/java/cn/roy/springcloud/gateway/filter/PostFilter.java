package cn.roy.springcloud.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 过滤器
 * @Author: Roy Z
 * @Date: 2019-09-03 09:21
 * @Version: v1.0
 */
public class PostFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(PostFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        long endTime = System.currentTimeMillis();
        long startTime = RequestHolder.getStartTimestamp();
        long time = endTime - startTime;
        log.info("请求结束，请求ID：{}，开始时间：{}，结束时间：{}，请求耗时{}", request.hashCode(), startTime, endTime, time);
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        return null;
    }

}