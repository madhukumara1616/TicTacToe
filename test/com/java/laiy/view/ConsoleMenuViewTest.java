package com.java.laiy.view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class ConsoleMenuViewTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        resetAttemptCount();
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        outContent.reset();
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
    public void testInvalidMenuInput() {
        String input = "abc\n4\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        ConsoleMenuView.showMenuWithResult();
        String output = outContent.toString().toLowerCase();
        
        assertTrue("Should contain invalid input message", 
            output.contains("invalid input") || 
            output.contains("incorrect") ||
            output.contains("please enter a number"));
    }

    @Test
    public void testValidBoardSize() {
        String input = "3\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        int size = ConsoleMenuView.enterSize();
        assertEquals("Board size should be 3", 3, size);
    }

    @Test
    public void testInvalidBoardSize() {
        String input = "11\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        int size = ConsoleMenuView.enterSize();
        assertEquals("Invalid size should return MIN_SIZE (3)", 3, size);
        assertTrue(outContent.toString().contains("Board size must be between"));
    }

    @Test
    public void testCustomInputWithInvalidNames() {
        // Test with empty names and a valid board size
        String input = String.join("\n", 
            " ",    // Empty name for player 1
            " ",    // Empty name for player 2
            "3"     // Valid board size
        ) + "\n";
        
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        ConsoleMenuView.customInput();
        String output = outContent.toString();
        
        // Check if default names are being used
        assertTrue("Should use default name for player 1", 
            output.contains("Player 1"));
        assertTrue("Should use default name for player 2", 
            output.contains("Player 2"));
    }

    @Test
    public void testMaxAttemptsExceeded() {
        // Simulate multiple invalid inputs to trigger the maximum attempts
        String input = "invalid\ninvalid\ninvalid\ninvalid\n"; // Four invalid inputs
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        ConsoleMenuView.showMenuWithResult();
        String output = outContent.toString().toLowerCase();
        
        // Check for the message indicating too many attempts
        assertTrue("Should indicate too many attempts", 
            output.contains("too many invalid attempts") || 
            output.contains("please try again later"));
    }

    @Test
    public void testValidGameFlow() {
        // Simulate valid input for starting a game
        String input = "1\n"; // Start game
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        
        // Call the method that starts the game
        ConsoleMenuView.showMenuWithResult();
        
        // Capture the output
        String output = outContent.toString().toLowerCase();
        
        // Check if the output contains the expected message
        assertTrue("Should start new game", 
            output.contains("a new game started"));
    }
} 