package com.mavi.projectmanager.controller.utils;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    //Method for login that can be used across all Controllers
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}