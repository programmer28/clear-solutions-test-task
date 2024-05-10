package com.clear.solutions.task.springboot_rest.controller;


import com.clear.solutions.task.springboot_rest.entity.User;
import com.clear.solutions.task.springboot_rest.exception_handling.NoSuchUserException;
import com.clear.solutions.task.springboot_rest.exception_handling.WrongUserBirthDateException;
import com.clear.solutions.task.springboot_rest.service.UserService;
import com.clear.solutions.task.springboot_rest.validation.AgeLimit;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRESTController {
    @Autowired
    private UserService userService;

    @GetMapping(path="/users", produces="application/json")
    public List<User> showAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return allUsers;
    }

    @GetMapping(path="/users/{id}", produces="application/json")
    public User getUser(@PathVariable("id") int id) {

        User user = userService.getUser(id);
        if (user == null) {
            throw new NoSuchUserException("There is no user with ID = "
            + id + " in Database");
        }
        return user;
    }

    @PostMapping(path="/users", produces="application/json")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody User user,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        userService.saveUser(user);
        return ResponseEntity.ok("User registered succesfully!");
    }

    @PutMapping(path="/users", produces="application/json")
    public User updateUser(@Valid @RequestBody User user) {
        userService.saveUser(user);
        return user;
    }

    @DeleteMapping(path="/users/{id}", produces="application/json")
    public String deleteUserById(@PathVariable("id") int id) {
        User user = userService.getUser(id);
        if (user == null) {
            throw new NoSuchUserException("There is no user with ID = "
            + id + " in Database");
        }
        userService.deleteUser(id);
        return "User with ID = " + id + " was deleted";
    }

    @GetMapping(path="/users/{from}/{to}")
    public List<User> getUsersByDateBetween(
            @PathVariable("from") @AgeLimit LocalDate from,
            @PathVariable("to") @AgeLimit LocalDate to) {
        if (from.isAfter(to)) {
            throw new WrongUserBirthDateException(
                    "Wrong incoming data: the date_from = " + from.toString() +
                    " should be earlier than date_to = " + to.toString());
        }
        List<User> users = userService.getUsersByDateBetween(from, to);
        return users;
    }
}
