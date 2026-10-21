
package com.campusfind.campusfind.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// RESPONSE DTO: Server se client ko item ki details bhejne ke liye.
public class CreateLostItemResponseDto {

    private Long id;
    private String itemName;
    private String description;
    private String location;
    private String status;
    private LocalDate lostDate;
    private LocalTime lostTime;
    private LocalDateTime createdAt;

    // Constructor: response DTO ke fields ko ek saath initialize karta hai.
    public CreateLostItemResponseDto(
            Long id,
            String itemName,
            String description,
            String location,
            String status,
            LocalDate lostDate,
            LocalTime lostTime,
            LocalDateTime createdAt) {

        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.location = location;
        this.status = status;
        this.lostDate = lostDate;
        this.lostTime = lostTime;
        this.createdAt = createdAt;
    }

    // Getters: Spring/Jackson ko response JSON banane ke liye fields read karne dete hain.
    public Long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getLostDate() {
        return lostDate;
    }

    public LocalTime getLostTime() {
        return lostTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
