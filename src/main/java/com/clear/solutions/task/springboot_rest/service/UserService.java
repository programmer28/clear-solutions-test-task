package com.clear.solutions.task.springboot_rest.service;

import com.clear.solutions.task.springboot_rest.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    void saveUser(User user);

    User getUser(int id);

    void deleteUser(int id);

    List<User> getUsersByDateBetween(LocalDate fromDate, LocalDate toDate);
}
