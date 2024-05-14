package com.clear.solutions.task.springboot_rest.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeLimitValidator.class)
public @interface AgeLimit {
    String message() default "";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
