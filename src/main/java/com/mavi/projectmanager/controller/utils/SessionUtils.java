package com.mavi.projectmanager.controller.utils;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Role;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    //Method for login that can be used across all Controllers
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("account") != null;
    }

    public static boolean userIsProjectLead(HttpSession session) {
        return ((Account) session.getAttribute("account")).getRole() == Role.PROJECT_LEAD;
    }
}