package com.example.flightprep.util;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * The `RadioButtonReader` class provides utility methods for working with
 * JavaFX `RadioButton` and `ToggleGroup` components. It includes functionality
 * to retrieve boolean values based on the selected toggle's text.
 */
public class RadioButtonReader {

    /**
     * Retrieves a boolean value from the selected `RadioButton` in a `ToggleGroup`.
     * If the selected `RadioButton`'s text is "Yes", it returns `true`. Otherwise, it returns `false`.
     *
     * @param group The `ToggleGroup` containing the `RadioButton` options.
     * @return `true` if the selected `RadioButton`'s text is "Yes", otherwise `false`.
     */
    public static boolean getToggleGroupBoolean(ToggleGroup group) {
        if (((RadioButton) group.getSelectedToggle()).getText().equals("Yes")) {
            return true;
        }
        else{
            return false;
        }
    }
} 