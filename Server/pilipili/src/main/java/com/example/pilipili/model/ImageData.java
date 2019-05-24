package com.example.pilipili.model;

/**
 * image model for data transfer
 */
public final class ImageData {
    Long id;
    String path;
    int likeNum;
    String userName;

    public ImageData(Long id, String path, int likeNum, String userName) {
        this.id = id;
        this.path = path;
        this.likeNum = likeNum;
        this.userName = userName;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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
