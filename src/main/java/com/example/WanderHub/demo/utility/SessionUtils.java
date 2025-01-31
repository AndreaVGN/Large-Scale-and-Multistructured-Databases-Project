package com.example.WanderHub.demo.utility;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    // Funzione per verificare se l'utente è loggato
    public static boolean isLogged(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}
