package com.example.flightprep.util;

import com.example.flightprep.model.User;

public class SessionManager {
    private static User currentUser = new User("Da", "1", "customer");

    public static void setCurrentUser(User user) {
        SessionManager.currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentUserId() {
        return currentUser.getId();
    }
    public static void clear() {
        currentUser = null;
    }
}