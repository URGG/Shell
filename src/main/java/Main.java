import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    // Main method to run the shell program
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print("$ ");
            input = scanner.nextLine().trim();

            // Exit the program if "exit" is typed
            if (input.equals("exit")) {
                break;
            }

            // Process the input command
            processCommand(input);
        }
    }

    // Process the command based on its type
    public static void processCommand(String command) {
        // Split the input by spaces, handling quoted strings properly
        List<String> tokens = tokenize(command);
        
        if (tokens.isEmpty()) return;

        String cmd = tokens.get(0);

        switch (cmd) {
            case "echo":
                handleEcho(tokens);
                break;
            case "cd":
                handleCd(tokens);
                break;
            case "cat":
                handleCat(tokens);
                break;
            default:
                System.out.println(cmd + ": command not found");
                break;
        }
    }

    // Tokenize the command input, preserving quoted parts
    public static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean inQuote = false;

        // Iterate through the input and handle spaces and quotes
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == ' ' && !inQuote) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else if (c == '"') {
                inQuote = !inQuote;  // Toggle the quote flag
            } else {
                token.append(c);
            }
        }

        // Add the last token if it exists
        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }

    // Handle the echo command
    public static void handleEcho(List<String> tokens) {
        tokens.remove(0); // Remove the 'echo' command

        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) {
                System.out.print(" "); // Add a space between arguments
            }
            System.out.print(tokens.get(i));
        }

        System.out.println();
    }

    // Handle the cd command
    public static void handleCd(List<String> tokens) {
        if (tokens.size() < 2) {
            System.out.println("cd: missing operand");
            return;
        }

        String path = tokens.get(1);

        try {
            Path newPath = Paths.get(path).toAbsolutePath();
            File dir = newPath.toFile();
            if (dir.exists() && dir.isDirectory()) {
                System.setProperty("user.dir", newPath.toString());
            } else {
                System.out.println("cd: " + path + ": No such file or directory");
            }
        } catch (Exception e) {
            System.out.println("cd: error: " + e.getMessage());
        }
    }

    // Handle the cat command
    public static void handleCat(List<String> tokens) {
        if (tokens.size() < 2) {
            System.out.println("cat: missing operand");
            return;
        }

        for (int i = 1; i < tokens.size(); i++) {
            String filePath = tokens.get(i);
            try {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    Scanner fileScanner = new Scanner(file);
                    while (fileScanner.hasNextLine()) {
                        System.out.println(fileScanner.nextLine());
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
