
package com.campusfind.campusfind.controller;

import com.campusfind.campusfind.dto.CreateLostItemRequestDto;
import com.campusfind.campusfind.dto.CreateLostItemResponseDto;
import com.campusfind.campusfind.dto.LostItemResponseDto;
import com.campusfind.campusfind.dto.UpdateLostItemRequestDto;
import com.campusfind.campusfind.service.LostItemService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lost-items")
public class LostItemController {

    private final LostItemService lostItemService;

    public LostItemController(LostItemService lostItemService) {
        this.lostItemService = lostItemService;
    }

    // CREATE: Naya lost item report karo
    @PostMapping
    public ResponseEntity<CreateLostItemResponseDto> createLostItem(
            @Valid @RequestBody CreateLostItemRequestDto request) {

        CreateLostItemResponseDto response =
                lostItemService.createLostItem(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET ALL: Saare lost items
    @GetMapping
    public ResponseEntity<List<LostItemResponseDto>> getAllLostItems() {

        return ResponseEntity.ok(
                lostItemService.getAllLostItems());
    }

    // MY REPORTS: Sirf logged-in user ke reported items
    @GetMapping("/my-reports")
    public ResponseEntity<List<LostItemResponseDto>> getMyReports() {

        return ResponseEntity.ok(
                lostItemService.getMyReports());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LostItemResponseDto> getLostItemById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                lostItemService.getLostItemById(id));
    }

    // PUT: Poora item update karo
    @PutMapping("/{id}")
    public ResponseEntity<LostItemResponseDto> updateLostItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLostItemRequestDto request) {

        return ResponseEntity.ok(
                lostItemService.updateLostItem(id, request));
    }

    // PATCH: Sirf selected fields update karo
    @PatchMapping("/{id}")
    public ResponseEntity<LostItemResponseDto> patchLostItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(
                lostItemService.patchLostItem(id, updates));
    }

    // DELETE: Item delete karo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLostItem(
            @PathVariable Long id) {

        lostItemService.deleteLostItem(id);

        return ResponseEntity.noContent().build();
    }

    // PAGINATION AND SORTING
    @GetMapping("/page")
    public ResponseEntity<?> getLostItemsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(
                lostItemService.getLostItemsWithPagination(
                        page, size, sortBy, direction));
    }

    // SEARCH: Item name ya description se
    @GetMapping("/search")
    public ResponseEntity<List<LostItemResponseDto>> searchLostItems(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                lostItemService.searchLostItems(keyword));
    }

    // FILTER: LOST ya FOUND status ke basis par
    @GetMapping("/filter")
    public ResponseEntity<List<LostItemResponseDto>> filterLostItemsByStatus(
            @RequestParam String status) {

        return ResponseEntity.ok(
                lostItemService.filterLostItemsByStatus(status));
    }
}
