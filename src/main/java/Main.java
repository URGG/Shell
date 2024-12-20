import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Main shell loop
        while (true) {
            // Prompt for user input
            System.out.print("$ ");
            String input = scanner.nextLine().trim();

            // Exit condition for the loop
            if (input.equals("exit")) {
                break;
            }

            // Split input while respecting quotes
            List<String> tokens = parseInput(input);

            // If no tokens were parsed, continue to next loop iteration
            if (tokens.isEmpty()) {
                continue;
            }

            // Extract command and arguments
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
            }
        }
    }

    // Parse input respecting double quotes
    private static List<String> parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes; // Toggle quote state
            } else if (c == '\\' && inQuotes && i + 1 < input.length()) {
                char next = input.charAt(i + 1);
                if (next == '"' || next == '\\') {
                    currentToken.append(next);
                    i++; // Skip next character
                } else {
                    currentToken.append(c); // Append backslash as-is
                }
            } else if (!inQuotes && Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Reset token
                }
            } else {
                currentToken.append(c);
            }
        }

        // Add the last token if any
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    // Handle 'echo' command
    private static void handleEcho(List<String> arguments) {
        System.out.println(String.join(" ", arguments));
    }

    // Handle 'cat' command
    private static void handleCat(List<String> arguments) {
        if (arguments.isEmpty()) {
            System.out.println("cat: missing operand");
            return;
        }

        StringBuilder output = new StringBuilder();
        boolean firstFile = true;

        for (String filePath : arguments) {
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                try (Scanner fileScanner = new Scanner(file)) {
                    if (!firstFile) {
                        output.append(" ");
                    }
                    firstFile = false;

                    while (fileScanner.hasNextLine()) {
                        output.append(fileScanner.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("cat: " + filePath + ": No such file");
                }
            } else {
                System.out.println("cat: " + filePath + ": No such file");
            }
        }

        // Print concatenated file contents
        if (output.length() > 0) {
            System.out.println(output.toString());
        }
    }
}
