
package com.campusfind.campusfind.repository;

import com.campusfind.campusfind.entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostItemRepository
        extends JpaRepository<LostItem, Long> {

    // Search by item name or description
    List<LostItem> findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String itemName,
            String description);

    // Filter by status
    List<LostItem> findByStatusIgnoreCase(String status);

    // Get all items, newest ID first
    List<LostItem> findAllByOrderByIdDesc();

    // Get logged-in user's reports, newest first
    List<LostItem> findByUser_IdOrderByIdDesc(Long userId);
}
