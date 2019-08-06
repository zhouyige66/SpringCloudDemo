package cn.roy.springcloud.api.dao;

import cn.roy.springcloud.api.dao.bean.Processor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-06 13:54
 * @Version: v1.0
 */
@Mapper
public interface ProcessorMapper {

    int insert(Processor record);

    int insertSelective(Processor record);

    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(Processor record);

    int updateByPrimaryKeySelective(Processor record);

    /**
     * 通过id查询数据库记录
     *
     * @param id
     * @return
     */
    Processor selectByPrimaryKey(Long id);

    /**
     * 通过work_request_id查询
     *
     * @param workRequestId
     * @return
     */
    @Select("select * from wr_assignee where wr_work_request_id = #{wrId}")
    List<Processor> selectAssigneeByWorkRequestId(@Param("wrId") Long workRequestId);

    /**
     * 通过staffId查询
     *
     * @param assignee
     * @return
     */
    @Select("select * from wr_assignee where assignee = #{assignee} and reviewer = 0")
    List<Processor> selectAssigneeList(@Param("assignee") String assignee);

}
