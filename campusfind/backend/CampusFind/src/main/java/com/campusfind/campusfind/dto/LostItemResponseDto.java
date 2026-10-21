
package com.campusfind.campusfind.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// RESPONSE DTO: Lost item ki details client ko return karta hai.
// GET aur future mein doosre API responses mein use kar sakte hain.
public class LostItemResponseDto {

    private Long id;
    private String itemName;
    private String description;
    private String location;
    private String status;
    private LocalDate lostDate;
    private LocalTime lostTime;
    private LocalDateTime createdAt;

    // Constructor: Entity se mili values ko DTO mein set karta hai.
    public LostItemResponseDto(
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

    // Getters: Response JSON banane ke liye fields read karne dete hain.
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
