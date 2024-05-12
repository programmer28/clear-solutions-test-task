package com.clear.solutions.task.springboot_rest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public class AgeLimitValidator implements ConstraintValidator<AgeLimit, LocalDate> {
    @Value(value = "${user.age}")
    String value;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext constraintValidatorContext) {
        if (birthDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate minimumAgeYearsAgo = today.minusYears(Integer.valueOf(this.value));
        return !minimumAgeYearsAgo.isBefore(birthDate);
    }

    @Override
    public void initialize(AgeLimit constraintAnnotation) {
    }
}