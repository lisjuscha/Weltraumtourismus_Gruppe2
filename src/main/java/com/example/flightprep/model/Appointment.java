package com.example.flightprep.model;

public class Appointment {
    private int appointmentId;
    private String customerId;
    private String doctorId;
    private String customerFirstName;
    private String customerLastName;
    private String date;
    private String time;
    private int riskGroup;

    public Appointment(int appointmentId, String customerId,String customerFirstName, String customerLastName, String doctorId, String date, String time, int riskGroup) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.riskGroup = riskGroup;
    }

    // Getter
    public int getAppointmentId() {
        return appointmentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    // Setter
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRiskGroup() {
        return riskGroup;
    }
    public void setRiskGroup(int riskGroup) {
        this.riskGroup = riskGroup;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", customerId='" + customerId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}