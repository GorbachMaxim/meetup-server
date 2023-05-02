package com.example.meetupserver.dto;

import javax.validation.constraints.NotBlank;

public class Password {
    @NotBlank
    private String password;



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
