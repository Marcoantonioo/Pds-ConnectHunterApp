package com.example.connecthuntapp.Models;

public class Language {
    private String language_name;
    private String language_level;
    private String user_id;

    public Language(){}

    public Language(String language_name, String language_level, String user_id) {
        this.language_name = language_name;
        this.language_level = language_level;
        this.user_id = user_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public String getLanguage_level() {
        return language_level;
    }

    public void setLanguage_level(String language_level) {
        this.language_level = language_level;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
