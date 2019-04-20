package com.example.pilipili.service;

import com.example.pilipili.api.UserRepository;
import com.example.pilipili.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    /**
     * Get User By Username.
     * @param userName userName
     * @return return a user instance
     */
    @Override
    public User getUserByName(String userName) {
        return userRepository.findUserByUserName(userName);
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
