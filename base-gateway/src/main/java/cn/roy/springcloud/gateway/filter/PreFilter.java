package cn.roy.springcloud.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description: 过滤器
 * @Author: Roy Z
 * @Date: 2019-04-25 15:04
 * @Version: v1.0
 */
public class PreFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(PreFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Date time = new Date();
        RequestHolder.putStartDate(time);
        log.info("请求开始，请求ID：{}，开始时间：{}", request.hashCode(), time.getTime());
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        // 统一header处理
        ctx.addZuulRequestHeader("baseInfo","kk20");
        return null;
    }

}
