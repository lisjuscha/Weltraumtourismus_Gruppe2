package com.example.flightprep.controller.Doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // for consistency with other test classes
class DocHomeControllerTest {


    @InjectMocks // Erstellt eine Instanz von DocHomeController
    private DocHomeController docHomeController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testControllerCanBeInstantiated() {
        assertNotNull(docHomeController, "DocHomeController sollte instanziiert werden können.");
    }
}