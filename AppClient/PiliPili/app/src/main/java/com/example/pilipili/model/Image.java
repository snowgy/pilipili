package com.example.pilipili.model;

public class Image {
    String path;
    int likeNum;
    String userName;

    public String getPath() {
        return path;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
