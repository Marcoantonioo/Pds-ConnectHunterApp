package com.example.connecthuntapp.Models;

import java.util.Date;

public class Vaga {
    private String nameVaga;
    private String CompanyName;
    private String logo_company;
    private String status;
    private String city;
    private String min_salary;
    private String max_salary;
    private String about_company;
    private Date dateJob;

    public Vaga(){}

    public Vaga(String nameVaga, String companyName, String logo_company, String status, String city, String min_salary, String max_salary, String about_company, Date dateJob) {
        this.nameVaga = nameVaga;
        this.CompanyName = companyName;
        this.logo_company = logo_company;
        this.status = status;
        this.city = city;
        this.min_salary = min_salary;
        this.max_salary = max_salary;
        this.about_company = about_company;
        this.dateJob = dateJob;
    }

    public Date getDateJob() {
        return dateJob;
    }

    public void setDateJob(Date dateJob) {
        this.dateJob = dateJob;
    }

    public String getNameVaga() {
        return nameVaga;
    }

    public void setNameVaga(String nameVaga) {
        this.nameVaga = nameVaga;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getLogo_company() {
        return logo_company;
    }

    public void setLogo_company(String logo_company) {
        this.logo_company = logo_company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMin_salary() {
        return min_salary;
    }

    public void setMin_salary(String min_salary) {
        this.min_salary = min_salary;
    }

    public String getMax_salary() {
        return max_salary;
    }

    public void setMax_salary(String max_salary) {
        this.max_salary = max_salary;
    }

    public String getAbout_company() {
        return about_company;
    }

    public void setAbout_company(String about_company) {
        this.about_company = about_company;
    }
}
