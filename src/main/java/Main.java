import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Main loop for shell
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            // Break if input is 'exit'
            if (input.trim().equals("exit")) {
                break;
            }

            // Split the input into tokens
            List<String> tokens = new ArrayList<>(Arrays.asList(input.split("\\s+")));

            // Handle different commands
            String command = tokens.get(0);
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

    public static void handleEcho(List<String> tokens) {
        // Print arguments for echo
        StringBuilder output = new StringBuilder();
        for (int i = 1; i < tokens.size(); i++) {
            output.append(tokens.get(i));
            if (i != tokens.size() - 1) {
                output.append(" ");
            }
        }
        System.out.println(output.toString());
    }

    public static void handleCat(List<String> tokens) {
        if (tokens.size() < 2) {
            System.out.println("cat: missing operand");
            return;
        }

        boolean firstFile = true;

        for (int i = 1; i < tokens.size(); i++) {
            String filePath = tokens.get(i);
            try {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    Scanner fileScanner = new Scanner(file);
                    if (!firstFile) {
                        System.out.print("."); // Add period between contents of files
                    }
                    firstFile = false;
                    // Print the content of the current file
                    while (fileScanner.hasNextLine()) {
                        System.out.print(fileScanner.nextLine());
                    }
                    fileScanner.close();
                } else {
                    System.out.println("cat: " + filePath + ": No such file");
                }
            } catch (FileNotFoundException e) {
                System.out.println("cat: " + filePath + ": No such file");
            }
        }
    }
}
