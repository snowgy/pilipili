package com.example.pilipili.service.auth;


import com.example.pilipili.api.UserRepository;
import com.example.pilipili.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepository;


    /**
     * verify whether the user can login successfully or not.
     * @param user user
     * @return boolean
     */
    public boolean verifyLogin(User user){
        PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
        User dbUser = userRepository.findUserByUserName(user.getUserName());
        if (dbUser == null)
            return false;
        System.err.println(user.getUserName()+" "+user.getPassword());

        String token = dbUser.getPassword();
        String password = user.getPassword();
        return passwordAuthentication.authenticate(password.toCharArray(), token);
    }


}
