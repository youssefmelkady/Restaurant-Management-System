package com.restaurant.utils;

import com.restaurant.exceptions.ValidationException;

public class ValidationUtils {

    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        if (!hasUpper) throw new ValidationException("Password must contain an uppercase letter.");
        if (!hasLower) throw new ValidationException("Password must contain a lowercase letter.");
        if (!hasDigit) throw new ValidationException("Password must contain a digit.");
        if (!hasSpecial) throw new ValidationException("Password must contain a special character.");
    }

    public static void validatePhoneNumber(String phone) throws ValidationException {
        if (phone == null || phone.length() != 11) {
            throw new ValidationException("Phone number must be exactly 11 digits.");
        }
        if (!phone.startsWith("01")) {
            throw new ValidationException("Phone number must start with 01.");
        }
        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new ValidationException("Phone number must contain digits only.");
            }
        }
    }
}