package com.clear.solutions.task.springboot_rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clear.solutions.task.springboot_rest.entity.User;
import com.clear.solutions.task.springboot_rest.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(UserRESTController.class)
public class UserRESTControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void showAllUsers() throws Exception {
        List<User> users = getUsersListForTest();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.getAllUsers()).thenReturn(users);
        MvcResult mvcResult = mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        String stringJson = mvcResult.getResponse().getContentAsString();
        User[] userList = objectMapper.readValue(stringJson, User[].class);
        assertTrue(userList.length == 3);
    }

    @Test
    void getUser() throws Exception {
        User user = getUserForTest();
        when(userService.getUser(1)).thenReturn(user);
        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("aabbcc@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Petro"))
                .andExpect(jsonPath("$.lastName").value("Petrenko"))
                .andExpect(jsonPath("$.birthDate").value("1970-04-03"))
                .andExpect(jsonPath("$.address").value("Shevchenka 123"))
                .andExpect(jsonPath("$.phoneNumber").value("380954123259"));
    }

    @Test
    void getUserByWrongId() throws Exception {
        int wrongId = 100;
        MvcResult mvcResult = mockMvc.perform(get("/api/users/{id}", wrongId))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertNotEquals(200, status);
        assertEquals("There is no user with ID = " + wrongId + " in Database",
                mvcResult.getResolvedException().getMessage());
        System.out.println(mvcResult.getResolvedException().getMessage());
    }

    @Test
    void addNewUser() throws Exception {
        User user = getUserForTest();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andReturn();
        String message = mvcResult.getResponse().getContentAsString();
        assertEquals("User registered succesfully!", message);
    }

    @Test
    void addNewUserWrongBirthDate() throws Exception {
        User user = getUserForTest();
        user.setBirthDate(LocalDate.of(2010,1,1)); //User is younger than 18 years old
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertNotEquals(200, status);
        assertEquals("User must be at least 18 years old; ", mvcResult.getResponse().getContentAsString());
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void addNewUserWrongEmail() throws Exception {
        User user = getUserForTest();
        user.setEmail("abc#gmail.com"); //User with wrong email
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertNotEquals(200, status);
        assertEquals("Email is not valid; ", mvcResult.getResponse().getContentAsString());
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void updateUser() throws Exception {
        User user = getUserForTest();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        String changedName = "Mykola";
        String changedBirthDate = "1990-01-01";
        userJson = userJson.replaceFirst(user.getFirstName(), changedName); //Replace firstName for changedFirstName
        userJson = userJson.replaceFirst(user.getBirthDate().toString(), changedBirthDate); //Replace birthDate for changedBirthDate
        //Save and update changed json
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("aabbcc@gmail.com"))
                .andExpect(jsonPath("$.firstName").value(changedName))
                .andExpect(jsonPath("$.lastName").value("Petrenko"))
                .andExpect(jsonPath("$.birthDate").value(changedBirthDate))
                .andExpect(jsonPath("$.address").value("Shevchenka 123"))
                .andExpect(jsonPath("$.phoneNumber").value("380954123259"));
    }

    @Test
    void updateUserWrongDate() throws Exception {
        User user = getUserForTest();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        String changedBirthDate = "2015-01-01";
        userJson = userJson.replaceFirst(user.getBirthDate().toString(), changedBirthDate); //Replace birthDate for changedBirthDate
        //Try to save and update changed json by wrong birthDate
        MvcResult mvcResult = mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertNotEquals(200, status);
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void deleteUserById() throws Exception {
        User user = getUserForTest();
        int id = 1;
        when(userService.getUser(id)).thenReturn(user);
        MvcResult mvcResult = mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isOk()).andReturn();
        String message = mvcResult.getResponse().getContentAsString();
        assertEquals(message, "User with ID = " + id + " was deleted");
    }

    @Test
    void deleteUserByWrongId() throws Exception {
        User user = getUserForTest();
        int id = 1;
        when(userService.getUser(id)).thenReturn(user);
        int wrongId = 20;
        MvcResult mvcResult = mockMvc.perform(delete("/api/users/{id}", wrongId))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertNotEquals(200, status);
        assertEquals("There is no user with ID = " + wrongId + " in Database",
                mvcResult.getResolvedException().getMessage());
        System.out.println(mvcResult.getResolvedException().getMessage());
    }

    @Test
    void getUsersByDateBetween() throws Exception {
        LocalDate from = LocalDate.of(2001,1,1);
        LocalDate to = LocalDate.of(2002,1,1);
        mockMvc.perform(get("/api/users/{from}/{to}", from, to))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByDateBetweenWrongDate() throws Exception {
        LocalDate from = LocalDate.of(2020,1,1);
        LocalDate to = LocalDate.of(2021,1,1);
        MvcResult mvcResult = mockMvc.perform(get("/api/users/{from}/{to}", from, to))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertNotEquals(200, status);
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getUsersByDateBetweenWrongOrderDateParameters() throws Exception {
        LocalDate from = LocalDate.of(1989,1,1);
        LocalDate to = LocalDate.of(1979,1,1);
        MvcResult mvcResult = mockMvc.perform(get("/api/users/{from}/{to}", from, to))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertNotEquals(200, status);
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    private User getUserForTest() {
        User user = new User();
        user.setId(1);
        user.setEmail("aabbcc@gmail.com");
        user.setFirstName("Petro");
        user.setLastName("Petrenko");
        user.setBirthDate(LocalDate.of(1970,4,3));
        user.setAddress("Shevchenka 123");
        user.setPhoneNumber("380954123259");
        return user;
    }

    private List<User> getUsersListForTest() {
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("aabbcc@gmail.com");
        user1.setFirstName("Petro");
        user1.setLastName("Petrenko");
        user1.setBirthDate(LocalDate.of(1995,4,3));
        user1.setAddress("Shevchenka 123");
        user1.setPhoneNumber("380954123259");
        User user2 = new User();
        user2.setId(2);
        user2.setEmail("abc@gmail.com");
        user2.setFirstName("Ivan");
        user2.setLastName("Ivanenko");
        user2.setBirthDate(LocalDate.of(2000,1,1));
        user2.setAddress("Shevchenka 3");
        user2.setPhoneNumber("380954573259");
        User user3 = new User();
        user3.setId(3);
        user3.setEmail("abcd@gmail.com");
        user3.setFirstName("Vasyl");
        user3.setLastName("Sydorenko");
        user3.setBirthDate(LocalDate.of(1985,2,18));
        user3.setAddress("Franka 45");
        user3.setPhoneNumber("380503573259");
        return List.of(user1, user2, user3);
    }
}
