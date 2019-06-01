package com.example.connecthuntapp.Models;

public class Skill {
    private String skillName;
    private String skillDegree;
    private String user_id;

    public Skill(){}

    public Skill(String skillName, String skillDegree, String user_id) {
        this.skillName = skillName;
        this.skillDegree = skillDegree;
        this.user_id = user_id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillDegree() {
        return skillDegree;
    }

    public void setSkillDegree(String skillDegree) {
        this.skillDegree = skillDegree;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
