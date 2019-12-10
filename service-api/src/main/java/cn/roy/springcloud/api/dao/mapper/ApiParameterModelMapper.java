package cn.roy.springcloud.api.dao.mapper;

import cn.roy.springcloud.api.dao.model.ApiParameterModel;
import cn.roy.springcloud.api.dao.model.ApiParameterModelQuery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiParameterModelMapper {
    long countByExample(ApiParameterModelQuery example);

    int deleteByExample(ApiParameterModelQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiParameterModel record);

    int insertSelective(ApiParameterModel record);

    List<ApiParameterModel> selectByExample(ApiParameterModelQuery example);

    ApiParameterModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ApiParameterModel record, @Param("example") ApiParameterModelQuery example);

    int updateByExample(@Param("record") ApiParameterModel record, @Param("example") ApiParameterModelQuery example);

    int updateByPrimaryKeySelective(ApiParameterModel record);

    int updateByPrimaryKey(ApiParameterModel record);
}