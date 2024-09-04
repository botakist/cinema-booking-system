import constants.Constants;
import workflows.Workflows;

import java.util.Scanner;

public class CinemaBookingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(Constants.DEFINE_TITLE_AND_SEATING_MAP);
        Workflows.generateMovieTitleAndSeatingMap(scanner);
    }
}