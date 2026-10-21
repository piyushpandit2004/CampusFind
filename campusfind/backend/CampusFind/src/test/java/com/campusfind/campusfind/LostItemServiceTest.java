
package com.campusfind.campusfind;
import com.campusfind.campusfind.exception.LostItemNotFoundException;

import com.campusfind.campusfind.dto.CreateLostItemRequestDto;
import com.campusfind.campusfind.dto.UpdateLostItemRequestDto;
import com.campusfind.campusfind.entity.User;

import org.junit.jupiter.api.AfterEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.security.access.AccessDeniedException;


import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import com.campusfind.campusfind.dto.LostItemResponseDto;
import com.campusfind.campusfind.entity.LostItem;
import com.campusfind.campusfind.repository.LostItemRepository;
import com.campusfind.campusfind.repository.UserRepository;
import com.campusfind.campusfind.service.LostItemService;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LostItemServiceTest {

    private LostItemRepository lostItemRepository;
    private UserRepository userRepository;
    private LostItemService lostItemService;

    @BeforeEach
    void setUp() {
        lostItemRepository = mock(LostItemRepository.class);
        userRepository = mock(UserRepository.class);

        lostItemService = new LostItemService(
                lostItemRepository,
                userRepository
        );
    }

    @Test
    void getAllLostItemsTest() {

        LostItem item = new LostItem();
        item.setId(1L);
        item.setItemName("Black Wallet");
        item.setDescription("Black leather wallet");
        item.setLocation("Library");
        item.setStatus("LOST");

        when(lostItemRepository.findAll())
                .thenReturn(List.of(item));

        List<LostItemResponseDto> result =
                lostItemService.getAllLostItems();

        assertEquals(1, result.size());
        verify(lostItemRepository, times(1)).findAll();
    }
    @Test
    void searchLostItemsTest() {

        LostItem item = new LostItem();
        item.setId(2L);
        item.setItemName("Blue Water Bottle");
        item.setDescription("Blue bottle found in classroom");
        item.setLocation("Classroom");
        item.setStatus("FOUND");

        when(lostItemRepository
                .findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "bottle", "bottle"))
                .thenReturn(List.of(item));

        List<LostItemResponseDto> result =
                lostItemService.searchLostItems("bottle");

        assertEquals(1, result.size());
        verify(lostItemRepository, times(1))
                .findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "bottle", "bottle");
    }
    @Test
    void searchLostItemsWithEmptyKeywordTest() {

        assertThrows(
                IllegalArgumentException.class,
                () -> lostItemService.searchLostItems("")
        );

        verifyNoInteractions(lostItemRepository);
    }

    @Test
    void filterLostItemsByStatusTest() {

        LostItem item = new LostItem();
        item.setId(3L);
        item.setItemName("Black Wallet");
        item.setDescription("Wallet near library");
        item.setLocation("Library");
        item.setStatus("LOST");

        when(lostItemRepository.findByStatusIgnoreCase("LOST"))
                .thenReturn(List.of(item));

        List<LostItemResponseDto> result =
                lostItemService.filterLostItemsByStatus("LOST");

        assertEquals(1, result.size());

        verify(lostItemRepository,times(1))
                .findByStatusIgnoreCase("LOST");
    }
    @Test
    void filterLostItemsWithInvalidStatusTest() {

        assertThrows(
                IllegalArgumentException.class,
                () -> lostItemService.filterLostItemsByStatus("PENDING")
        );

        verifyNoInteractions(lostItemRepository);
    }

    @Test
    void getLostItemByIdTest() {
        LostItem item = new LostItem();
        item.setId(10L);
        item.setItemName("College ID Card");
        item.setDescription("ID card lost");
        item.setLocation("Library");
        item.setStatus("LOST");

        when(lostItemRepository.findById(10L))
                .thenReturn(java.util.Optional.of(item));

        LostItemResponseDto result =
                lostItemService.getLostItemById(10L);

        assertEquals(10L, result.getId());
        assertEquals("College ID Card", result.getItemName());

        verify(lostItemRepository).findById(10L);
    }

    @Test
    void getLostItemByIdNotFoundTest() {
        when(lostItemRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                LostItemNotFoundException.class,
                () -> lostItemService.getLostItemById(999L)
        );

        verify(lostItemRepository).findById(999L);
    }

    @Test
    void paginationTest() {
        LostItem item = new LostItem();
        item.setId(11L);
        item.setItemName("Notebook");
        item.setDescription("Blue notebook");
        item.setLocation("Classroom");
        item.setStatus("LOST");

        when(lostItemRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        var result = lostItemService.getLostItemsWithPagination(
                0, 10, "createdAt", "desc"
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Notebook", result.getContent().get(0).getItemName());

        verify(lostItemRepository).findAll(any(Pageable.class));
    }

    @Test
    void paginationWithNegativePageTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lostItemService.getLostItemsWithPagination(
                        -1, 10, "id", "asc"
                )
        );

        verifyNoInteractions(lostItemRepository);
    }

    @Test
    void paginationWithInvalidSizeTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lostItemService.getLostItemsWithPagination(
                        0, 0, "id", "asc"
                )
        );

        verifyNoInteractions(lostItemRepository);
    }

    @Test
    void paginationWithInvalidSortFieldTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lostItemService.getLostItemsWithPagination(
                        0, 10, "password", "asc"
                )
        );

        verifyNoInteractions(lostItemRepository);
    }

    @Test
    void paginationWithInvalidDirectionTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lostItemService.getLostItemsWithPagination(
                        0, 10, "id", "sideways"
                )
        );

        verifyNoInteractions(lostItemRepository);
    }

    @Test
    void patchWithEmptyUpdatesTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lostItemService.patchLostItem(
                        1L, java.util.Collections.emptyMap()
                )
        );

        verifyNoInteractions(lostItemRepository);
    }

    private User prepareLoggedInUser() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@example.com",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        return user;
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createLostItemTest() {
        User user = prepareLoggedInUser();

        CreateLostItemRequestDto request =
                mock(CreateLostItemRequestDto.class);

        when(request.getItemName()).thenReturn("Black Wallet");
        when(request.getDescription()).thenReturn("Leather wallet");
        when(request.getLocation()).thenReturn("Library");
        when(request.getStatus()).thenReturn("LOST");

        when(lostItemRepository.save(any(LostItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = lostItemService.createLostItem(request);

        assertEquals("Black Wallet", result.getItemName());

        org.mockito.ArgumentCaptor<LostItem> captor =
                org.mockito.ArgumentCaptor.forClass(LostItem.class);

        verify(lostItemRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
    }

    @Test
    void updateLostItemTest() {
        User user = prepareLoggedInUser();

        LostItem item = new LostItem();
        item.setId(1L);
        item.setItemName("Old Wallet");
        item.setDescription("Old description");
        item.setLocation("Library");
        item.setStatus("LOST");
        item.setUser(user);

        when(lostItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(lostItemRepository.save(any(LostItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateLostItemRequestDto request =
                mock(UpdateLostItemRequestDto.class);

        when(request.getItemName()).thenReturn("New Wallet");
        when(request.getDescription()).thenReturn("Updated description");
        when(request.getLocation()).thenReturn("Classroom");
        when(request.getStatus()).thenReturn("FOUND");

        var result = lostItemService.updateLostItem(1L, request);

        assertEquals("New Wallet", result.getItemName());
        assertEquals("FOUND", result.getStatus());

        verify(lostItemRepository).save(item);
    }

    @Test
    void patchLostItemTest() {
        User user = prepareLoggedInUser();

        LostItem item = new LostItem();
        item.setId(1L);
        item.setItemName("Old Wallet");
        item.setDescription("Leather wallet");
        item.setLocation("Library");
        item.setStatus("LOST");
        item.setUser(user);

        when(lostItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(lostItemRepository.save(any(LostItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var updates = java.util.Map.<String, Object>of(
                "status", "FOUND"
        );

        var result = lostItemService.patchLostItem(1L, updates);

        assertEquals("FOUND", result.getStatus());
        assertEquals("Old Wallet", result.getItemName());

        verify(lostItemRepository).save(item);
    }

    @Test
    void deleteLostItemTest() {
        User user = prepareLoggedInUser();

        LostItem item = new LostItem();
        item.setId(1L);
        item.setItemName("Black Wallet");
        item.setUser(user);

        when(lostItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        lostItemService.deleteLostItem(1L);

        verify(lostItemRepository, times(1)).delete(item);
    }


}
