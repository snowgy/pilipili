package com.example.pilipili.service;

import com.example.pilipili.api.ImageRepository;
import com.example.pilipili.api.UserRepository;
import com.example.pilipili.model.Image;
import com.example.pilipili.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageRepository imageRepository;

    /**
     * Get User By Username.
     * @param userName userName
     * @return return a user instance
     */
    @Override
    public User getUserByName(String userName) {
        return userRepository.findUserByUserName(userName);
    }

    /**
     * save the image to database
     * @param userName username
     * @param path image path
     */
    @Override
    public void addUserImage(String userName, String path) {
        User user = getUserByName(userName);
        Image image = new Image();
        image.setImagePath(path);
        image.setOwner(user);
        image.setLikeNum(0);
        imageRepository.save(image);
    }

    /**
     * get all the images paths of the user
     * @param user userName
     * @return all the image paths of the user
     */
    @Override
    public List<String> getUserImages(User user) {
        List<Image> images = user.getImageList();
        List<String> paths = new ArrayList<>();
        for (Image image : images) {
            paths.add(image.getImagePath());
        }
        return paths;
    }

    @Override
    public void addLovePhoto(User user, Image image) {
        user.addLoveImage(image);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

//    @Override
//    public User getUserById(long id) {
//        return userRepository.findUserByUserId(id);
//    }
//
//    @Override
//    public void save(User user) {
//        userRepository.save(user);
//    }
//
//    @Override
//    public void deleteUserByID(long id) {
//        userRepository.deleteUserByUserId(id);
//    }
//
//    @Override
//    public void deleteUserByUsername(String userName) {
//        userRepository.deleteUserByUserName(userName);
//    }
}
