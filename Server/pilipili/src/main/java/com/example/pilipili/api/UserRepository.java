package com.example.pilipili.api;

import com.example.pilipili.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    /**
     * find user by userName
     * @param userName userName
     * @return User Instance
     */
    User findUserByUserName(String userName);
//    User findUserByUserId(long id);
//    void deleteUserByUserId(long id);
//    void deleteUserByUserName(String userName);
}
