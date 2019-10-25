package cn.roy.springcloud.gateway.filter;

import cn.roy.springcloud.gateway.es.bean.ApiCallBean;
import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @Description: 过滤器
 * @Author: Roy Z
 * @Date: 2019-09-03 09:21
 * @Version: v1.0
 */
public class PostFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(PostFilter.class);

    @Autowired
    private RestClient restClient;

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
        Date endTime = new Date();
        Date startTime = RequestHolder.getStartDate();
        long time = endTime.getTime() - startTime.getTime();
        log.info("请求结束，请求ID：{}，开始时间：{}，结束时间：{}，请求耗时{}", request.hashCode(), startTime, endTime, time);
        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        InputStream responseDataStream = ctx.getResponseDataStream();
        InputStreamReader inputStreamReader = new InputStreamReader(responseDataStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = responseDataStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            String result = baos.toString("UTF-8");
            System.out.println("返回数据：" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 存储到ES
        ApiCallBean apiCallBean = new ApiCallBean();
        apiCallBean.setName(request.getRequestURI());
        apiCallBean.setStartTime(startTime);
        apiCallBean.setEndTime(endTime);
        apiCallBean.setCostTime(time);
        apiCallBean.setRemoteIp(request.getRemoteAddr());

        Request esRequest = new Request("POST", "/api/_doc/");
        esRequest.setJsonEntity(JSON.toJSONString(apiCallBean));
        restClient.performRequestAsync(esRequest, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                log.info("保存成功");
            }

            @Override
            public void onFailure(Exception e) {
                log.info("保存失败");
            }
        });

        RequestHolder.clear();
        return null;
    }

}
