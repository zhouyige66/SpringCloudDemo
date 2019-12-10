package cn.roy.springcloud.api.dao.mapper;

import cn.roy.springcloud.api.dao.model.ApiEntityModel;
import cn.roy.springcloud.api.dao.model.ApiEntityModelQuery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiEntityModelMapper {
    long countByExample(ApiEntityModelQuery example);

    int deleteByExample(ApiEntityModelQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiEntityModel record);

    int insertSelective(ApiEntityModel record);

    List<ApiEntityModel> selectByExample(ApiEntityModelQuery example);

    ApiEntityModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ApiEntityModel record, @Param("example") ApiEntityModelQuery example);

    int updateByExample(@Param("record") ApiEntityModel record, @Param("example") ApiEntityModelQuery example);

    int updateByPrimaryKeySelective(ApiEntityModel record);

    int updateByPrimaryKey(ApiEntityModel record);
}