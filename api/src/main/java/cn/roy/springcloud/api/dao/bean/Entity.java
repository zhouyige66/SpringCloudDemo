package cn.roy.springcloud.api.dao.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Database Table Remarks:
 *   接口请求参数实体表
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table entity
 */
public class Entity implements Serializable {
    /**
     * Database Column Remarks:
     *   主键
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column entity.id
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    private Long id;

    /**
     * Database Column Remarks:
     *   实体名称
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column entity.name
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    private String name;

    /**
     * Database Column Remarks:
     *   属性
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column entity.properties
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    private String properties;

    /**
     * Database Column Remarks:
     *   是否删除
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column entity.del
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    private Boolean del;

    /**
     * Database Column Remarks:
     *   创建时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column entity.create_time
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    private Date createTime;

    /**
     * Database Column Remarks:
     *   更新时间
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column entity.update_time
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column entity.id
     *
     * @return the value of entity.id
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Entity withId(Long id) {
        this.setId(id);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column entity.id
     *
     * @param id the value for entity.id
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column entity.name
     *
     * @return the value of entity.name
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Entity withName(String name) {
        this.setName(name);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column entity.name
     *
     * @param name the value for entity.name
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column entity.properties
     *
     * @return the value of entity.properties
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public String getProperties() {
        return properties;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Entity withProperties(String properties) {
        this.setProperties(properties);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column entity.properties
     *
     * @param properties the value for entity.properties
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public void setProperties(String properties) {
        this.properties = properties;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column entity.del
     *
     * @return the value of entity.del
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Boolean getDel() {
        return del;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Entity withDel(Boolean del) {
        this.setDel(del);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column entity.del
     *
     * @param del the value for entity.del
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public void setDel(Boolean del) {
        this.del = del;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column entity.create_time
     *
     * @return the value of entity.create_time
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Entity withCreateTime(Date createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column entity.create_time
     *
     * @param createTime the value for entity.create_time
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column entity.update_time
     *
     * @return the value of entity.update_time
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public Entity withUpdateTime(Date updateTime) {
        this.setUpdateTime(updateTime);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column entity.update_time
     *
     * @param updateTime the value for entity.update_time
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table entity
     *
     * @mbg.generated Fri Sep 06 13:05:52 CST 2019
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", properties=").append(properties);
        sb.append(", del=").append(del);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}