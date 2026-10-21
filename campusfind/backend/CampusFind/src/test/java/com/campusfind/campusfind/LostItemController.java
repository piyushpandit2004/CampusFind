package com.campusfind.campusfind;

import com.campusfind.campusfind.controller.LostItemController;
import com.campusfind.campusfind.dto.CreateLostItemRequestDto;
import com.campusfind.campusfind.dto.CreateLostItemResponseDto;
import com.campusfind.campusfind.dto.LostItemResponseDto;
import com.campusfind.campusfind.dto.UpdateLostItemRequestDto;
import com.campusfind.campusfind.security.JwtAuthenticationFilter;
import com.campusfind.campusfind.service.JwtService;
import com.campusfind.campusfind.service.LostItemService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LostItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class LostItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LostItemService lostItemService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    // 1. Get all lost items
    @Test
    void getAllLostItemsTest() throws Exception {

        doReturn(Collections.emptyList())
                .when(lostItemService)
                .getAllLostItems();

        mockMvc.perform(get("/api/lost-items"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(lostItemService).getAllLostItems();
    }


    // 2. Get lost item by ID
    @Test
    void getLostItemByIdTest() throws Exception {

        LostItemResponseDto response =
                mock(LostItemResponseDto.class);

        doReturn(response)
                .when(lostItemService)
                .getLostItemById(1L);

        mockMvc.perform(get("/api/lost-items/{id}", 1L))
                .andExpect(status().isOk());

        verify(lostItemService).getLostItemById(1L);
    }


    // 3. Create lost item
    @Test
    void createLostItemTest() throws Exception {

        CreateLostItemResponseDto response =
                mock(CreateLostItemResponseDto.class);

        doReturn(response)
                .when(lostItemService)
                .createLostItem(any(CreateLostItemRequestDto.class));

        String requestJson = """
                {
                    "itemName": "Wallet",
                    "description": "Black wallet",
                    "location": "Library",
                    "status": "LOST",
                    "lostDate": "2026-10-19",
                    "lostTime": "10:30:00"
                }
                """;

        mockMvc.perform(post("/api/lost-items")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isCreated());

        verify(lostItemService)
                .createLostItem(any(CreateLostItemRequestDto.class));
    }


    // 4. Update lost item
    @Test
    void updateLostItemTest() throws Exception {

        LostItemResponseDto response =
                mock(LostItemResponseDto.class);

        doReturn(response)
                .when(lostItemService)
                .updateLostItem(
                        eq(1L),
                        any(UpdateLostItemRequestDto.class)
                );

        String requestJson = """
                {
                    "itemName": "Updated Wallet",
                    "description": "Black leather wallet",
                    "location": "Library",
                    "status": "FOUND",
                    "lostDate": "2026-10-19",
                    "lostTime": "10:30:00"
                }
                """;

        mockMvc.perform(put("/api/lost-items/{id}", 1L)
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk());

        verify(lostItemService)
                .updateLostItem(
                        eq(1L),
                        any(UpdateLostItemRequestDto.class)
                );
    }


    // 5. Partially update lost item
    @Test
    void patchLostItemTest() throws Exception {

        LostItemResponseDto response =
                mock(LostItemResponseDto.class);

        doReturn(response)
                .when(lostItemService)
                .patchLostItem(eq(1L), any(Map.class));

        String requestJson = """
                {
                    "status": "FOUND"
                }
                """;

        mockMvc.perform(patch("/api/lost-items/{id}", 1L)
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk());

        verify(lostItemService)
                .patchLostItem(eq(1L), any(Map.class));
    }


    // 6. Delete lost item
    @Test
    void deleteLostItemTest() throws Exception {

        doNothing()
                .when(lostItemService)
                .deleteLostItem(1L);

        mockMvc.perform(delete("/api/lost-items/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(lostItemService).deleteLostItem(1L);
    }

// 7. Pagination
    @Test
    void getLostItemsWithPaginationTest() throws Exception {

        org.springframework.data.domain.Page<LostItemResponseDto> emptyPage =
                org.springframework.data.domain.Page.empty(
                        org.springframework.data.domain.PageRequest.of(
                                0,
                                10,
                                org.springframework.data.domain.Sort
                                        .by("createdAt")
                                        .descending()
                        )
                );

        doReturn(emptyPage)
                .when(lostItemService)
                .getLostItemsWithPagination(0, 10, "createdAt", "desc");

        mockMvc.perform(get("/api/lost-items/page")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk());

        verify(lostItemService)
                .getLostItemsWithPagination(0, 10, "createdAt", "desc");
    }

    // 8. Search lost items
    @Test
    void searchLostItemsTest() throws Exception {

        doReturn(Collections.emptyList())
                .when(lostItemService)
                .searchLostItems("wallet");

        mockMvc.perform(get("/api/lost-items/search")
                        .param("keyword", "wallet"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(lostItemService).searchLostItems("wallet");
    }


    // 9. Filter lost items by status
    @Test
    void filterLostItemsByStatusTest() throws Exception {

        doReturn(Collections.emptyList())
                .when(lostItemService)
                .filterLostItemsByStatus("LOST");

        mockMvc.perform(get("/api/lost-items/filter")
                        .param("status", "LOST"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(lostItemService)
                .filterLostItemsByStatus("LOST");
    }

    @Test
    void createLostItemWithEmptyItemNameTest() throws Exception {

        mockMvc.perform(post("/api/lost-items")
                        .contentType("application/json")
                        .content("""
                        {
                            "itemName": "",
                            "description": "Black wallet",
                            "location": "Library",
                            "status": "LOST",
                            "lostDate": "2026-10-20",
                            "lostTime": "10:30:00"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLostItemWithEmptyDescriptionTest() throws Exception {

        mockMvc.perform(post("/api/lost-items")
                        .contentType("application/json")
                        .content("""
                        {
                            "itemName": "Wallet",
                            "description": "",
                            "location": "Library",
                            "status": "LOST",
                            "lostDate": "2026-10-20",
                            "lostTime": "10:30:00"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLostItemWithEmptyLocationTest() throws Exception {

        mockMvc.perform(post("/api/lost-items")
                        .contentType("application/json")
                        .content("""
                        {
                            "itemName": "Wallet",
                            "description": "Black wallet",
                            "location": "",
                            "status": "LOST",
                            "lostDate": "2026-10-20",
                            "lostTime": "10:30:00"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }


}