package com.example.flightprep.util;

import com.example.flightprep.model.MedicalData;

public class RiskClassifierAI {

    public static int classifyRisk(MedicalData data) {

        // Get height and weight from the data
        double height = Double.parseDouble(data.getHeight()) / 100; // cm zu m
        double weight = Double.parseDouble(data.getWeight());
        // BMI calculation
        double bmi = weight / (height * height);

        // Return risk group
        if (bmi > 20 && bmi < 25) return 1;
        else if (bmi > 18.5 && bmi < 30) return 2;
        else return 3;
    }
}

