package com.example.pilipili.service;

import com.example.pilipili.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User getUserByName(String username);
//    User getUserById(long id);
//
//    void save(User user);
//
//    void deleteUserByID(long id);
//    void deleteUserByUsername(String username);

    // List<Image> getUserImages(User user);
}
