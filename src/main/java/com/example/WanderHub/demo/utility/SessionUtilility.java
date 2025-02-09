package com.example.WanderHub.demo.utility;

import jakarta.servlet.http.HttpSession;


public class SessionUtilility {

    public static boolean isLogged(HttpSession session, String username) {

        String currentUsername = (String) session.getAttribute("user");

        return !(currentUsername == null || !currentUsername.equals(username));
    }

    public static boolean isAdmin(HttpSession session) {

        String currentUsername = (String) session.getAttribute("user");

        if (currentUsername.equals("admin")) {
            return true;
        }
        return false;
    }
}
