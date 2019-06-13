package com.example.connecthuntapp.Models;

import java.util.Date;

public class History {
    private String companyName;
    private String nameVaga;
    private String logo_company;
    private String city;
    private String Comment;
    private boolean Situation = true;
    private Date dateVisualized;
    private Date dateComment;
    private Date dateApply;

    public History(){}

    public History(String companyName, String nameVaga, String logo_company, boolean situation, String city, String Comment, Date dateVisualized,Date dateComment, Date dateApply) {
        this.companyName = companyName;
        this.nameVaga = nameVaga;
        this.logo_company = logo_company;
        Situation = situation;
        this.city = city;
        this.Comment = Comment;
        this.dateVisualized = dateVisualized;
        this.dateComment = dateComment;
        this.dateApply = dateApply;
    }

    public Date getDateApply() {
        return dateApply;
    }

    public void setDateApply(Date dateApply) {
        this.dateApply = dateApply;
    }

    public Date getDateComment() {
        return dateComment;
    }

    public void setDateComment(Date dateComment) {
        this.dateComment = dateComment;
    }

    public Date getDateVisualized() {
        return dateVisualized;
    }

    public void setDateVisualized(Date dateVisualized) {
        this.dateVisualized = dateVisualized;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getNameVaga() {
        return nameVaga;
    }

    public void setNameVaga(String nameVaga) {
        this.nameVaga = nameVaga;
    }

    public String getLogo_company() {
        return logo_company;
    }

    public void setLogo_company(String logo_company) {
        this.logo_company = logo_company;
    }

    public boolean isSituation() {
        return Situation;
    }

    public void setSituation(boolean situation) {
        Situation = situation;
    }
}
