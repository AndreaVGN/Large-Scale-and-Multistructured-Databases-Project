package com.example.WanderHub.demo.utility;

import jakarta.servlet.http.HttpSession;


public class SessionUtilility {

    // Funzione per verificare se l'utente è loggato
    public static boolean isLogged(HttpSession session, String username) {

        String currentUsername = (String) session.getAttribute("user");

        return !(currentUsername == null || !currentUsername.equals(username));
    }
}
