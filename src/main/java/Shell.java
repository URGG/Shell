import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shell {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ "); // Display the shell prompt
            String input = scanner.nextLine();

            // Exit on empty input or specific commands like "exit"
            if (input.trim().equalsIgnoreCase("exit") || input.trim().isEmpty()) {
                break;
            }

            // Parse the command and arguments
            List<String> tokens = parseInput(input);

            if (tokens.isEmpty()) {
                continue;
            }

            String command = tokens.get(0);
            List<String> arguments = tokens.subList(1, tokens.size());

            // Handle commands
            switch (command) {
                case "echo":
                    handleEcho(arguments);
                    break;
                case "cat":
                    handleCat(arguments);
                    break;
                default:
                    System.out.println(command + ": command not found");
                    break;
            }
        }

        scanner.close();
    }

    // Parse input while handling single and double quotes
    private static List<String> parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"' && !inSingleQuotes) { // Toggle double-quote mode
                inDoubleQuotes = !inDoubleQuotes;
                continue;
            } else if (c == '\'' && !inDoubleQuotes) { // Toggle single-quote mode
                inSingleQuotes = !inSingleQuotes;
                continue;
            }

            if (c == ' ' && !inSingleQuotes && !inDoubleQuotes) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    // Handle the "echo" command
    private static void handleEcho(List<String> arguments) {
        System.out.println(String.join(" ", arguments));
    }

    // Handle the "cat" command
    private static void handleCat(List<String> arguments) {
        if (arguments.isEmpty()) {
            System.out.println("cat: missing operand");
            return;
        }

        StringBuilder output = new StringBuilder();

        for (String filePath : arguments) {
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                try (Scanner fileScanner = new Scanner(file)) {
                    while (fileScanner.hasNextLine()) {
                        output.append(fileScanner.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("cat: " + filePath + ": No such file");
                    return;
                }
            } else {
                System.out.println("cat: " + filePath + ": No such file");
                return;
            }
        }

        // Print concatenated file contents followed by a newline
        System.out.println(output.toString());
    }
}

