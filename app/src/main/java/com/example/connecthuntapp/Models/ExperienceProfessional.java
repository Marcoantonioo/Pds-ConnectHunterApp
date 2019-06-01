package com.example.connecthuntapp.Models;

public class ExperienceProfessional {
    private String companyName;
    private String office;
    private String initialDate;
    private String finalData;
    private String description;

    public ExperienceProfessional(){}

    public ExperienceProfessional(String companyName, String office, String initialDate, String finalData, String description) {
        this.companyName = companyName;
        this.office = office;
        this.initialDate = initialDate;
        this.finalData = finalData;
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getFinalData() {
        return finalData;
    }

    public void setFinalData(String finalData) {
        this.finalData = finalData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
