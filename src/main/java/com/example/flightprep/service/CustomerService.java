package com.example.flightprep.service;

import com.example.flightprep.dao.CustomerDAO;
import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.Customer;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.RiskClassifierAI;
import com.example.flightprep.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The `CustomerService` class provides business logic for managing customer-related operations
 * in the Flight Preparation application. It handles tasks such as retrieving customer data,
 * submitting medical data, managing uploaded files, and updating customer statuses.
 * This class follows the Singleton design pattern to ensure a single instance.
 */
public class CustomerService {
    private static CustomerService instance;
    private static final Object LOCK = new Object();
    private static final String UPLOAD_DIR = "Data/uploads";

    private final CustomerDAO customerDAO;
    private final UserDAO userDAO;
    private final MedicalDataDAO medicalDataDAO;
    private final FileUploadService fileUploadService;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes DAO and service instances.
     */
    private CustomerService() {
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();
        this.medicalDataDAO = new MedicalDataDAO();
        this.fileUploadService = new FileUploadService();
    }

    /**
     * Retrieves the single instance of `CustomerService`.
     *
     * @return The `CustomerService` instance.
     */
    public static CustomerService getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new CustomerService();
                }
            }
        }
        return instance;
    }

    /**
     * Retrieves a list of customers who have uploaded files, but have not been declared yet
     *
     * @return A list of `Customer` objects with uploaded files.
     * @throws SQLException If a database access error occurs.
     */
    public List<Customer> getCustomerWithUploadedFiles() throws SQLException {
        synchronized (LOCK) {
            return customerDAO.findAllWithUploadedFiles();
        }
    }

    /**
     * Retrieves the status of a customer by their user ID.
     *
     * @param userId The user ID of the customer.
     * @return The `Customer` object representing the customer's status.
     * @throws SQLException If the customer is not found or a database error occurs.
     */
    public Customer getCustomerStatus(String userId) throws SQLException {
        synchronized (LOCK) {
            Customer customer = userDAO.getCustomerByUserId(userId, null);
            if (customer == null) {
                throw new SQLException("Customer not found");
            }
            return customer;
        }
    }

    /**
     * Submits medical data for the current user and updates their risk group and form submission status.
     *
     * @param data The `MedicalData` object containing the medical information.
     * @throws SQLException If a database access error occurs.
     */
    public void submitMedicalData(MedicalData data) throws SQLException {
        synchronized (LOCK) {
            String userId = SessionManager.getCurrentUserId();
            medicalDataDAO.save(data);

            int riskGroup = RiskClassifierAI.classifyRisk(data);
            customerDAO.updateCustomerRiskGroup(userId, riskGroup);
            customerDAO.updateFormSubmittedStatus(userId, true);
        }
    }

    /**
     * Retrieves the medical data of a specific patient by their user ID.
     *
     * @param patientId The user ID of the patient.
     * @return The `MedicalData` object containing the patient's medical information.
     * @throws SQLException If a database access error occurs.
     */
    public MedicalData getMedicalData(String patientId) throws SQLException {
        synchronized (LOCK) {
            return medicalDataDAO.getDataByUserId(patientId);
        }
    }

    /**
     * Saves a declaration for a customer, including approval status and comments.
     *
     * @param userId     The user ID of the customer.
     * @param isApproved Whether the declaration is approved.
     * @param comment    Additional comments for the declaration.
     * @throws SQLException If a database access error occurs.
     */
    public void saveDeclaration(String userId, boolean isApproved, String comment) throws SQLException {
        synchronized (LOCK) {
            customerDAO.saveDeclaration(userId, isApproved, comment);
        }
    }

    /**
     * Retrieves a list of patient document file names from the upload directory.
     *
     * @return A sorted list of file names.
     */
    public List<String> getPatientDocuments() {
        try {
            fileUploadService.createDirectories();
            File uploadsDir = new File(UPLOAD_DIR);
            File[] files = uploadsDir.listFiles(file -> !file.isDirectory());

            if (files == null) {
                return new ArrayList<>();
            }

            List<String> fileNames = new ArrayList<>();
            for (File file : files) {
                fileNames.add(file.getName());
            }
            Collections.sort(fileNames);
            return fileNames;
        } catch (IOException e) {
            throw new RuntimeException("Error accessing documents", e);
        }
    }

    /**
     * Updates the file upload status for a specific customer.
     *
     * @param userId The user ID of the customer.
     * @throws SQLException If a database access error occurs.
     */
    public void updateFileUploadStatus(String userId) throws SQLException {
        synchronized (LOCK) {
            customerDAO.updateFileUploadStatus(userId);
        }
    }

    /**
     * Retrieves the flight date for a specific customer by their user ID.
     *
     * @param userId The user ID of the customer.
     * @return The flight date as a `LocalDate` object.
     * @throws SQLException If a database access error occurs.
     */
    public LocalDate getFlightDate(String userId) throws SQLException {
        synchronized (LOCK) {
            return customerDAO.getFlightDate(userId);
        }
    }
}