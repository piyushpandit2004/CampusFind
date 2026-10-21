
package com.campusfind.campusfind.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegisterResponseDto {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public RegisterResponseDto(
            Long id,
            String name,
            String email,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }
}
