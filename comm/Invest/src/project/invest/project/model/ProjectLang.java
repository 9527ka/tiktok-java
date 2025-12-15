package project.invest.project.model;

import kernel.bo.EntityObject;

import java.util.Date;

public class ProjectLang extends EntityObject<String> {

    /**
     * 项目ID
     */
    private String projectId;


    /**
     * 名称
     */
    private String name;

    /**
     * 语言
     */
    private String lang;

    /**
     * 担保机构
     */
    private String guarantyAgency;

    /**
     * 结算时间
     */
    private String desSettle;

    /**
     * 资金用途
     */
    private String desUse;

    /**
     * 安全保障
     */
    private String desSafe;


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getGuarantyAgency() {
        return guarantyAgency;
    }

    public void setGuarantyAgency(String guarantyAgency) {
        this.guarantyAgency = guarantyAgency;
    }

    public String getDesSettle() {
        return desSettle;
    }

    public void setDesSettle(String desSettle) {
        this.desSettle = desSettle;
    }

    public String getDesUse() {
        return desUse;
    }

    public void setDesUse(String desUse) {
        this.desUse = desUse;
    }

    public String getDesSafe() {
        return desSafe;
    }

    public void setDesSafe(String desSafe) {
        this.desSafe = desSafe;
    }
}