package com.example.flightprep;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.service.UserService;
import com.example.flightprep.users.Customer;
import com.example.flightprep.users.Doctor;
import com.example.flightprep.users.User;
import com.example.flightprep.util.DbConnection;

import java.sql.*;

public class tst {
    public static void main(String[] args) {
        UserService us = new UserService();
        User u = us.authenticateUser("FreddieG6", "000");
        System.out.println(u.user_id);
    }
}