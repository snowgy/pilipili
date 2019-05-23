package com.example.pilipili.service;

import com.example.pilipili.model.Image;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ImageService {
    List<Image> findAllImages();
    int getImageLikes(String imagePath);
}
