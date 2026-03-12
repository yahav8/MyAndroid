package com.example.myfinal.Objects;

public class User {
    protected String name;
    protected int id;
    protected int phonNum;
    protected String mail;
    protected String rank;
    protected int courseNum;
    protected String releaseDate;

    public User(String name, int id, int phonNum, String mail, String rank, int courseNum, String releaseDate) {
        this.name = name;
        this.id = id;
        this.phonNum = phonNum;
        this.mail = mail;
        this.rank = rank;
        this.courseNum = courseNum;
        this.releaseDate = releaseDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhonNum() {
        return phonNum;
    }

    public void setPhonNum(int phonNum) {
        this.phonNum = phonNum;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(int courseNum) {
        this.courseNum = courseNum;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", phonNum=" + phonNum +
                ", mail='" + mail + '\'' +
                ", rank='" + rank + '\'' +
                ", courseNum=" + courseNum +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
