
package com.campusfind.campusfind.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// ENTITY: Database mein lost item ka data store karta hai
@Entity
@Getter
@Setter
@NoArgsConstructor
public class LostItem {

    // PRIMARY KEY: Har item ki unique ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // VALIDATION: Item name empty nahi hona chahiye
    @NotBlank(message = "Item name is required")
    private String itemName;

    // VALIDATION: Description empty nahi honi chahiye
    @NotBlank(message = "Description is required")
    private String description;

    // VALIDATION: Location empty nahi honi chahiye
    @NotBlank(message = "Location is required")
    private String location;

    // VALIDATION: Status empty nahi hona chahiye
    @NotBlank(message = "Status is required")
    private String status;

    // Item kab khoya
    private LocalDate lostDate;
    private LocalTime lostTime;

    // Item kis user ne create kiya
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Record create hone ka date-time automatically set hoga
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // CUSTOM CONSTRUCTOR
    public LostItem(String itemName, String description,
                    String location, String status) {
        this.itemName = itemName;
        this.description = description;
        this.location = location;
        this.status = status;
    }
}
