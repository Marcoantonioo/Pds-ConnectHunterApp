package com.example.connecthuntapp.Models;

public class History {
    private String companyName;
    private String nameVaga;
    private String logo_company;
    private String city;
    private boolean Situation = true;

    public History(){}

    public History(String companyName, String nameVaga, String logo_company, boolean situation, String city) {
        this.companyName = companyName;
        this.nameVaga = nameVaga;
        this.logo_company = logo_company;
        Situation = situation;
        this.city = city;
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
