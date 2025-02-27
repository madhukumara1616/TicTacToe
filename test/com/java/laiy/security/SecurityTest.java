package com.java.laiy.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.java.laiy.view.ConsoleMenuView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class SecurityTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        resetAttemptCount(); // Reset attempt counter before each test
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        outContent.reset();
        errContent.reset();
    }

    private void resetAttemptCount() {
        try {
            Field attemptCountField = ConsoleMenuView.class.getDeclaredField("attemptCount");
            attemptCountField.setAccessible(true);
            attemptCountField.set(null, 0);
        } catch (Exception e) {
            System.err.println("Could not reset attempt count: " + e.getMessage());
        }
    }

    @Test
    public void testInputSanitization() {
        String input = "Player1<script>alert('xss')</script>\nPlayer2\n3\n4\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        ConsoleMenuView.customInput();
        String output = outContent.toString();
        
        assertFalse("Should remove script tags", output.contains("<script>"));
        assertTrue("Should contain sanitized input", output.contains("Player1"));
    }

    @Test
    public void testResourceLeaks() {
        String input = "4\n"; // Exit option
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        ConsoleMenuView.showMenuWithResult();
        String output = outContent.toString().toLowerCase();
        assertTrue("Should show exit message", output.contains("exit"));
    }

    @Test
    public void testBoundaryValues() {
        for (String size : new String[]{"0", "11", "-1", "999999"}) {
            outContent.reset();
            ByteArrayInputStream testIn = new ByteArrayInputStream((size + "\n").getBytes());
            System.setIn(testIn);
            
            int result = ConsoleMenuView.enterSize();
            assertEquals("Invalid size should return MIN_SIZE (3)", 3, result);
            
            String output = outContent.toString();
            assertTrue("Should show size error message for input: " + size, 
                output.contains("Invalid board size") || 
                output.contains("Board size must be between"));
        }
    }
} 