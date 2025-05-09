module com.example.flightprep {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.flightprep to javafx.fxml;
    exports com.example.flightprep;
    exports com.example.flightprep.controller;
    opens com.example.flightprep.controller to javafx.fxml;
    exports com.example.flightprep.model;
    opens com.example.flightprep.model to javafx.fxml;
    exports com.example.flightprep.util;
    opens com.example.flightprep.util to javafx.fxml;
    exports com.example.flightprep.controller.Doctor;
    opens com.example.flightprep.controller.Doctor to javafx.fxml;
    exports com.example.flightprep.controller.Customer;
    opens com.example.flightprep.controller.Customer to javafx.fxml;
}