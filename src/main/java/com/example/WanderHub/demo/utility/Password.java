package com.example.WanderHub.demo.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Password {
    // Funzione per hashare la password con SHA-256
    public static String hashPassword(String password) {
        try {
            // Crea una istanza dell'algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Esegui l'hash sulla password (convertita in byte[])
            byte[] hashedBytes = digest.digest(password.getBytes());

            // Converti i byte risultanti in una stringa esadecimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Restituisce la password hashata come stringa esadecimale
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore with hashing of the password!", e);
        }
    }

    // Funzione per verificare la password hashata
    public static boolean matches(String rawPassword, String storedPassword) {
        // Hash della password fornita dall'utente e confronto con quella memorizzata
        String hashedPassword = hashPassword(rawPassword);
        return hashedPassword.equals(storedPassword);
    }
}
