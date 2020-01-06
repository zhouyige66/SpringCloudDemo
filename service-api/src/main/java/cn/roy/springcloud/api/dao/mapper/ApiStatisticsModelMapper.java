package cn.roy.springcloud.api.dao.mapper;

import cn.roy.springcloud.api.dao.model.ApiStatisticsModel;
import cn.roy.springcloud.api.dao.model.ApiStatisticsModelQuery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiStatisticsModelMapper {
    long countByQuery(ApiStatisticsModelQuery example);

    int deleteByQuery(ApiStatisticsModelQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiStatisticsModel record);

    int insertSelective(ApiStatisticsModel record);

    List<ApiStatisticsModel> selectByQuery(ApiStatisticsModelQuery example);

    ApiStatisticsModel selectByPrimaryKey(Long id);

    int updateByQuerySelective(@Param("record") ApiStatisticsModel record, @Param("example") ApiStatisticsModelQuery example);

    int updateByQuery(@Param("record") ApiStatisticsModel record, @Param("example") ApiStatisticsModelQuery example);

    int updateByPrimaryKeySelective(ApiStatisticsModel record);

    int updateByPrimaryKey(ApiStatisticsModel record);
}