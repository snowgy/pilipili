package com.example.pilipili.service;

import com.example.pilipili.api.ImageRepository;
import com.example.pilipili.model.Image;
import com.example.pilipili.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
    @Autowired
    ImageRepository imageRepository;
    @Override
    public List<Image> findAllImages() {
        return imageRepository.findAll();
    }

    /**
     * get the image like number via image path
     * @param imagePath the image path
     * @return like number
     */
    @Override
    public int getImageLikes(String imagePath) {
        Image image = imageRepository.findImageByImagePath(imagePath);
        return image.getLikeNum();
    }

    @Override
    public Image getImageById(long id) {
        Image image = imageRepository.findImageByImageId(id);
        return image;
    }

    @Override
    public void save(Image image) {
        imageRepository.save(image);
    }

    @Override
    public void lovePhoto(long imgId) {

    }

    @Override
    public void deletePhoto(long imgId) {
        Image img = imageRepository.findImageByImageId(imgId);
        Set<User> users = img.getLovers();
        for(User user: users){
            user.removeLoveImage(img);
        }
        imageRepository.delete(img);
    }
}
