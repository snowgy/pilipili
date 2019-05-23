package com.example.pilipili.web;

import com.example.pilipili.model.Image;
import com.example.pilipili.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ImageController {
    @Autowired
    ImageService imageService;

    @PostMapping(value = {"/getAllImages"})
    public List<String> getAllImages() {
        List<Image> imagesList = imageService.findAllImages();
        List<String> pathList = new ArrayList<>();
        for (Image image: imagesList) {
            String path = image.getImagePath();
            System.out.println(path);
            pathList.add(path.substring(path.lastIndexOf("/")+1));
        }
        return pathList;
    }

    @PostMapping(value = {"/getLikeNumber"})
    public int getLikeNumber(@RequestParam("path") String imagePath) {
        return imageService.getImageLikes(imagePath);
    }

}
