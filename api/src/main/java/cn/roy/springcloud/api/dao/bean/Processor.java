package cn.roy.springcloud.api.dao.bean;

import java.util.Date;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-06 13:55
 * @Version: v1.0
 */
public class Processor {
    private Long id;

    private String assignee;

    private String assignDate;

    private String group;

    private String grade;

    private Short reviewer;

    private String name;

    private String email;

    private String department;

    private String mobile;

    private String extension;

    private Long wrWorkRequestId;

    private Short sDf;

    private Date sCt;

    private Date sUt;

    private String sUid;

    /**
     * TODO 0801新增字段
     * Modify by Roy Z on 2019-07-04 17:08
     */
    private String nameCN;
    private String taskId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee == null ? null : assignee.trim();
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate == null ? null : assignDate.trim();
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group == null ? null : group.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public Short getReviewer() {
        return reviewer;
    }

    public void setReviewer(Short reviewer) {
        this.reviewer = reviewer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension == null ? null : extension.trim();
    }

    public Long getWrWorkRequestId() {
        return wrWorkRequestId;
    }

    public void setWrWorkRequestId(Long wrWorkRequestId) {
        this.wrWorkRequestId = wrWorkRequestId;
    }

    public Short getsDf() {
        return sDf;
    }

    public void setsDf(Short sDf) {
        this.sDf = sDf;
    }

    public Date getsCt() {
        return sCt;
    }

    public void setsCt(Date sCt) {
        this.sCt = sCt;
    }

    public Date getsUt() {
        return sUt;
    }

    public void setsUt(Date sUt) {
        this.sUt = sUt;
    }

    public String getsUid() {
        return sUid;
    }

    public void setsUid(String sUid) {
        this.sUid = sUid == null ? null : sUid.trim();
    }

    public String getNameCN() {
        return nameCN;
    }

    public void setNameCN(String nameCN) {
        this.nameCN = nameCN;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
