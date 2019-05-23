package com.example.pilipili.service;

import com.example.pilipili.model.Image;
import com.example.pilipili.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User getUserByName(String userName);
    void addUserImage (String userName, String path);
    List<String> getUserImages(User user);
    void addLovePhoto(User user, Image image);
    void save(User user);
//    User getUserById(long id);
//
//    void save(User user);
//
//    void deleteUserByID(long id);
//    void deleteUserByUsername(String username);

}
