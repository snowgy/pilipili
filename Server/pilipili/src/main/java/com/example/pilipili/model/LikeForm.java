package com.example.pilipili.model;

public class LikeForm {
    String userName;
    long id;
    int likes;

    public String getUserName(){
        return userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
