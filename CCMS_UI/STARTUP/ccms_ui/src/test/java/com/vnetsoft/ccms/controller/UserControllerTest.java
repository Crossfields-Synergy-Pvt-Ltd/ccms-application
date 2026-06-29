package com.vnetsoft.ccms.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vnetsoft.ccms.pojo.Status;
import com.vnetsoft.ccms.pojo.User;
import com.vnetsoft.ccms.services.UserServices;

public class UserControllerTest extends AbstractControllerTest {

    @Mock
    private UserServices userServices;

    @InjectMocks
    private UserController controller;

    @Before
    @Override
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configureController(controller);
    }

    // --- login (hardcoded, no MongoDB dependency) ---

    @Test
    public void testLogin_AdminValidCredentials_ReturnsUserWithStatus100() throws Exception {
        performGet("/superadmin/user/login?name=admin@example.com&password=admin123")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("100")))
            .andExpect(jsonPath("$.email", is("admin@example.com")))
            .andExpect(jsonPath("$.role", is("ADMIN")))
            .andExpect(jsonPath("$.monitor_and_controller", is(true)))
            .andExpect(jsonPath("$.history", is(true)));
    }

    @Test
    public void testLogin_UserValidCredentials_ReturnsUserWithStatus100() throws Exception {
        performGet("/superadmin/user/login?name=user@example.com&password=user123")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("100")))
            .andExpect(jsonPath("$.email", is("user@example.com")))
            .andExpect(jsonPath("$.role", is("USER")))
            .andExpect(jsonPath("$.monitor_and_controller", is(false)))
            .andExpect(jsonPath("$.history", is(false)));
    }

    @Test
    public void testLogin_InvalidPassword_ReturnsUserWithStatus00() throws Exception {
        performGet("/superadmin/user/login?name=admin@example.com&password=wrong")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("00")))
            .andExpect(jsonPath("$.email", is("admin@example.com")));
    }

    @Test
    public void testLogin_PasswordClearedInResponse() throws Exception {
        performGet("/superadmin/user/login?name=admin@example.com&password=admin123")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.password", is("")));
    }

    @Test
    public void testLogin_UnknownUser_ReturnsStatus00() throws Exception {
        performGet("/superadmin/user/login?name=unknown@test.com&password=test")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("00")))
            .andExpect(jsonPath("$.password", is("")));
    }

    // --- create user ---

    @Test
    public void testCreateUser_ValidUser_ReturnsStatus200() throws Exception {
        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setPassword("pass123");
        newUser.setRole("USER");
        newUser.setFirstName("Test");
        newUser.setLastName("User");

        when(userServices.addEntity(any(User.class))).thenReturn(true);

        performPost("/superadmin/user/create", toJson(newUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.message", is("Success")));
    }

    @Test
    public void testCreateUser_ServiceThrows_ReturnsErrorStatus() throws Exception {
        User newUser = new User();
        newUser.setEmail("fail@test.com");

        when(userServices.addEntity(any(User.class))).thenThrow(new RuntimeException("Save failed"));

        performPost("/superadmin/user/create", toJson(newUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    public void testCreateUser_EmptyEmail_StillProcesses() throws Exception {
        User newUser = new User();
        newUser.setPassword("pass");

        when(userServices.addEntity(any(User.class))).thenReturn(true);

        performPost("/superadmin/user/create", toJson(newUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(200)));
    }

    // --- list users ---

    @Test
    public void testGetAllUsers_WithAuth_ReturnsUserList() throws Exception {
        User user1 = new User();
        user1.setEmail("admin@test.com");
        user1.setRole("SUPER ADMIN");

        User user2 = new User();
        user2.setEmail("user@test.com");
        user2.setRole("USER");

        when(userServices.getEntityList()).thenReturn(Arrays.asList(user1, user2));

        performGetWithAuth("/superadmin/user/list", "dGVzdDp0ZXN0")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].email", is("admin@test.com")))
            .andExpect(jsonPath("$[1].email", is("user@test.com")));
    }

    @Test
    public void testGetAllUsers_NoAuthHeader_Returns400() throws Exception {
        performGet("/superadmin/user/list")
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetAllUsers_EmptyList_ReturnsEmptyArray() throws Exception {
        when(userServices.getEntityList()).thenReturn(Arrays.asList());

        performGetWithAuth("/superadmin/user/list", "dGVzdDp0ZXN0")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- delete user ---

    @Test
    public void testDeleteUser_ExistingId_ReturnsSuccess() throws Exception {
        when(userServices.deleteEntity("user@test.com")).thenReturn(true);

        performDelete("/superadmin/user/delete/user@test.com")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(1)))
            .andExpect(jsonPath("$.message", containsString("deleted")));
    }

    @Test
    public void testDeleteUser_ServiceThrows_ReturnsError() throws Exception {
        when(userServices.deleteEntity("bad-id")).thenThrow(new RuntimeException("Delete failed"));

        performDelete("/superadmin/user/delete/bad-id")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)));
    }
}
