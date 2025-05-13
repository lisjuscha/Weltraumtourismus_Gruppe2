package com.example.flightprep.util;

import com.example.flightprep.model.MedicalData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RiskClassifierAITest {

    @Test
    void testClassifyRisk_Group1() {
        // Test for normal BMI (risk group 1: BMI > 20 and < 25)
        MedicalData data = new MedicalData();
        data.setHeight("170");
        data.setWeight("60");

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(1, risk);
    }

    @Test
    void testClassifyRisk_Group2_Underweight() {
        // Test for slightly underweight (risk group 2: BMI > 18.5 and < 30)
        MedicalData data = new MedicalData();
        data.setHeight("170");
        data.setWeight("54");

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(2, risk);
    }

    @Test
    void testClassifyRisk_Group2_Overweight() {
        // Test for slightly overweight (risk group 2: BMI > 18.5 and < 30)
        MedicalData data = new MedicalData();
        data.setHeight("170");
        data.setWeight("80");

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(2, risk);
    }

    @Test
    void testClassifyRisk_Group3_SeverelyUnderweight() {
        // Test for severely underweight (risk group 3: BMI <= 18.5)
        MedicalData data = new MedicalData();
        data.setHeight("170");
        data.setWeight("50");

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(3, risk);
    }

    @Test
    void testClassifyRisk_Group3_SeverelyOverweight() {
        // Test for severely overweight (risk group 3: BMI >= 30)
        MedicalData data = new MedicalData();
        data.setHeight("170");
        data.setWeight("90");

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(3, risk);
    }

    @Test
    void testClassifyRisk_EmptyData() {
        // Test with empty height and weight
        MedicalData data = new MedicalData();
        data.setHeight("");
        data.setWeight("");

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(3, risk);
    }

    @Test
    void testClassifyRisk_NullData() {
        // Test with null height and weight
        MedicalData data = new MedicalData();
        data.setHeight(null);
        data.setWeight(null);

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(3, risk);
    }

    @Test
    void testClassifyRisk_CommaDecimalSeparator() {
        // Test with comma as decimal separator
        MedicalData data = new MedicalData();
        data.setHeight("170,5");
        data.setWeight("65,5");

        int risk = RiskClassifierAI.classifyRisk(data);

        assertEquals(1, risk);
    }
}