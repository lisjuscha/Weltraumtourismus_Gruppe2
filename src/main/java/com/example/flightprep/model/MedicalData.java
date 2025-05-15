package com.example.flightprep.model;

/**
 * The `MedicalData` class represents the medical information of a customer in the Flight Preparation application.
 * It includes various health-related attributes such as height, weight, medical conditions, and lifestyle factors.
 */

public class MedicalData {

    private String height;
    private String weight;
    private String alcoholConsumption;
    private String smokingStatus;
    private boolean trainingStatus;
    private boolean disabilityStatus;
    private String disabilityDetails;
    private boolean heartDisease;
    private boolean highBloodPressure;
    private boolean irregularHeartbeat;
    private boolean strokeHistory;
    private boolean asthma;
    private boolean lungDisease;
    private boolean seizureHistory;
    private boolean neurologicalDisorder;
    private boolean hsp_respiratory_cardio;
    private boolean hsp_heart_lung;
    private boolean persc_med;
    private boolean allergies;
    private boolean surgery;
    private boolean ser_injury;

    /**
     * Constructs a `MedicalData` object with the specified details.
     *
     * @param height                 The height of the customer.
     * @param weight                 The weight of the customer.
     * @param alcoholConsumption     The alcohol consumption status of the customer.
     * @param smokingStatus          The smoking status of the customer.
     * @param trainingStatus         Whether the customer is engaged in training activities.
     * @param disabilityStatus       Whether the customer has a disability.
     * @param disabilityDetails      Details about the customer's disability, if any.
     * @param heartDisease           Whether the customer has a history of heart disease.
     * @param highBloodPressure      Whether the customer has high blood pressure.
     * @param irregularHeartbeat     Whether the customer has an irregular heartbeat.
     * @param strokeHistory          Whether the customer has a history of strokes.
     * @param asthma                 Whether the customer has asthma.
     * @param lungDisease            Whether the customer has lung disease.
     * @param seizureHistory         Whether the customer has a history of seizures.
     * @param neurologicalDisorder   Whether the customer has a neurological disorder.
     * @param hsp_respiratory_cardio Whether the customer has high-stress respiratory or cardiovascular conditions.
     * @param hsp_heart_lung         Whether the customer has high-stress heart or lung conditions.
     * @param persc_med              Whether the customer is on prescription medication.
     * @param allergies              Whether the customer has allergies.
     * @param surgery                Whether the customer has undergone surgery.
     * @param ser_injury             Whether the customer has had serious injuries.
     */
    public MedicalData(String height, String weight, String alcoholConsumption, String smokingStatus, boolean trainingStatus,
                       boolean disabilityStatus, String disabilityDetails, boolean heartDisease, boolean highBloodPressure,
                       boolean irregularHeartbeat, boolean strokeHistory, boolean asthma, boolean lungDisease, boolean seizureHistory,
                       boolean neurologicalDisorder, boolean hsp_respiratory_cardio, boolean hsp_heart_lung, boolean persc_med,
                       boolean allergies, boolean surgery, boolean ser_injury) {

        this.height = height;
        this.weight = weight;
        this.alcoholConsumption = alcoholConsumption;
        this.smokingStatus = smokingStatus;
        this.trainingStatus = trainingStatus;
        this.disabilityStatus = disabilityStatus;
        this.disabilityDetails = disabilityDetails;
        this.heartDisease = heartDisease;
        this.highBloodPressure = highBloodPressure;
        this.irregularHeartbeat = irregularHeartbeat;
        this.strokeHistory = strokeHistory;
        this.asthma = asthma;
        this.lungDisease = lungDisease;
        this.seizureHistory = seizureHistory;
        this.neurologicalDisorder = neurologicalDisorder;
        this.hsp_respiratory_cardio = hsp_respiratory_cardio;
        this.hsp_heart_lung = hsp_heart_lung;
        this.persc_med = persc_med;
        this.allergies = allergies;
        this.surgery = surgery;
        this.ser_injury = ser_injury;
    }

    /**
     * Default constructor for the `MedicalData` class.
     */
    public MedicalData() {

    }

    /**
     * Gets the height of the customer.
     *
     * @return The height of the customer.
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the height of the customer.
     *
     * @param height The height to set.
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Gets the weight of the customer.
     *
     * @return The weight of the customer.
     */
    public String getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the customer.
     *
     * @param weight The weight to set.
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     * Gets the alcohol consumption status of the customer.
     *
     * @return The alcohol consumption status of the customer.
     */
    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    /**
     * Sets the alcohol consumption status of the customer.
     *
     * @param alcoholConsumption The alcohol consumption status to set.
     */
    public void setAlcoholConsumption(String alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    /**
     * Gets the smoking status of the customer.
     *
     * @return The smoking status of the customer.
     */
    public String getSmokingStatus() {
        return smokingStatus;
    }

    /**
     * Sets the smoking status of the customer.
     *
     * @param smokingStatus The smoking status to set.
     */
    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    /**
     * Checks if the customer is engaged in training activities.
     *
     * @return `true` if the customer is engaged in training activities, otherwise `false`.
     */
    public boolean isTrainingStatus() {
        return trainingStatus;
    }

    /**
     * Sets the training status of the customer.
     *
     * @param trainingStatus The training status to set.
     */
    public void setTrainingStatus(boolean trainingStatus) {
        this.trainingStatus = trainingStatus;
    }

    /**
     * Checks if the customer has a disability.
     *
     * @return `true` if the customer has a disability, otherwise `false`.
     */
    public boolean getDisabilityStatus() {
        return disabilityStatus;
    }

    /**
     * Sets the disability status of the customer.
     *
     * @param disabilityStatus The disability status to set.
     */
    public void setDisabilityStatus(boolean disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    /**
     * Gets the details about the customer's disability.
     *
     * @return The details about the customer's disability.
     */
    public String getDisabilityDetails() {
        return disabilityDetails;
    }

    /**
     * Sets the details about the customer's disability.
     *
     * @param disabilityDetails The disability details to set.
     */
    public void setDisabilityDetails(String disabilityDetails) {
        this.disabilityDetails = disabilityDetails;
    }

    /**
     * Checks if the customer has a history of heart disease.
     *
     * @return `true` if the customer has a history of heart disease, otherwise `false`.
     */
    public boolean isHeartDisease() {
        return heartDisease;
    }

    /**
     * Sets the heart disease status of the customer.
     *
     * @param heartDisease The heart disease status to set.
     */
    public void setHeartDisease(boolean heartDisease) {
        this.heartDisease = heartDisease;
    }

    /**
     * Checks if the customer has high blood pressure.
     *
     * @return `true` if the customer has high blood pressure, otherwise `false`.
     */
    public boolean isHighBloodPressure() {
        return highBloodPressure;
    }

    /**
     * Sets the high blood pressure status of the customer.
     *
     * @param highBloodPressure The high blood pressure status to set.
     */
    public void setHighBloodPressure(boolean highBloodPressure) {
        this.highBloodPressure = highBloodPressure;
    }

    /**
     * Checks if the customer has an irregular heartbeat.
     *
     * @return `true` if the customer has an irregular heartbeat, otherwise `false`.
     */
    public boolean isIrregularHeartbeat() {
        return irregularHeartbeat;
    }

    /**
     * Sets the irregular heartbeat status of the customer.
     *
     * @param irregularHeartbeat The irregular heartbeat status to set.
     */
    public void setIrregularHeartbeat(boolean irregularHeartbeat) {
        this.irregularHeartbeat = irregularHeartbeat;
    }

    /**
     * Checks if the customer has a history of strokes.
     *
     * @return `true` if the customer has a history of strokes, otherwise `false`.
     */
    public boolean isStrokeHistory() {
        return strokeHistory;
    }

    /**
     * Sets the stroke history status of the customer.
     *
     * @param strokeHistory The stroke history status to set.
     */
    public void setStrokeHistory(boolean strokeHistory) {
        this.strokeHistory = strokeHistory;
    }

    /**
     * Checks if the customer has asthma.
     *
     * @return `true` if the customer has asthma, otherwise `false`.
     */
    public boolean isAsthma() {
        return asthma;
    }

    /**
     * Sets the asthma status of the customer.
     *
     * @param asthma The asthma status to set.
     */
    public void setAsthma(boolean asthma) {
        this.asthma = asthma;
    }

    /**
     * Checks if the customer has lung disease.
     *
     * @return `true` if the customer has lung disease, otherwise `false`.
     */
    public boolean isLungDisease() {
        return lungDisease;
    }

    /**
     * Sets the lung disease status of the customer.
     *
     * @param lungDisease The lung disease status to set.
     */
    public void setLungDisease(boolean lungDisease) {
        this.lungDisease = lungDisease;
    }

    /**
     * Checks if the customer has a history of seizures.
     *
     * @return `true` if the customer has a history of seizures, otherwise `false`.
     */
    public boolean isSeizureHistory() {
        return seizureHistory;
    }

    /**
     * Sets the seizure history status of the customer.
     *
     * @param seizureHistory The seizure history status to set.
     */
    public void setSeizureHistory(boolean seizureHistory) {
        this.seizureHistory = seizureHistory;
    }

    /**
     * Checks if the customer has a neurological disorder.
     *
     * @return `true` if the customer has a neurological disorder, otherwise `false`.
     */
    public boolean isNeurologicalDisorder() {
        return neurologicalDisorder;
    }

    /**
     * Sets the neurological disorder status of the customer.
     *
     * @param neurologicalDisorder The neurological disorder status to set.
     */
    public void setNeurologicalDisorder(boolean neurologicalDisorder) {
        this.neurologicalDisorder = neurologicalDisorder;
    }

    /**
     * Checks if the customer has high-stress respiratory or cardiovascular conditions.
     *
     * @return `true` if the customer has such conditions, otherwise `false`.
     */
    public boolean isHsp_respiratory_cardio() {
        return hsp_respiratory_cardio;
    }

    /**
     * Sets the high-stress respiratory or cardiovascular conditions status of the customer.
     *
     * @param hsp_respiratory_cardio The status to set.
     */
    public void setHsp_respiratory_cardio(boolean hsp_respiratory_cardio) {
        this.hsp_respiratory_cardio = hsp_respiratory_cardio;
    }

    /**
     * Checks if the customer has high-stress heart or lung conditions.
     *
     * @return `true` if the customer has such conditions, otherwise `false`.
     */
    public boolean isHsp_heart_lung() {
        return hsp_heart_lung;
    }

    /**
     * Sets the high-stress heart or lung conditions status of the customer.
     *
     * @param hsp_heart_lung The status to set.
     */
    public void setHsp_heart_lung(boolean hsp_heart_lung) {
        this.hsp_heart_lung = hsp_heart_lung;
    }

    /**
     * Checks if the customer is on prescription medication.
     *
     * @return `true` if the customer is on prescription medication, otherwise `false`.
     */
    public boolean isPersc_med() {
        return persc_med;
    }

    /**
     * Sets the prescription medication status of the customer.
     *
     * @param persc_med The status to set.
     */
    public void setPersc_med(boolean persc_med) {
        this.persc_med = persc_med;
    }

    /**
     * Checks if the customer has allergies.
     *
     * @return `true` if the customer has allergies, otherwise `false`.
     */
    public boolean isAllergies() {
        return allergies;
    }

    /**
     * Sets the allergies status of the customer.
     *
     * @param allergies The allergies status to set.
     */
    public void setAllergies(boolean allergies) {
        this.allergies = allergies;
    }

    /**
     * Checks if the customer has undergone surgery.
     *
     * @return `true` if the customer has undergone surgery, otherwise `false`.
     */
    public boolean isSurgery() {
        return surgery;
    }

    /**
     * Sets the surgery status of the customer.
     *
     * @param surgery The surgery status to set.
     */
    public void setSurgery(boolean surgery) {
        this.surgery = surgery;
    }

    /**
     * Checks if the customer has had serious injuries.
     *
     * @return `true` if the customer has had serious injuries, otherwise `false`.
     */
    public boolean isSer_injury() {
        return ser_injury;
    }

    /**
     * Sets the serious injury status of the customer.
     *
     * @param ser_injury The serious injury status to set.
     */
    public void setSer_injury(boolean ser_injury) {
        this.ser_injury = ser_injury;
    }


}
