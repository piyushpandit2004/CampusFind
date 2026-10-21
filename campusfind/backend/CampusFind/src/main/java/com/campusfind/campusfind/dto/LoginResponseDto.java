
package com.campusfind.campusfind.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

    private String message;
    private String email;
    private String token;

    public LoginResponseDto(String message, String email, String token) {
        this.message = message;
        this.email = email;
        this.token = token;
    }
}
