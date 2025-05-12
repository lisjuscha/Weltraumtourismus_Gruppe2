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

public class CustomerService {
    private static CustomerService instance;
    private static final Object LOCK = new Object();
    private static final String UPLOAD_DIR = "Data/uploads";

    private final CustomerDAO customerDAO;
    private final UserDAO userDAO;
    private final MedicalDataDAO medicalDataDAO;
    private final FileUploadService fileUploadService;

    private CustomerService() {
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();
        this.medicalDataDAO = new MedicalDataDAO();
        this.fileUploadService = new FileUploadService();
    }

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

    public List<Customer> getCustomerWithUploadedFiles() throws SQLException {
        synchronized (LOCK) {
            return customerDAO.findAllWithUploadedFiles();
        }
    }

    public Customer getCustomerStatus(String userId) throws SQLException {
        synchronized (LOCK) {
            Customer customer = userDAO.getCustomerByUserId(userId, null);
            if (customer == null) {
                throw new SQLException("Customer not found");
            }
            return customer;
        }
    }

    public void submitMedicalData(MedicalData data) throws SQLException {
        synchronized (LOCK) {
            String userId = SessionManager.getCurrentUserId();
            medicalDataDAO.save(data);

            int riskGroup = RiskClassifierAI.classifyRisk(data);
            customerDAO.updateCustomerRiskGroup(userId, riskGroup);
            customerDAO.updateFormSubmittedStatus(userId, true);
        }
    }

    public MedicalData getMedicalData(String patientId) throws SQLException {
        synchronized (LOCK) {
            return medicalDataDAO.getDataByUserId(patientId);
        }
    }

    public void saveDeclaration(String userId, boolean isApproved, String comment) throws SQLException {
        synchronized (LOCK) {
            customerDAO.saveDeclaration(userId, isApproved, comment);
        }
    }

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
            throw new RuntimeException("Fehler beim Zugriff auf Dokumente", e);
        }
    }

    public void updateFileUploadStatus(String userId) throws SQLException {
        synchronized (LOCK) {
            customerDAO.updateFileUploadStatus(userId);
        }
    }

    public LocalDate getFlightDate(String userId) throws SQLException {
        synchronized (LOCK) {
            return customerDAO.getFlightDate(userId);
        }
    }
}