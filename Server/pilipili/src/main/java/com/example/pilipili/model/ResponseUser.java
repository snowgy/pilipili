package com.example.pilipili.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is used for sending reponse to
 * the client. The difference between it and
 * User class is that it does not have password
 * field, which ensures the safety of the app.
 */

@Getter
@Setter
public class ResponseUser {
    private String userName;
}
