package com.campusfind.campusfind.controller;

import com.campusfind.campusfind.dto.LostItemResponseDto;
import com.campusfind.campusfind.dto.UpdateLostItemRequestDto;
import com.campusfind.campusfind.service.LostItemService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LostItemService lostItemService;

    public AdminController(LostItemService lostItemService) {
        this.lostItemService = lostItemService;
    }

    // Test admin access
    @GetMapping("/test")
    public String adminTest() {
        return "Welcome Admin! Access granted.";
    }

    // Admin: Get all reported items
    @GetMapping("/lost-items")
    public ResponseEntity<List<LostItemResponseDto>> getAllItems() {
        return ResponseEntity.ok(
                lostItemService.getAllLostItems()
        );
    }

    // Admin: Update complete item
    @PutMapping("/lost-items/{id}")
    public ResponseEntity<LostItemResponseDto> updateItem(
            @PathVariable Long id,
            @RequestBody UpdateLostItemRequestDto request) {

        return ResponseEntity.ok(
                lostItemService.updateLostItem(id, request)
        );
    }

    // Admin: Update selected fields
    @PatchMapping("/lost-items/{id}")
    public ResponseEntity<LostItemResponseDto> patchItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(
                lostItemService.patchLostItem(id, updates)
        );
    }

    // Admin: Delete item
    @DeleteMapping("/lost-items/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id) {

        lostItemService.deleteLostItem(id);
        return ResponseEntity.noContent().build();
    }
}