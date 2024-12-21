import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main{
    public static void main(String[] args) {
        // Simulating input for testing
        String input = "echo \"quz  hello\"  \"bar\""; // Example input
        String[] tokens = parseInput(input);
        
        // Execute commands based on tokens
        executeCommand(tokens);
        
        // Print the prompt after command execution
        printPrompt();
    }

    private static String[] parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (c == '\"') {
                inQuotes = !inQuotes; // Toggle quoting state
            } else if (c == '\\' && inQuotes) {
                // Handle escape sequences
                if (i + 1 < input.length()) {
                    currentToken.append(input.charAt(i + 1));
                    i++; // Skip the next character
                }
            } else if (c == ' ' && !inQuotes) {
                // Token delimiter
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Reset for next token
                }
            } else {
                currentToken.append(c);
            }
        }
        
        // Add the last token if exists
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        
        return tokens.toArray(new String[0]);
    }

    private static void executeCommand(String[] tokens) {
        if (tokens.length == 0) return;

        String command = tokens[0];
        
        switch (command) {
            case "echo":
                echoCommand(tokens);
                break;
            case "cat":
                catCommand(tokens);
                break;
            default:
                System.out.println("Command not found: " + command);
        }
    }

    private static void echoCommand(String[] tokens) {
        for (int i = 1; i < tokens.length; i++) {
            System.out.print(tokens[i]);
            if (i < tokens.length - 1) {
                System.out.print(" "); // Add space between tokens
            }
        }
        System.out.println(); // New line at the end
    }

    private static void catCommand(String[] tokens) {
        for (int i = 1; i < tokens.length; i++) {
            String fileName = tokens[i].replaceAll("^\"|\"$", ""); // Remove surrounding quotes
            readFile(fileName);
        }
    }

    private static void readFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.print(line + " "); // Print file content
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + fileName);
        }
    }

    private static void printPrompt() {
        System.out.print("$ "); // Print the shell prompt
    }
}
