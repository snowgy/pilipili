package com.example.pilipili.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name="user")
/** User Model */
public class User {

    @Id
    @Column(name="user_id")
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;


    @Column(name="user_name", unique = true)
    private String userName;

    @Column(name="password")
    private String password;

    @JsonBackReference(value = "user-own-image")
    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Image> imageList = new ArrayList<>();

    @JsonBackReference(value = "user-love-image")
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.ALL
    })
    @JoinTable(name="image_user",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="image_id")
    )
    private Set<Image> lovedImages = new HashSet<>();

    public void addLoveImage(Image img){
        lovedImages.add(img);
        img.getLovers().add(this);
    }

    public void removeLoveImage(Image img){
        lovedImages.remove(img);
        img.getLovers().remove(this);
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User) obj;
        return this.userId == user.userId;
    }





}
