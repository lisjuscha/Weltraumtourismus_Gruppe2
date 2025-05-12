package com.example.flightprep.util;

import com.example.flightprep.model.User;

public class SessionManager {
    private static User currentUser;
    private static String selectedPatientId;

    public static void setCurrentUser(User user) {
        SessionManager.currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentUserId() {
        return currentUser.getUserId();
    }

    public static void setSelectedPatientId(String patientId) {SessionManager.selectedPatientId = patientId;}

    public static String getSelectedPatientId() {return selectedPatientId;}

    public static void clear() {
        currentUser = null;
    }
}