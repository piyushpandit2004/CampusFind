
package com.campusfind.campusfind;

import com.campusfind.campusfind.service.JwtService;
import com.campusfind.campusfind.service.LostItemService;

import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LostItemService lostItemService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {

        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Please provide a valid JWT token"));
    }

    @Test
    void protectedEndpointWithInvalidTokenReturns401() throws Exception {

        when(jwtService.extractEmail("invalid.token.here"))
                .thenThrow(new MalformedJwtException("Invalid token"));

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Invalid or expired token"));
    }

    @Test
    void userRoleCannotAccessAdminEndpoint() throws Exception {

        org.mockito.Mockito.when(
                        jwtService.extractEmail("user.test.token"))
                .thenReturn("rahul@example.com");

        org.mockito.Mockito.when(
                        jwtService.extractRole("user.test.token"))
                .thenReturn("USER");

        org.mockito.Mockito.when(
                        jwtService.isTokenValid(
                                "user.test.token", "rahul@example.com"))
                .thenReturn(true);

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer user.test.token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRoleCanPassAdminAuthorization() throws Exception {

        org.mockito.Mockito.when(
                        jwtService.extractEmail("admin.test.token"))
                .thenReturn("amit2@example.com");

        org.mockito.Mockito.when(
                        jwtService.extractRole("admin.test.token"))
                .thenReturn("ADMIN");

        org.mockito.Mockito.when(
                        jwtService.isTokenValid(
                                "admin.test.token", "amit2@example.com"))
                .thenReturn(true);

        mockMvc.perform(get("/api/admin/test")
                        .header("Authorization", "Bearer admin.test.token"))
                .andExpect(status().isOk());
    }

}
