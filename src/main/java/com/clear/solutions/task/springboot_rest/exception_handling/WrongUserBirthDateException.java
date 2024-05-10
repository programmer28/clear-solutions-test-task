package com.clear.solutions.task.springboot_rest.exception_handling;

public class WrongUserBirthDateException extends RuntimeException {
    public WrongUserBirthDateException(String message) {
        super(message);
    }
}
