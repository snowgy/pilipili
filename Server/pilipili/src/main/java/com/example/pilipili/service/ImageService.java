package com.example.pilipili.service;

import com.example.pilipili.model.Image;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ImageService {
    List<Image> findAllImages();
    int getImageLikes(String imagePath);
    Image getImageById(long id);
    void save(Image image);
    void lovePhoto(long imgId);
    void deletePhoto(long imgId);
}
