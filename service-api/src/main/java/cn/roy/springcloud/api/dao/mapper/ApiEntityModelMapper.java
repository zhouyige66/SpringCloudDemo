package cn.roy.springcloud.api.dao.mapper;

import cn.roy.springcloud.api.dao.model.ApiEntityModel;
import cn.roy.springcloud.api.dao.model.ApiEntityModelQuery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiEntityModelMapper {
    long countByQuery(ApiEntityModelQuery example);

    int deleteByQuery(ApiEntityModelQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiEntityModel record);

    int insertSelective(ApiEntityModel record);

    List<ApiEntityModel> selectByQuery(ApiEntityModelQuery example);

    ApiEntityModel selectByPrimaryKey(Long id);

    int updateByQuerySelective(@Param("record") ApiEntityModel record, @Param("example") ApiEntityModelQuery example);

    int updateByQuery(@Param("record") ApiEntityModel record, @Param("example") ApiEntityModelQuery example);

    int updateByPrimaryKeySelective(ApiEntityModel record);

    int updateByPrimaryKey(ApiEntityModel record);
}