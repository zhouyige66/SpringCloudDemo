package cn.roy.springcloud.api.dao.mapper;

import cn.roy.springcloud.api.dao.model.ApiParameterModel;
import cn.roy.springcloud.api.dao.model.ApiParameterModelQuery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiParameterModelMapper {
    long countByQuery(ApiParameterModelQuery example);

    int deleteByQuery(ApiParameterModelQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiParameterModel record);

    int insertSelective(ApiParameterModel record);

    List<ApiParameterModel> selectByQuery(ApiParameterModelQuery example);

    ApiParameterModel selectByPrimaryKey(Long id);

    int updateByQuerySelective(@Param("record") ApiParameterModel record, @Param("example") ApiParameterModelQuery example);

    int updateByQuery(@Param("record") ApiParameterModel record, @Param("example") ApiParameterModelQuery example);

    int updateByPrimaryKeySelective(ApiParameterModel record);

    int updateByPrimaryKey(ApiParameterModel record);
}