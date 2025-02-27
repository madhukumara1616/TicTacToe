package com.java.laiy.view;

import com.java.laiy.controller.Game;
import com.java.laiy.controller.GameStarter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.NoSuchElementException;

public class ConsoleMenuView {
    private static final int START_CODE = 1;
    private static final int LOAD_CODE = 2;
    private static final int SETTINGS_CODE = 3;
    private static final int EXIT_CODE = 4;
    private static final int MIN_SIZE = 3;
    private static final int MAX_SIZE = 10;
    private static final int MAX_INPUT_LENGTH = 50;
    private static final int DEFAULT_BOARD_SIZE = MIN_SIZE;
    private static final Logger LOGGER = Logger.getLogger(ConsoleMenuView.class.getName());
    private static int attemptCount = 0;
    private static final int MAX_ATTEMPTS = 3;

    private static Scanner getScanner() {
        return new Scanner(System.in);
    }

    private static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Limit input length
        if (input.length() > MAX_INPUT_LENGTH) {
            input = input.substring(0, MAX_INPUT_LENGTH);
        }
        // Remove any non-alphanumeric characters except spaces
        return input.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }

    private static void logSecurityEvent(String message) {
        LOGGER.log(Level.WARNING, "Security Event: {0}", message);
    }

    private static void checkRateLimit() {
        if (++attemptCount > MAX_ATTEMPTS) {
            logSecurityEvent("Rate limit exceeded");
            throw new SecurityException("Too many attempts");
        }
    }

    private static boolean isValidPlayerName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= MAX_INPUT_LENGTH;
    }

    private static final String INVALID_INPUT_MSG = "Invalid input. Please try again.";
    private static final String INVALID_SIZE_MSG = "Invalid board size. Using default size %d.";

    public static void showMenuWithResult() {
        Scanner scanner = getScanner();
        attemptCount = 0; // Reset attempt count at the start
        while (attemptCount < MAX_ATTEMPTS) {
            try {
                displayMenu();
                processMenuChoice(scanner);
                return; // Exit after processing a valid choice
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
                attemptCount++;
            } catch (NoSuchElementException e) {
                System.out.println("No input available. Please try again.");
                attemptCount++;
                break; // Exit the loop if no input is available
            }
        }
        // If we reach here, it means max attempts were exceeded
        System.out.println("Too many invalid attempts. Please try again later.");
        logSecurityEvent("Rate limit exceeded");
    }

    private static void displayMenu() {
        System.out.println("++++  XO Magic  ++++");
        System.out.println(START_CODE + " - Play");
        System.out.println(LOAD_CODE + " - Load");
        System.out.println(SETTINGS_CODE + " - Set up and play");
        System.out.println(EXIT_CODE + " - Exit");
        System.out.print("> ");
    }

    private static void processMenuChoice(Scanner scanner) {
        if (!scanner.hasNextInt()) {
            throw new InputMismatchException();
        }

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case START_CODE:
                System.out.println("A new game started");
                GameStarter.defaultStart().theGame();
                break;
            case LOAD_CODE:
                System.out.println("Loading...");
                break;
            case SETTINGS_CODE:
                Game game = customInput();
                if (game != null) {
                    game.theGame();
                }
                break;
            case EXIT_CODE:
                System.out.println("Exit");
                break;
            default:
                System.out.println("Choice is incorrect, please try again");
                attemptCount++; // Increment attempt count for invalid choice
                break;
        }
    }

    public static Game customInput() {
        Scanner scanner = getScanner();
        final String gameName = "XO";

        System.out.println("Enter player one name:");
        String playerOneName = sanitizeInput(scanner.nextLine());
        if (!isValidPlayerName(playerOneName)) {
            LOGGER.warning("Invalid player one name");
            playerOneName = "Player 1";
        }

        System.out.println("Enter player two name:");
        String playerTwoName = sanitizeInput(scanner.nextLine());
        if (!isValidPlayerName(playerTwoName)) {
            LOGGER.warning("Invalid player two name");
            playerTwoName = "Player 2";
        }

        int boardSize = enterSize();
        if (boardSize == -1) {
            LOGGER.warning("Invalid board size provided");
            return null;
        }

        return GameStarter.customStart(boardSize, playerOneName, playerTwoName, gameName);
    }

    public static int enterSize() {
        Scanner scanner = getScanner();
        System.out.println("Enter board size (" + MIN_SIZE + "-" + MAX_SIZE + "):");

        try {
            if (!scanner.hasNextInt()) {
                LOGGER.warning("Invalid board size input");
                System.out.printf("Invalid board size. Using default size %d.%n", DEFAULT_BOARD_SIZE);
                scanner.nextLine();
                return DEFAULT_BOARD_SIZE;
            }

            int size = scanner.nextInt();
            scanner.nextLine();

            if (size < MIN_SIZE || size > MAX_SIZE) {
                LOGGER.warning("Board size out of range: " + size);
                System.out.println("Board size must be between " + MIN_SIZE + " and " + MAX_SIZE + ".");
                return DEFAULT_BOARD_SIZE;
            }

            return size;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading board size", e);
            return DEFAULT_BOARD_SIZE;
        }
    }
}
