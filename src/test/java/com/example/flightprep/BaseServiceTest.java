package com.example.flightprep;

import com.example.flightprep.model.User;
import com.example.flightprep.util.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

public abstract class BaseServiceTest {
    @BeforeEach
    public void setupBaseTest() {
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUser123");
        SessionManager.setCurrentUser(mockUser);
    }

    @AfterEach
    public void cleanupBaseTest() {
        SessionManager.setCurrentUser(null);
    }
}