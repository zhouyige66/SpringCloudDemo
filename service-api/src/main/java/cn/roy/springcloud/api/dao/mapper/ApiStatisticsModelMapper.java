package cn.roy.springcloud.api.dao.mapper;

import cn.roy.springcloud.api.dao.model.ApiStatisticsModel;
import cn.roy.springcloud.api.dao.model.ApiStatisticsModelQuery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiStatisticsModelMapper {
    long countByExample(ApiStatisticsModelQuery example);

    int deleteByExample(ApiStatisticsModelQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiStatisticsModel record);

    int insertSelective(ApiStatisticsModel record);

    List<ApiStatisticsModel> selectByExample(ApiStatisticsModelQuery example);

    ApiStatisticsModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ApiStatisticsModel record, @Param("example") ApiStatisticsModelQuery example);

    int updateByExample(@Param("record") ApiStatisticsModel record, @Param("example") ApiStatisticsModelQuery example);

    int updateByPrimaryKeySelective(ApiStatisticsModel record);

    int updateByPrimaryKey(ApiStatisticsModel record);
}