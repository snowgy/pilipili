package com.example.pilipili.model;

/**
 * image model for data transfer
 */
public class ImageData {
    String path;
    int likeNum;
    String userName;

    public ImageData(String path, int likeNum, String userName) {
        this.path = path;
        this.likeNum = likeNum;
        this.userName = userName;
    }

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
