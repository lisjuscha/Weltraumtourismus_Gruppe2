package com.example.flightprep.util;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class RadioButoonReader {

    public static boolean getToggleGroupBoolean(ToggleGroup group) {
        if (((RadioButton) group.getSelectedToggle()).getText() == "Yes") {
            return true;
        }
        else{
            return false;
        }
    }
}
