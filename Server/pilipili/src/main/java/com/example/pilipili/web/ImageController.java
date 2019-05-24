package com.example.pilipili.web;

import com.example.pilipili.model.Image;
import com.example.pilipili.model.ImageData;
import com.example.pilipili.model.LikeForm;
import com.example.pilipili.model.User;
import com.example.pilipili.service.ImageService;
import com.example.pilipili.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ImageController {
    @Autowired
    ImageService imageService;

    @Autowired
    UserService userService;


    @PostMapping(value = {"/getAllImages"})
    public List<ImageData> getAllImages() {
        List<Image> imagesList = imageService.findAllImages();
        List<ImageData> imageDataList = new ArrayList<>();

        for (Image image: imagesList) {
            String path = image.getImagePath();
            ImageData imageData = new ImageData(image.getImageId(),
                                                path.substring(path.lastIndexOf("/")+1),
                                                image.getLikeNum(),
                                                image.getOwner().getUserName());
            imageDataList.add(imageData);
        }
        return imageDataList;
    }

    @PostMapping(value = {"/deleteImage"})
    public void deletImage(@RequestParam("imgId") long imgId) {
        imageService.deletePhoto(imgId);
    }
    @PostMapping(value = {"/getLikeNumber"})
    public int getLikeNumber(@RequestParam("path") String imagePath) {
        return imageService.getImageLikes(imagePath);
    }

    @PostMapping(value = {"/updateLikeNum"})
    public void updateLikeNum(@RequestBody LikeForm likeForm) {
        User user = userService.getUserByName(likeForm.getUserName());
        Image image = imageService.getImageById(likeForm.getId());
        image.setLikeNum(image.getLikeNum() + 1);
        userService.addLovePhoto(user, image);
        userService.save(user);
        imageService.save(image);
    }
}
