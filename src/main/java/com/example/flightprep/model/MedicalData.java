package com.example.flightprep.model;

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

    // Constructor
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

    public MedicalData() {

    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public String getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public boolean isTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(boolean trainingStatus) {
        this.trainingStatus = trainingStatus;
    }

    public boolean getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(boolean disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public String getDisabilityDetails() {
        return disabilityDetails;
    }

    public void setDisabilityDetails(String disabilityDetails) {
        this.disabilityDetails = disabilityDetails;
    }

    public boolean isHeartDisease() {
        return heartDisease;
    }

    public void setHeartDisease(boolean heartDisease) {
        this.heartDisease = heartDisease;
    }

    public boolean isHighBloodPressure() {
        return highBloodPressure;
    }

    public void setHighBloodPressure(boolean highBloodPressure) {
        this.highBloodPressure = highBloodPressure;
    }

    public boolean isIrregularHeartbeat() {
        return irregularHeartbeat;
    }

    public void setIrregularHeartbeat(boolean irregularHeartbeat) {
        this.irregularHeartbeat = irregularHeartbeat;
    }

    public boolean isStrokeHistory() {
        return strokeHistory;
    }

    public void setStrokeHistory(boolean strokeHistory) {
        this.strokeHistory = strokeHistory;
    }

    public boolean isAsthma() {
        return asthma;
    }

    public void setAsthma(boolean asthma) {
        this.asthma = asthma;
    }

    public boolean isLungDisease() {
        return lungDisease;
    }

    public void setLungDisease(boolean lungDisease) {
        this.lungDisease = lungDisease;
    }

    public boolean isSeizureHistory() {
        return seizureHistory;
    }

    public void setSeizureHistory(boolean seizureHistory) {
        this.seizureHistory = seizureHistory;
    }

    public boolean isNeurologicalDisorder() {
        return neurologicalDisorder;
    }

    public void setNeurologicalDisorder(boolean neurologicalDisorder) {
        this.neurologicalDisorder = neurologicalDisorder;
    }

    public boolean isHsp_respiratory_cardio() {
        return hsp_respiratory_cardio;
    }

    public void setHsp_respiratory_cardio(boolean hsp_respiratory_cardio) {
        this.hsp_respiratory_cardio = hsp_respiratory_cardio;
    }

    public boolean isHsp_heart_lung() {
        return hsp_heart_lung;
    }

    public void setHsp_heart_lung(boolean hsp_heart_lung) {
        this.hsp_heart_lung = hsp_heart_lung;
    }

    public boolean isPersc_med() {
        return persc_med;
    }

    public void setPersc_med(boolean persc_med) {
        this.persc_med = persc_med;
    }

    public boolean isAllergies() {
        return allergies;
    }

    public void setAllergies(boolean allergies) {
        this.allergies = allergies;
    }

    public boolean isSurgery() {
        return surgery;
    }

    public void setSurgery(boolean surgery) {
        this.surgery = surgery;
    }

    public boolean isSer_injury() {
        return ser_injury;
    }

    public void setSer_injury(boolean ser_injury) {
        this.ser_injury = ser_injury;
    }


}
