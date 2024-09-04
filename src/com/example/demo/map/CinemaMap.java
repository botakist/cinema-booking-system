package com.example.demo.map;

import com.example.demo.constants.Constants;
import com.example.demo.workflows.Workflows;

public class CinemaMap {


    public static void displayMapDefault() {
        System.out.println(Constants.PRINT_SCREEN_MESSAGE);
        for (int i = Workflows.getRow(); i > 0; i--) {
            System.out.printf("%s ", Constants.ALPHABETS[i - 1]);
            System.out.println(displayRow());
            System.out.println();
        }
    }

    private static String displayRow() {
        for (int j = 0; j < Workflows.getSeatsPerRow(); j++) {
            // TODO: for each row use StringBuilder generate the string, then use deriveCharToDisplay to check whether the seat already booked or not

        }
        return null;
    }

    private static char deriveCharToDisplay(int seatNumber, String bookingId) {
        String[][] map = Workflows.getCinemaMap();
        int middleMostCol = Workflows.getMiddleMostCol();
        return 0;
    }


}
