package com.example.connecthuntapp.Models;

import java.util.Date;

public class Candidato {

    private String name;
    private String description;
    private String profileUrl;
    private String Comment;
    private Date date;

    public Candidato(){}

    public Candidato(String name, String description, String profileUrl, String Comment, Date date) {
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.Comment = Comment;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
