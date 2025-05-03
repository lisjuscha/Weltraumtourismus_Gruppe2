package com.example.flightprep;

import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.util.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class tst {
    public static void main(String[] args) {
        try (Connection connection = DbConnection.getConnection()) {
            AppointmentDAO appointmentDAO = new AppointmentDAO(connection);
            
            // Test 1: Buche einen neuen Termin
            LocalDate testDate = LocalDate.now().plusDays(1); // Termin für morgen
            String testTime = "10:00";
            
            System.out.println("Teste Terminbuchung für: " + testDate + " " + testTime);
            
            // Prüfe ob der Slot frei ist
            boolean isBooked = appointmentDAO.isSlotBooked(testDate, testTime);
            System.out.println("Ist der Slot bereits gebucht? " + isBooked);
            
            if (!isBooked) {
                // Buche den Termin
                appointmentDAO.bookAppointment(testDate, testTime);
                System.out.println("Termin wurde gebucht!");
                
                // Prüfe ob die Buchung erfolgreich war
                isBooked = appointmentDAO.isSlotBooked(testDate, testTime);
                System.out.println("Wurde der Slot erfolgreich gebucht? " + isBooked);
            }
            
            // Test 2: Versuche denselben Termin nochmal zu buchen (sollte fehlschlagen)
            try {
                appointmentDAO.bookAppointment(testDate, testTime);
                System.out.println("Fehler: Doppelte Buchung wurde zugelassen!");
            } catch (SQLException e) {
                System.out.println("Erfolgreich: Doppelte Buchung wurde verhindert");
            }
            
        } catch (SQLException e) {
            System.err.println("Fehler beim Testen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
