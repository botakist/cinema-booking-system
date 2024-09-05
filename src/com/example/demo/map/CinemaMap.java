package com.example.demo.map;

import com.example.demo.constants.Constants;
import com.example.demo.validators.Validator;
import com.example.demo.workflows.Workflows;

import java.util.ArrayList;

import static com.example.demo.constants.Constants.ALPHABETS;

public class CinemaMap {

    public static String[] getCurrentRow(int i) {
        return (String[]) Workflows.settings.get(ALPHABETS[i - 1]);
    }

    public static int getCurrentRowIndex(String seatPosition) {
        if (Validator.isBlank(seatPosition)) {
            return 1;
        }
        String letter = String.valueOf(seatPosition.charAt(0));
        for (int i = 0; i < ALPHABETS.length; i++) {
            if (ALPHABETS[i].equals(letter)) {
                return i + 1;
            }
        }
        return -1;
    }

    public static void generateEmptyCinemaMap() {
        for (int i = Workflows.getRows(); i > 0; i--) {
            Workflows.settings.put(ALPHABETS[i - 1], new String[Workflows.getSeatsPerRow()]);
        }
    }

    public static void display(String bookingId) {
        System.out.println(Constants.PRINT_SCREEN_MESSAGE);
        for (int i = Workflows.getRows(); i > 0; i--) {
            StringBuilder sb = new StringBuilder();
            String rowInAlphabet = ALPHABETS[i - 1];
            sb.append(" " + rowInAlphabet + " "); // display the row in descending order
            String[] rowArr = (String[]) Workflows.settings.get(rowInAlphabet);
            for (String row : rowArr) {
                sb.append(" " + deriveCharToDisplay(row, bookingId) + " "); // display the seats for the row in asc order (1, 2, ..., X)
            }
            System.out.println(sb);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (int i = 0; i < Workflows.getSeatsPerRow(); i++) {
            sb.append(" ").append(i + 1).append(" ");
        }
        System.out.println(sb);
    }

    private static char deriveCharToDisplay(String rowStr, String bookingIdToDisplay) {
        if (Validator.isBlank(bookingIdToDisplay)) {
            bookingIdToDisplay = Workflows.getCurrentBookingId();
        }
        if (Validator.isBlank(rowStr)) {
            return '.';
        }
        if (rowStr.contains(bookingIdToDisplay)) {
            return 'o';
        } else {
            return '#';
        }
    }

    public static void setReservationForCurrentUser(int noOfSeatsBooked, String seatPosition) {
        int noOfSeatsLeft = noOfSeatsBooked;
        // derive available position for current user to sit
        int seatsPerRow = Workflows.getSeatsPerRow(); // use for overflow to next row
        int totalRows = Workflows.getRows();
        // check if position already taken.
        // if yes return validation error and ask user re-enter valid position
        for (int alphabetIndex = getCurrentRowIndex(seatPosition); alphabetIndex <= totalRows; alphabetIndex++) {
            ArrayList<Integer> reserved;
            String[] currentRowArr = getCurrentRow(alphabetIndex);
            if (noOfSeatsLeft > seatsPerRow) {
                reserved = deriveAvailableSeatsOnCurrentRow(seatsPerRow, seatPosition, currentRowArr);
            } else {
                reserved = deriveAvailableSeatsOnCurrentRow(noOfSeatsLeft, seatPosition, currentRowArr);
            }
            for (Integer seatNo : reserved) {
                currentRowArr[seatNo - 1] = Workflows.getCurrentBookingId();
                noOfSeatsLeft--;
            }
            if (noOfSeatsLeft <= 0) {
                break;
            }
        }
    }

    public static ArrayList<Integer> deriveAvailableSeatsOnCurrentRow(int seatsToBook, final String seatPosition, String[] currentRow) {
        int middleMostCol = Workflows.getMiddleMostCol();
        // derive row and seat number
        if (Validator.isBlank(seatPosition)) {
            return generateReservedCurrentRow(seatsToBook, middleMostCol, currentRow);
        }
        int startPos = Validator.getSeatPosition(seatPosition);

        int availableSeatsInCurrentRow = 0;
        // check available seats left in current row
        for (String seat : currentRow) {
            // if it is not blank it would be already taken by the previous user
            if (Validator.isBlank(seat)) {
                availableSeatsInCurrentRow++;
            }
        }
        // TODO: stuck here, need to determine when to auto set startPos and when to
        // if rows have no existing booking, generate default, else generate custom.
        if (seatsToBook <= availableSeatsInCurrentRow) {
            return generateReservedCurrentRow(seatsToBook, middleMostCol, currentRow);
        }
        return generateCustomReservedCurrentRow(availableSeatsInCurrentRow, startPos, currentRow);
    }


    public static ArrayList<Integer> generateReservedCurrentRow(int noOfSeatsBookedCurrentRow, final int middleCol, String[] currentRow) {
        // generate default array of reserved seats that is in the middle
        ArrayList<Integer> reserved = new ArrayList<>();
        reserved.add(middleCol);
        boolean polarity = true; // set right-side first
        int forward = 1;
        int backward = 1;
        while (noOfSeatsBookedCurrentRow > 1) { // exclude middle seat
            if (polarity) {
                // check if this position already taken, if taken dont add, but add forward to find one that is free.
                if (Validator.isBlank(currentRow[(middleCol + forward) - 1])) {
                    reserved.add(middleCol + forward);
                    noOfSeatsBookedCurrentRow--;
                }
                forward++;
            } else {
                // check if this position already taken, if taken dont add, but add backward to find one that is free.
                if (Validator.isBlank(currentRow[(middleCol + backward) - 1])) {
                    reserved.add(0, middleCol - backward);
                    noOfSeatsBookedCurrentRow--;
                }
                backward++;
            }
            polarity = !polarity;
        }
        return reserved;
    }

    public static ArrayList<Integer> generateCustomReservedCurrentRow(int availableSeatsInCurrentRow, int startPos, String[] currentRow) {
        // asc order only.
        ArrayList<Integer> reserved = new ArrayList<>();
        int forward = startPos;
        while (availableSeatsInCurrentRow > 1) {
            if (Validator.isBlank(currentRow[startPos - 1])) {
                reserved.add(forward);
                availableSeatsInCurrentRow--;
            }
            forward++;
        }
        return reserved;
    }

    public static void clearExistingReservationForBookingId(String currentBookingId) {
        for (String alphabet : ALPHABETS) {
            String[] row = (String[]) Workflows.settings.get(alphabet);
            if (row != null) {
                for (int i = 0; i < row.length; i++) {
                    if (!Validator.isBlank(row[i]) && row[i].equals(currentBookingId)) {
                        row[i] = null;
                    }
                }
            }
        }
    }
}
