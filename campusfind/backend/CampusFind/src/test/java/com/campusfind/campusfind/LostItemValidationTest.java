
package com.campusfind.campusfind;

import com.campusfind.campusfind.controller.LostItemController;
import com.campusfind.campusfind.service.LostItemService;
import com.campusfind.campusfind.service.JwtService;
import com.campusfind.campusfind.security.JwtAuthenticationFilter;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LostItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LostItemValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LostItemService lostItemService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void updateLostItemWithEmptyItemNameTest() throws Exception {

        mockMvc.perform(put("/api/lost-items/1")
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
    void updateLostItemWithEmptyDescriptionTest() throws Exception {

        mockMvc.perform(put("/api/lost-items/1")
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
    void updateLostItemWithEmptyLocationTest() throws Exception {

        mockMvc.perform(put("/api/lost-items/1")
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

    @Test
    void updateLostItemValidationErrorResponseTest() throws Exception {

        mockMvc.perform(put("/api/lost-items/1")
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.itemName")
                        .value("Item name is required"));
    }

}
