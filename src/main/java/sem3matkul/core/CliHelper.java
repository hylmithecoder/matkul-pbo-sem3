package sem3matkul.core;

import java.util.*;

public class CliHelper {
    public static void print(String message) {
        System.out.println(message);
    }

    public static void printWithoutNewline(String message) {
        System.out.print(message);
    }

    public static void error(String message) {
        System.out.println("Error: " + message);
    }
    
    public static int inputInt() {
        Scanner scanner = new Scanner(System.in);
        int input = 0;
        if (scanner.hasNextInt()) {
            input = scanner.nextInt();
        } else {
            error("Please input only integer number");
            return inputInt();
        }
        
        scanner.close();
        return input;
    }

    public static String inputString() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.isBlank()) {
            error("Please input a string");
            return inputString();
        } else if (input.contains("\n")){
            error("Please input only a single line");
            return inputString();
        }
        scanner.close();
        return input;
    }
}