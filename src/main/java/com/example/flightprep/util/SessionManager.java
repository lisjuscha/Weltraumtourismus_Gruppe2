package com.example.flightprep.util;

import com.example.flightprep.model.User;

/**
 * The `SessionManager` class is a utility class that manages the session state
 * for the currently logged-in user and the selected patient in the Flight Preparation application.
 * It provides methods to set, retrieve, and clear session-related data.
 */
public class SessionManager {
    private static User currentUser;
    private static String selectedPatientId;

    /**
     * Sets the currently logged-in user.
     *
     * @param user The `User` object representing the logged-in user.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return The `User` object representing the logged-in user.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Retrieves the user ID of the currently logged-in user.
     *
     * @return A `String` representing the user ID of the logged-in user.
     */
    public static String getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    /**
     * Sets the ID of the selected patient.
     *
     * @param patientId A `String` representing the selected patient's ID.
     */
    public static void setSelectedPatientId(String patientId) {
        SessionManager.selectedPatientId = patientId;
    }

    /**
     * Retrieves the ID of the selected patient.
     *
     * @return A `String` representing the selected patient's ID.
     */
    public static String getSelectedPatientId() {return selectedPatientId;}

    /**
     * Clears the session by resetting the current user to `null`.
     */
    public static void clear() {
        currentUser = null;
        selectedPatientId = null; // Also clear the selected patient ID
    }
}