package cn.roy.springcloud.api.service;

import cn.roy.springcloud.api.entity.ResultData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Description: swagger数据导入service
 * @Author: Roy Z
 * @Date: 2019-09-05 18:39
 * @Version: v1.0
 */
public interface SwaggerDataDealService {

    ResultData importApiFromFile(String filePath) throws IOException;

    ResultData importApiFromUrl(String swaggerUrl) throws URISyntaxException, IOException;

    ResultData exportApiDoc(String filePath);

    ResultData exportApiDoc(List<String> apiNameList);

    ResultData cleanData();

}
