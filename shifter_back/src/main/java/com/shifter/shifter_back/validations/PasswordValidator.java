package com.shifter.shifter_back.validations;

import com.shifter.shifter_back.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // Define regex patterns for each validation rule
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String DIGIT_PATTERN = ".*\\d.*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[@$!%*?&#].*";


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            addConstraintViolation(context, "Password must not be empty.");
            return false;
        }

        if (password.length() < 8 || password.length() > 20) {
            addConstraintViolation(context, "Password must be between 8 and 20 characters.");
            return false;
        }

        if (!Pattern.matches(UPPERCASE_PATTERN, password)) {
            addConstraintViolation(context, "Password must contain at least one uppercase letter.");
            return false;
        }

        if (!Pattern.matches(LOWERCASE_PATTERN, password)) {
            addConstraintViolation(context, "Password must contain at least one lowercase letter.");
            return false;
        }

        if (!Pattern.matches(DIGIT_PATTERN, password)) {
            addConstraintViolation(context, "Password must contain at least one digit.");
            return false;
        }

        if (!Pattern.matches(SPECIAL_CHAR_PATTERN, password)) {
            addConstraintViolation(context, "Password must contain at least one special character (@, $, !, %, *, ?, &, #).");
            return false;
        }

        return true; // Password meets all criteria
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
