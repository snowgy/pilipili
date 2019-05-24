package com.example.pilipili.service.auth;


import com.example.pilipili.api.UserRepository;
import com.example.pilipili.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    @Autowired
    UserRepository userRepository;

    private String message;

    /**
     * Perform signup logic.
     * @param user user
     * @return boolean
     */
    public boolean signupUser(User user){
        try{
            User dbUser = userRepository.findUserByUserName(user.getUserName());
            if(dbUser == null){
                PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
                String originalPassword = user.getPassword();
                String hashedPassword = passwordAuthentication.hash(originalPassword.toCharArray());
                user.setPassword(hashedPassword);
                userRepository.save(user);
                setMessage("Sign Up Success");
                return true;

            }
            else{
                setMessage("Please Use Another Username");
                return false;
            }

        }catch (Exception e){
            setMessage("Unknown Error");
            return false;
        }
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
