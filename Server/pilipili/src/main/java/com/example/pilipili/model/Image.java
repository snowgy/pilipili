package com.example.pilipili.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="image")
/** Image Model */
public class Image {
    @Id
    @Column(name="image_id")
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(name="image_path")
    private String imagePath;

    @Column(name="like_num")
    private int likeNum;

    @JsonBackReference(value = "user-own-image")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User owner;


    @JsonBackReference(value = "user-love-image")
    @ManyToMany(mappedBy = "lovedImages")
    private Set<User> lovers = new HashSet<>();

    public void addUser(User user){
        lovers.add(user);
        user.getLovedImages().add(this);
    }

    public void remove(User user){
        lovers.remove(user);
        user.getLovedImages().remove(this);
    }

    @Override
    public boolean equals(Object obj) {
        Image user = (Image) obj;
        return this.imageId == user.imageId;
    }


    public Set<User> getLovers() {
        return lovers;
    }

}
