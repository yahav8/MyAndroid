package com.example.myfinal.Objects;

public class Admin  extends User {
    private boolean admin;

    public Admin(String name, int id, int phonNum, String mail, String rank, int courseNum, String releaseDate, boolean admin) {
        super(name, id, phonNum, mail, rank, courseNum, releaseDate);
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "admin=" + admin +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", phonNum=" + phonNum +
                ", mail='" + mail + '\'' +
                ", rank='" + rank + '\'' +
                ", courseNum=" + courseNum +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
