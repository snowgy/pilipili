package com.example.pilipili.api;

import com.example.pilipili.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    /**
     * find image by image path
     * @param imagePath the path an image
     * @return
     */
    Image findImageByImagePath(String imagePath);
}
