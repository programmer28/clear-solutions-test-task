package com.clear.solutions.task.springboot_rest.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeLimitValidator.class)
public @interface AgeLimit {
    int default_minimum_age = 18;
    int minimumAge() default default_minimum_age;
    String message() default "User must be at least " + default_minimum_age + " years old";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
