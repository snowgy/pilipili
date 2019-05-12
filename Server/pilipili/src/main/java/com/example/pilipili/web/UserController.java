package com.example.pilipili.web;

import com.example.pilipili.model.ResponseCode;
import com.example.pilipili.model.ResponseEntity;
import com.example.pilipili.model.ResponseUser;
import com.example.pilipili.model.User;
import com.example.pilipili.service.auth.LoginService;
import com.example.pilipili.service.auth.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/** UserController Provides User APIs. */
@RestController
public final class UserController {
    /** LoginService instance. */
    @Autowired
    private LoginService loginService;
    /** SignupService instance. */
    @Autowired
    private SignupService signupService;


    /**
     * <p>implement user Login</p>
     * <p>RequestUrl: http://10.20.35.198:8080/userLogin</p>
     * <p>RequsetMethod: Post</p>
     * <p>Parameter: application/json</p>
     * <p>e.g. {"userName": root, "password": 12345}</p>
     * <p>Response: application/json</p>
     * <p>e.g. {"code": 0, "message": "Login Success", "data":{"useName":"root"}}</p>
     * @param user converted from json data
     * @return ResponseEntity contains response message
     */
    @PostMapping(value = "/userLogin")
    public ResponseEntity login(@RequestBody User user) {
//        User tempUser = new User();
//        tempUser.setPassword(password);
//        tempUser.setUserName(userName);

        boolean verify = loginService.verifyLogin(user);
        if (verify) {
//            httpSession.setAttribute(WebSecurityConfig.SESSION_KEY, userName);
//            httpSession.setAttribute("user",
//                    userDao.findUserByUserName(userName));
            ResponseUser responseUser = new ResponseUser();
            responseUser.setUserName(user.getUserName());
            return new ResponseEntity(ResponseCode.SUCCESS,
                    "Login Success", responseUser);
        } else {
            return new ResponseEntity(ResponseCode.FAIL, "UserName or Password Incorrect");
        }

    }

    /**
     * <p>implement user Signup</p>
     * <p>RequestUrl: http://10.20.35.198:8080/userSignup</p>
     * <p>RequsetMethod: Post</p>
     * <p>Parameter: application/json</p>
     * <p>e.g. {"userName": root, "password": 12345}</p>
     * <p>Response: application/json</p>
     * <p>e.g. {"code": 0, "message": "Signup Success", "data":{"useName":"root"}}</p>
     * @param user converted from json data
     * @return ResponseEntity contains response message
     */
    @PostMapping(value = {"/userSignup"})
    public ResponseEntity signup(@RequestBody User user) {
        if(signupService.signupUser(user)) {
            ResponseUser responseUser = new ResponseUser();
            responseUser.setUserName(user.getUserName());
            return new ResponseEntity(ResponseCode.SUCCESS,
                    signupService.getMessage(), responseUser);
        } else{
            return new ResponseEntity(ResponseCode.FAIL, signupService.getMessage());
        }

    }

    @PostMapping(value = {"/uploadImg"})
    public @ResponseBody String uploadImg(@RequestParam("file") MultipartFile file,
                                          HttpServletRequest request) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        String filePath = getImgPath();
        try {
            FileUtils.uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static String getImgPath() {
        String filePath = UserController.class.getClassLoader().getResource("").toString();
        File file=new File(filePath);
        filePath =file.getParent();
        int s = filePath.indexOf("/");
        int e = filePath.lastIndexOf("/");
        filePath = filePath.substring(s, e+1);
        filePath += "/src/main/resources/static/img/";
        return filePath;
    }

}
