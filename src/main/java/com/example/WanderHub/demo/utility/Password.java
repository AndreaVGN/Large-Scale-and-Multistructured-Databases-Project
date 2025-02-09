package com.example.WanderHub.demo.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Password {
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error with hashing of the password!", e);
        }
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        String hashedPassword = hashPassword(rawPassword);
        return hashedPassword.equals(storedPassword);
    }
}
