package com.example.pilipili.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;


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

    @JsonBackReference
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Image> imageList = new ArrayList<>();


}
