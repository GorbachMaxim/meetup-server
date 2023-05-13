package com.example.meetupserver.dto;

import com.example.meetupserver.model.User;
import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {

    private String token;
    private String type = "Bearer";

    private User user;



    public JwtResponse(String token, User user) {
        this.token = token;
        this.user = user;

    }

}