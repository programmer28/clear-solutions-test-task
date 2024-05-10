package com.clear.solutions.task.springboot_rest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AgeLimitValidator implements ConstraintValidator<AgeLimit, LocalDate> {
    int minimumAge;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext constraintValidatorContext) {
        if (birthDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate minimumAgeYearsAgo = today.minusYears(this.minimumAge);
        return !minimumAgeYearsAgo.isBefore(birthDate);
    }

    @Override
    public void initialize(AgeLimit constraintAnnotation) {
        this.minimumAge = constraintAnnotation.minimumAge();
    }
}
