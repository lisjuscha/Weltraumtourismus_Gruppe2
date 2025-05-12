package com.example.flightprep.service;

import com.example.flightprep.dao.CustomerDao;
import com.example.flightprep.model.Customer;
import java.util.List;

public class CustomerService {
    private static CustomerService instance;
    private final CustomerDao customerDao;

    private CustomerService() {
        this.customerDao = new CustomerDao();
    }

    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    public List<Customer> getPatientsWithUploadedFiles() {
        return customerDao.findAllWithUploadedFiles();
    }
}
