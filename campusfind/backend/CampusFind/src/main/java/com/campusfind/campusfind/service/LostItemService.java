
package com.campusfind.campusfind.service;

import com.campusfind.campusfind.dto.CreateLostItemRequestDto;
import com.campusfind.campusfind.dto.CreateLostItemResponseDto;
import com.campusfind.campusfind.dto.LostItemResponseDto;
import com.campusfind.campusfind.dto.UpdateLostItemRequestDto;
import com.campusfind.campusfind.entity.LostItem;
import com.campusfind.campusfind.entity.User;
import com.campusfind.campusfind.exception.LostItemNotFoundException;
import com.campusfind.campusfind.repository.LostItemRepository;
import com.campusfind.campusfind.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class LostItemService {

    private final LostItemRepository lostItemRepository;
    private final UserRepository userRepository;

    public LostItemService(
            LostItemRepository lostItemRepository,
            UserRepository userRepository) {

        this.lostItemRepository = lostItemRepository;
        this.userRepository = userRepository;
    }

    // Logged-in user ko database se find karo
    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Please login first");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Logged-in user not found"));
    }

    // ADMIN kisi bhi item ko manage kar sakta hai
    private boolean isAdmin() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }

    // Item find karo aur ownership check karo
    private LostItem getItemForCurrentUser(Long id) {

        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() ->
                        new LostItemNotFoundException(
                                "Lost item not found with id: " + id));

        if (!isAdmin()) {
            User loggedInUser = getLoggedInUser();

            if (item.getUser() == null
                    || !item.getUser().getId()
                    .equals(loggedInUser.getId())) {

                throw new AccessDeniedException(
                        "You can manage only your own lost items");
            }
        }

        return item;
    }

    // CREATE: Naya lost item report karo
    public CreateLostItemResponseDto createLostItem(
            CreateLostItemRequestDto request) {

        User loggedInUser = getLoggedInUser();

        LostItem item = new LostItem();
        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setLocation(request.getLocation());
        item.setStatus(request.getStatus());
        item.setLostDate(request.getLostDate());
        item.setLostTime(request.getLostTime());
        item.setUser(loggedInUser);

        LostItem savedItem = lostItemRepository.save(item);

        return new CreateLostItemResponseDto(
                savedItem.getId(),
                savedItem.getItemName(),
                savedItem.getDescription(),
                savedItem.getLocation(),
                savedItem.getStatus(),
                savedItem.getLostDate(),
                savedItem.getLostTime(),
                savedItem.getCreatedAt()
        );
    }

    // GET ALL: Latest ID wala item sabse pehle
    public List<LostItemResponseDto> getAllLostItems() {

        return lostItemRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    // MY REPORTS: Sirf logged-in user ke apne items
    public List<LostItemResponseDto> getMyReports() {

        User loggedInUser = getLoggedInUser();

        return lostItemRepository
                .findByUser_IdOrderByIdDesc(loggedInUser.getId())
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    // GET ALL WITH PAGINATION AND SORTING
    public Page<LostItemResponseDto> getLostItemsWithPagination(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100");
        }

        if (sortBy == null || !List.of(
                "id",
                "itemName",
                "location",
                "status",
                "lostDate",
                "lostTime",
                "createdAt"
        ).contains(sortBy)) {

            throw new IllegalArgumentException("Invalid sort field");
        }

        Sort sort;

        if ("asc".equalsIgnoreCase(direction)) {
            sort = Sort.by(sortBy).ascending();
        } else if ("desc".equalsIgnoreCase(direction)) {
            sort = Sort.by(sortBy).descending();
        } else {
            throw new IllegalArgumentException(
                    "Direction must be asc or desc");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return lostItemRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }

    // GET BY ID
    public LostItemResponseDto getLostItemById(Long id) {

        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() ->
                        new LostItemNotFoundException(
                                "Lost item not found with id: " + id));

        return convertToResponseDto(item);
    }

    // PUT: Poora item update karo
    @Transactional
    public LostItemResponseDto updateLostItem(
            Long id,
            UpdateLostItemRequestDto request) {

        LostItem item = getItemForCurrentUser(id);

        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setLocation(request.getLocation());
        item.setStatus(request.getStatus());
        item.setLostDate(request.getLostDate());
        item.setLostTime(request.getLostTime());

        LostItem updatedItem = lostItemRepository.save(item);

        return convertToResponseDto(updatedItem);
    }

    // PATCH: Sirf selected fields update karo
    @Transactional
    public LostItemResponseDto patchLostItem(
            Long id,
            Map<String, Object> updates) {

        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one field is required");
        }

        LostItem item = getItemForCurrentUser(id);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {

            String field = entry.getKey();
            Object value = entry.getValue();

            switch (field) {
                case "itemName" ->
                        item.setItemName(requireText(field, value));

                case "description" ->
                        item.setDescription(requireText(field, value));

                case "location" ->
                        item.setLocation(requireText(field, value));

                case "status" ->
                        item.setStatus(requireText(field, value));

                case "lostDate" ->
                        item.setLostDate(
                                LocalDate.parse(requireText(field, value)));

                case "lostTime" ->
                        item.setLostTime(
                                LocalTime.parse(requireText(field, value)));

                default -> throw new IllegalArgumentException(
                        "Field cannot be updated: " + field);
            }
        }

        LostItem updatedItem = lostItemRepository.save(item);

        return convertToResponseDto(updatedItem);
    }

    // PATCH field ke liye non-empty string check
    private String requireText(String field, Object value) {

        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException(
                    field + " must be a non-empty string");
        }

        return text;
    }

    // DELETE: Owner ya ADMIN
    @Transactional
    public void deleteLostItem(Long id) {

        LostItem item = getItemForCurrentUser(id);
        lostItemRepository.delete(item);
    }

    // Entity -> Response DTO
    private LostItemResponseDto convertToResponseDto(LostItem item) {

        return new LostItemResponseDto(
                item.getId(),
                item.getItemName(),
                item.getDescription(),
                item.getLocation(),
                item.getStatus(),
                item.getLostDate(),
                item.getLostTime(),
                item.getCreatedAt()
        );
    }

    // SEARCH: Item name ya description se search karo
    public List<LostItemResponseDto> searchLostItems(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty");
        }

        return lostItemRepository
                .findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        keyword, keyword)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    // FILTER: Status ke basis par items filter karo
    public List<LostItemResponseDto> filterLostItemsByStatus(
            String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Status cannot be empty");
        }

        if (!status.equalsIgnoreCase("LOST")
                && !status.equalsIgnoreCase("FOUND")) {

            throw new IllegalArgumentException(
                    "Status must be LOST or FOUND");
        }

        return lostItemRepository
                .findByStatusIgnoreCase(status)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }
}
