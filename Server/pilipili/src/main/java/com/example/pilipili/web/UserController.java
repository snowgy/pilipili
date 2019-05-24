package com.example.pilipili.web;

import com.example.pilipili.model.*;
import com.example.pilipili.service.ImageService;
import com.example.pilipili.service.UserService;
import com.example.pilipili.service.auth.LoginService;
import com.example.pilipili.service.auth.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** UserController Provides User APIs. */
@RestController
public final class UserController {
    /** LoginService instance. */
    @Autowired
    private LoginService loginService;
    /** SignupService instance. */
    @Autowired
    private SignupService signupService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

    @Value("${file.uploadFolder}")
    private String uploadPath;

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

    /**
     * upload a image to server
     * @param file file
     * @param userName userName
     * @param request http request
     * @return
     */
    @PostMapping(value = {"/uploadImg"})
    public @ResponseBody String uploadImg(@RequestParam("file") MultipartFile file,
                                          @RequestParam("username") String userName,
                                          HttpServletRequest request) {
        String fileName = file.getOriginalFilename();
        // String filePath = getImgPath();
        userService.addUserImage(userName, uploadPath + fileName);
        try {
            FileUtils.uploadFile(file.getBytes(), uploadPath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }


    /**
     * Get all the images belong to one user
     * @param userName userName
     * @return all user images
     */
    @PostMapping(value = {"/getUserImages"})
    public List<ImageData> getUserImages(@RequestParam String userName) {
        User user = userService.getUserByName(userName);
        List<Image> images = user.getImageList();
        return fillImageList(images);
    }

    /**
     * Get all the images that one user likes
     * @param userName userName
     * @return all the images the user liked
     */
    @PostMapping(value = {"/getLovedImages"})
    public List<ImageData> getLovedImages(@RequestParam String userName) {
        User user = userService.getUserByName(userName);
        Set<Image> lovedImages = user.getLovedImages();
        return fillImageList(lovedImages);
    }


    private List<ImageData> fillImageList(Collection<Image> imgs){
        List<ImageData> imageDataList = new ArrayList<>();
        for (Image image : imgs) {
            String path = image.getImagePath();
            path = path.substring(path.lastIndexOf("/")+1);
            ImageData imageData = new ImageData(image.getImageId(),
                    path,
                    image.getLikeNum(),
                    image.getOwner().getUserName());
            imageDataList.add(imageData);
        }
        return imageDataList;
    }

}
