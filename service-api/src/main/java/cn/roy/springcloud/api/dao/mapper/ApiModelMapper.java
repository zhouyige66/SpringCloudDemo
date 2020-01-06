package cn.roy.springcloud.api.dao.mapper;

import cn.roy.springcloud.api.dao.model.ApiModel;
import cn.roy.springcloud.api.dao.model.ApiModelQuery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiModelMapper {
    long countByQuery(ApiModelQuery example);

    int deleteByQuery(ApiModelQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiModel record);

    int insertSelective(ApiModel record);

    List<ApiModel> selectByQuery(ApiModelQuery example);

    ApiModel selectByPrimaryKey(Long id);

    int updateByQuerySelective(@Param("record") ApiModel record, @Param("example") ApiModelQuery example);

    int updateByQuery(@Param("record") ApiModel record, @Param("example") ApiModelQuery example);

    int updateByPrimaryKeySelective(ApiModel record);

    int updateByPrimaryKey(ApiModel record);
}