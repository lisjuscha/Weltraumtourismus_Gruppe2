package com.example.flightprep.util;

import com.example.flightprep.model.MedicalData;

/**
 * The `RiskClassifierAI` class provides functionality for classifying the risk group
 * of a customer based on their medical data. It uses the Body Mass Index (BMI) as
 * the primary metric for determining the risk group.
 */
public class RiskClassifierAI {

    /**
     * Classifies the risk group of a customer based on their medical data.
     * The classification is determined using the BMI:
     * - Risk group 1: BMI between 20 and 25 (inclusive).
     * - Risk group 2: BMI between 18.5 and 30 (inclusive, but outside group 1 range).
     * - Risk group 3: BMI outside the above ranges.
     *
     * @param data The `MedicalData` object containing the customer's height and weight.
     * @return An integer representing the risk group (1, 2, or 3).
     */
    public static int classifyRisk(MedicalData data) {

        double height = parseDouble(data.getHeight()) / 100; // cm zu m
        double weight = parseDouble(data.getWeight());
        double bmi = weight / (height * height);

        // Return risk group
        if (bmi > 20 && bmi < 25) return 1;
        else if (bmi > 18.5 && bmi < 30) return 2;
        else return 3;
    }
    /**
     * Parses a string value into a double. If the value is null or empty,
     * it returns 0.0. The method also handles values with commas as decimal separators.
     *
     * @param value The string value to parse.
     * @return The parsed double value.
     */
    private static double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        // Replace comma with dot for locales that use comma as decimal separator.
        return Double.parseDouble(value.replace(",", "."));
    }
}

