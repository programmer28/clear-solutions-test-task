package com.clear.solutions.task.springboot_rest.dao;

import com.clear.solutions.task.springboot_rest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByBirthDateBetween(LocalDate fromDate, LocalDate toDate);
}
