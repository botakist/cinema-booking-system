package com.example.demo.workflows;

import com.example.demo.constants.Constants;
import com.example.demo.exception.NotEnoughSeatsAvailableException;
import com.example.demo.map.CinemaMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Workflows {
    public static Map<String, Object> settings = new HashMap<>();
    public static Scanner scanner = new Scanner(System.in);

    public static String getMovieTitle() {
        return settings.get(Constants.TITLE).toString();
    }

    public static int getTotalSeatsAvailable() {
        return (int) settings.get(Constants.TOTAL_SEATS_AVAILABLE);
    }

    public static int decreaseTotalSeatsAvailable(int amountToDecrease) {
        int amountLeft = getTotalSeatsAvailable() - amountToDecrease;
        if (amountLeft < 0) {
            throw new NotEnoughSeatsAvailableException(String.format("Sorry, there are only %d seats available.", getTotalSeatsAvailable()));
        }
        return amountLeft;
    }

    public static void setTotalSeatsAvailable(int amountToSet) {
        settings.put(Constants.TOTAL_SEATS_AVAILABLE, amountToSet);
    }

    public static int getId() {
        return (int) settings.get(Constants.ID);
    }

    public static void increaseId() {
        settings.put(Constants.ID, getId() + 1);
    }

    public static int getRow() {
        return (int) settings.get(Constants.ROW);
    }

    public static int getSeatsPerRow() {
        return (int) settings.get(Constants.SEATS_PER_ROW);
    }

    public static void setCurrentBookingId(String currentBookingId) {
        settings.put(Constants.CURRENT_BOOKING_ID, currentBookingId);
    }

    public static String getCurrentBookingId() {
        return (String) settings.get(Constants.CURRENT_BOOKING_ID);
    }

    public static void setMaximumRowAllowed() {
        char maxRowChar = Constants.ALPHABETS[getRow() - 1].charAt(0);
        settings.put(Constants.MAXIMUM_ROW_ALLOWED, maxRowChar);
    }

    public static char getMaximumRowAllowed() {
        return (char) settings.get(Constants.MAXIMUM_ROW_ALLOWED);
    }

    public static String[][] getCinemaMap() {
        return (String[][]) settings.get(Constants.CINEMA_MAP);
    }

    public static void setCinemaMap(String[][] cinemaMap) {
        settings.put(Constants.CINEMA_MAP, cinemaMap);
    }

    public static int getMiddleMostCol() {
        return (int) settings.get(Constants.MIDDLEMOST_COL);
    }

    public static void setMiddleMostCol(int col) {
        settings.put(Constants.MIDDLEMOST_COL, col);
    }

    public static void initialiseSettings() {
        while (true) {
            System.out.println(Constants.DEFINE_TITLE_AND_SEATING_MAP);
            String userInput = scanner.nextLine();
            // clean up input
            if (isBlank(userInput)) {
                System.out.println("Invalid input. Input is not in [Title] [Row] [SeatsPerRow] format.");
                continue;
            }
            userInput = userInput.trim();
            String[] parts = userInput.split("\\s+");
            // Validate input length
            if (parts.length < 3) {
                System.out.println("Input must contain a movie title followed by at least two numbers.");
                continue;
            }

            int row = 0;
            int seatsPerRow = 0;
            try {
                seatsPerRow = Integer.parseInt(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid SeatsPerRow input. [SeatsPerRow] must be in number format.");
            }
            try {
                row = Integer.parseInt(parts[parts.length - 2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Row input. [Row] must be in number format.");
                continue;
            }

            if (row == 0 || row > 26) {
                System.out.println("Invalid Row input. [Row] must be between 1 and 26.");
                continue;
            }

            if (seatsPerRow == 0 || seatsPerRow > 50) {
                System.out.println("Invalid SeatsPerRow input. [SeatsPerRow] must be between 1 and 50");
                continue;
            }

            StringBuilder titleBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 2; i++) {
                if (i > 0) {
                    titleBuilder.append(" ");
                }
                titleBuilder.append(parts[i]);
            }
            settings.put(Constants.TITLE, titleBuilder);
            settings.put(Constants.ROW, row);
            settings.put(Constants.SEATS_PER_ROW, seatsPerRow);
            settings.put(Constants.TOTAL_SEATS_AVAILABLE, row * seatsPerRow);
            settings.put(Constants.CINEMA_MAP, new String[row][seatsPerRow]);
            settings.put(Constants.ID, 1);
            setMiddleMostCol(seatsPerRow / 2);
            setMaximumRowAllowed();
            break;
        }
    }


    public static String displayMainMenu() {
        System.out.println(Constants.WELCOME);
        System.out.printf((Constants.OPTION_ONE) + "%n", getMovieTitle(), getTotalSeatsAvailable());
        System.out.println(Constants.OPTION_TWO);
        System.out.println(Constants.OPTION_THREE);
        System.out.println(Constants.PLS_ENTER_SELECTION);
        System.out.print(Constants.POINT_RIGHT_WITH_SPACE);
        return scanner.nextLine();
    }

    public static int isValidIntegerInput(String userInput, int max) {
        if (isBlank(userInput)) {
            System.out.println("Invalid selection. Please select a valid option.");
            return 0;
        }
        int input = 0;
        try {
            input = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid option selected. Please select a valid option.");
            return 0;
        }
        if (input < 1) {
            System.out.printf("%d is not a valid option. Please enter positive numbers only.%n", input);
            return 0;
        }
        if (max == 0) {
            // use a different validation message
            return input;
        }
        if (input > max) {
            System.out.printf("%d is not a valid option. Please select options between 1 and %d only.%n", input, max);
            return 0;
        }
        return input;
    }


    public static void ticketBookingFlow() {
        generateBookingId();
        while (true) {
            System.out.println("Enter number of tickets to book, or enter blank to go back to main menu:");
            String input = scanner.nextLine();
            if (isBlank(input)) {
                break;
            }
            int tickets = isValidIntegerInput(input, 0);
            if (tickets == 0) {
                continue;
            }
            int seatsLeft = 0;
            try {
                seatsLeft = decreaseTotalSeatsAvailable(tickets);
            } catch (NotEnoughSeatsAvailableException e) {
                System.out.println(e.getMessage());
                continue;
            }

            System.out.printf("Successfully reserved %d %s tickets.%n", tickets, getMovieTitle());
            System.out.printf("Booking id: %s%n", getCurrentBookingId());
            System.out.println("Selected seats:");
            CinemaMap.displayMapDefault();
            System.out.println("Enter blank to accept seat selection, or enter a new seating position:");
            System.out.print(Constants.POINT_RIGHT_WITH_SPACE);
            String seatSelection = scanner.nextLine();
            if (isBlank(seatSelection)) {
                confirmation(seatsLeft);
                break;
            }
            if (!isValidSeatSelection(seatSelection)) {
                continue;
            }


            System.out.println("Enter blank to accept seat selection, or enter a new seating position:");
            System.out.print(Constants.POINT_RIGHT_WITH_SPACE);
            String newSeatingPosition = scanner.nextLine();
            break;
        }
    }

    public static boolean isValidSeatSelection(String seatSelection) {
        char row = seatSelection.charAt(0);
        if (row < 'A') {
            // input row cannot be "smaller" than the first row ('A')
            System.out.printf("Invalid seat selection %s row %c. Please enter a valid row.", seatSelection, row);
            return false;
        }
        // input row cannot be larger than the initial row settings
        if (row > getMaximumRowAllowed()) {
            System.out.printf("Invalid seat selection %s row %c. Row input cannot exceed %c.%n", seatSelection, row, getMaximumRowAllowed());
            return false;
        }
        // Trim any leading '0' characters
        String trimmedStr = seatSelection.substring(1).replaceFirst("^0+", "");
        int seatNumberInt = Integer.parseInt(trimmedStr);
        if (seatNumberInt > getSeatsPerRow()) {
            System.out.printf("Invalid seat selection %s seat number %d. Seat number cannot be more than the maximum allowed %d.%n", seatSelection, seatNumberInt, getSeatsPerRow());
            return false;
        }
        return true;
    }

    public static String generateBookingId() {
        int length = 4;
        char padChar = '0';

        StringBuilder sb = new StringBuilder();
        sb.append(Constants.GIC);
        int paddingLength = length - String.valueOf(getId()).length();

        for (int i = 0; i < paddingLength; i++) {
            sb.append(padChar);
        }
        sb.append(getId());
        setCurrentBookingId(sb.toString());
        return sb.toString();
    }

    public static boolean isBlank(String input) {
        return input == null || input.isEmpty() || Pattern.matches("^\\s*$", input);
    }

    public static void confirmation(int amountLeft) {
        System.out.printf("Booking id: %s confirmed.%n", getCurrentBookingId());
        // only set available seats left after booking confirmed.
        setTotalSeatsAvailable(amountLeft);
        // increment id only after booking confirmed
        increaseId();
    }
}

