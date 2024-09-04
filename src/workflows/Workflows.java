package workflows;

import constants.Constants;
import validators.Validator;

import java.util.Scanner;

public class Workflows {

    public static void generateMovieTitleAndSeatingMap(Scanner scanner) {
        while(true) {
            System.out.println(Constants.DEFINE_TITLE_AND_SEATING_MAP);
            String userInput = scanner.nextLine();
            userInput.trim();

            String[] parts = userInput.split("\\s+");


        }
    }
}
