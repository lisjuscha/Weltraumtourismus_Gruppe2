package com.example.flightprep.model;

/**
 * The `Appointment` class represents an appointment in the Flight Preparation application.
 * It contains details about the appointment, such as the customer, doctor, date, time, and risk group.
 */

public class Appointment {
    private int appointmentId;
    private String customerId;
    private String doctorId;
    private String customerFirstName;
    private String customerLastName;
    private String date;
    private String time;
    private int riskGroup;

    /**
     * Constructs an `Appointment` object with the specified details.
     *
     * @param appointmentId      The unique ID of the appointment.
     * @param customerId         The ID of the customer associated with the appointment.
     * @param customerFirstName  The first name of the customer.
     * @param customerLastName   The last name of the customer.
     * @param doctorId           The ID of the doctor associated with the appointment.
     * @param date               The date of the appointment.
     * @param time               The time of the appointment.
     * @param riskGroup          The risk group of the customer.
     */
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

    /**
     * Gets the unique ID of the appointment.
     *
     * @return The appointment ID.
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * Gets the ID of the customer associated with the appointment.
     *
     * @return The customer ID.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Gets the ID of the doctor associated with the appointment.
     *
     * @return The doctor ID.
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Gets the date of the appointment.
     *
     * @return The appointment date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the time of the appointment.
     *
     * @return The appointment time.
     */
    public String getTime() {
        return time;
    }

    /**
     * Gets the first name of the customer.
     *
     * @return The customer's first name.
     */
    public String getCustomerFirstName() {
        return customerFirstName;
    }

    /**
     * Gets the last name of the customer.
     *
     * @return The customer's last name.
     */
    public String getCustomerLastName() {
        return customerLastName;
    }

    /**
     * Gets the risk group of the customer.
     *
     * @return The customer's risk group.
     */
    public int getRiskGroup() {
        return riskGroup;
    }

    /**
     * Sets the unique ID of the appointment.
     *
     * @param appointmentId The appointment ID to set.
     */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * Sets the ID of the customer associated with the appointment.
     *
     * @param customerId The customer ID to set.
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Sets the ID of the doctor associated with the appointment.
     *
     * @param doctorId The doctor ID to set.
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Sets the date of the appointment.
     *
     * @param date The appointment date to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the time of the appointment.
     *
     * @param time The appointment time to set.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Sets the risk group of the customer.
     *
     * @param riskGroup The risk group to set.
     */
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