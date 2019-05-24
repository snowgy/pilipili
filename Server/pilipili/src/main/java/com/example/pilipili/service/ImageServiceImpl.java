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

    /**
     * find all the images
     * @return all images in the database
     */
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

    /**
     * Get one image by id
     * @param id image id
     * @return image object
     */
    @Override
    public Image getImageById(long id) {
        Image image = imageRepository.findImageByImageId(id);
        return image;
    }

    /**
     * save one image
     * @param image image object
     */
    @Override
    public void save(Image image) {
        imageRepository.save(image);
    }

    /**
     * delete one photo
     * @param imgId image id
     */
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
