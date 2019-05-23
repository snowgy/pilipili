package com.example.pilipili.web;

import com.example.pilipili.model.Image;
import com.example.pilipili.model.ImageData;
import com.example.pilipili.model.LikeForm;
import com.example.pilipili.model.User;
import com.example.pilipili.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ImageController {
    @Autowired
    ImageService imageService;

//    @PostMapping(value = {"/getAllImages"})
//    public List<String> getAllImages() {
//        List<Image> imagesList = imageService.findAllImages();
//        List<String> pathList = new ArrayList<>();
//        for (Image image: imagesList) {
//            String path = image.getImagePath();
//            System.out.println(path);
//            pathList.add(path.substring(path.lastIndexOf("/")+1));
//        }
//        return pathList;
//    }

    @PostMapping(value = {"/getAllImages"})
    public List<ImageData> getAllImages() {
        List<Image> imagesList = imageService.findAllImages();
        List<ImageData> imageDataList = new ArrayList<>();

        for (Image image: imagesList) {
            String path = image.getImagePath();
            ImageData imageData = new ImageData(image.getImageId(),
                                                path.substring(path.lastIndexOf("/")+1),
                                                image.getLikeNum(),
                                                image.getUser().getUserName());
            imageDataList.add(imageData);
        }
        return imageDataList;
    }

    @PostMapping(value = {"/getLikeNumber"})
    public int getLikeNumber(@RequestParam("path") String imagePath) {
        return imageService.getImageLikes(imagePath);
    }

    @PostMapping(value = {"/updateLikeNum"})
    public void updateLikeNum(@RequestBody LikeForm likeForm) {
        Image image = imageService.getImageById(likeForm.getId());
        image.setLikeNum(image.getLikeNum() + 1);
        imageService.save(image);
    }
}
