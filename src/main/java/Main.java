import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Main loop for shell simulation
        while (true) {
            // Prompt for input
            System.out.print("$ ");
            String input = scanner.nextLine().trim();

            // Exit condition for the loop
            if (input.equals("exit")) {
                break;
            }

            // Split input into tokens based on whitespace
            String[] tokens = input.split("\\s+");

            // Handle different commands
            String command = tokens[0];
            switch (command) {
                case "echo":
                    handleEcho(tokens);
                    break;
                case "cat":
                    handleCat(tokens);
                    break;
                default:
                    System.out.println(command + ": command not found");
            }
        }
    }

    // Handle 'echo' command
    private static void handleEcho(String[] tokens) {
        // If the tokens contain no content after 'echo', return nothing
        if (tokens.length <= 1) {
            System.out.println();
            return;
        }

        // Print the arguments separated by a single space
        StringBuilder output = new StringBuilder();
        for (int i = 1; i < tokens.length; i++) {
            output.append(tokens[i]);
            if (i != tokens.length - 1) {
                output.append(" ");  // Add space between arguments
            }
        }
        System.out.println(output.toString());
    }

    // Handle 'cat' command
    private static void handleCat(String[] tokens) {
        // If there are no files given after 'cat', show error
        if (tokens.length <= 1) {
            System.out.println("cat: missing operand");
            return;
        }

        StringBuilder output = new StringBuilder();
        boolean firstFile = true;

        // Loop through all file arguments provided after 'cat'
        for (int i = 1; i < tokens.length; i++) {
            String filePath = tokens[i];
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                try (Scanner fileScanner = new Scanner(file)) {
                    // If not the first file, add period between file contents
                    if (!firstFile) {
                        output.append(".");
                    }
                    firstFile = false;

                    // Read file content and append to output
                    while (fileScanner.hasNextLine()) {
                        output.append(fileScanner.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    // This block should not be necessary due to the exists check above.
                    System.out.println("cat: " + filePath + ": No such file");
                }
            } else {
                // Handle file not found scenario
                System.out.println("cat: " + filePath + ": No such file");
            }
        }

        // Output the concatenated result of file contents
        if (output.length() > 0) {
            System.out.println(output.toString());
        }
    }
}
