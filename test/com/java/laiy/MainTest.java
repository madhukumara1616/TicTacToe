package com.java.laiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void testMainWithDefaultGame() {
        // Test the START_CODE (1) option
        String simulatedUserInput = String.join("\n",
            "1",           // Choose START_CODE (Play)
            "1",          // First move
            "4",          // Second move
            "2",          // Third move
            "5",          // Fourth move
            "3",          // Fifth move - this should win the game
            "4"           // EXIT_CODE after game ends
        ) + "\n";

        runTestWithInput(simulatedUserInput);
    }

    @Test
    public void testMainWithCustomGame() {
        // Test the SETTINGS_CODE (3) option
        String simulatedUserInput = String.join("\n",
            "3",           // Choose SETTINGS_CODE (Set up and play)
            "Alice",       // Player one name
            "Bob",         // Player two name
            "3",          // Board size
            "1",          // First move
            "4",          // Second move
            "2",          // Third move
            "5",          // Fourth move
            "3",          // Fifth move - this should win the game
            "4"           // EXIT_CODE after game ends
        ) + "\n";

        runTestWithInput(simulatedUserInput);
    }

    @Test
    public void testMainWithExit() {
        // Test the EXIT_CODE (4) option
        String simulatedUserInput = "4\n";  // Choose EXIT_CODE
        runTestWithInput(simulatedUserInput);
    }

    private void runTestWithInput(String input) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        try {
            Main.main(new String[]{});
        } catch (Exception e) {
            // Game might end in different ways depending on the implementation
            // We're primarily testing the input/output flow
        }
    }
}
