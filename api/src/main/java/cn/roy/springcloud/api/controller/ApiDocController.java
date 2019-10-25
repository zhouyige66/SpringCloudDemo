package cn.roy.springcloud.api.controller;

import cn.roy.springcloud.api.service.SwaggerDataDealService;
import cn.roy.springcloud.common.http.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Description: 接口文档controller
 * @Author: Roy Z
 * @Date: 2019-09-06 19:11
 * @Version: v1.0
 */
@RestController
@RequestMapping(value = "api")
@Api(tags = "ApiDoc导入导出接口")
public class ApiDocController {

    @Autowired
    private SwaggerDataDealService swaggerDataDealService;

    @GetMapping("importApiFromFile")
    @ApiOperation(value = "导入API数据", notes = "功能：从包含项目所有接口数据文件中获取API数据，存入数据库")
    @ApiImplicitParam(name = "filePath", value = "包含项目所有接口数据的JSON格式的文件路径", required = true,
            type = "String", example = "c:/api-docs.json")
    public ResultData importApiFromFile(@RequestParam String filePath) throws IOException {
        return swaggerDataDealService.importApiFromFile(filePath);
    }

    @GetMapping("importApiFromUrl")
    @ApiOperation(value = "导入API数据", notes = "功能：从url获取API数据，存入数据库")
    @ApiImplicitParam(name = "swaggerUrl", value = "可获取项目的所有接口JSON格式的URL", required = true,
            type = "String", example = "http://localhost:8080/v2/api-docs")
    public ResultData importApiFromUrl(@RequestParam String swaggerUrl) throws IOException, URISyntaxException {
        return swaggerDataDealService.importApiFromUrl(swaggerUrl);
    }

    @GetMapping("exportApiDocWithFile")
    @ApiOperation(value = "导出API数据", notes = "功能：根据上传的文件，解析出需要导出的接口，导出接口相关数据")
    @ApiImplicitParam(name = "filePath", value = "包含需要导出相关数据的接口列表Excel文件", required = true,
            type = "String", example = "C:\\Users\\Roy Z Zhou\\Desktop\\接口列表.xlsx")
    public ResultData exportApiDoc(@RequestParam String filePath) {
        return swaggerDataDealService.exportApiDoc(filePath);
    }

    @PostMapping("exportApiDocWithName")
    @ApiOperation(value = "导出API数据", notes = "功能：导出指定接口的相关数据")
    public ResultData exportApiDoc(@ApiParam(name = "apiNameList", value = "包含需要导出相关数据的接口列表")
                                   @RequestBody List<String> apiNameList) {
        return swaggerDataDealService.exportApiDoc(apiNameList);
    }

    @GetMapping("cleanData")
    @ApiOperation(value = "清空数据表数据", notes = "功能：清空api,parameter,entity表")
    public ResultData cleanData() {
        return swaggerDataDealService.cleanData();
    }

}
