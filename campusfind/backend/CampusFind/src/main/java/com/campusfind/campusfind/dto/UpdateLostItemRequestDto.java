
package com.campusfind.campusfind.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;

// REQUEST DTO: Existing lost item update karne ke liye client se data leta hai.
public class UpdateLostItemRequestDto {

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Status is required")
    private String status;

    private LocalDate lostDate;
    private LocalTime lostTime;

    // Getters and setters

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getLostDate() {
        return lostDate;
    }

    public void setLostDate(LocalDate lostDate) {
        this.lostDate = lostDate;
    }

    public LocalTime getLostTime() {
        return lostTime;
    }

    public void setLostTime(LocalTime lostTime) {
        this.lostTime = lostTime;
    }
}
