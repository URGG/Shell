import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Print prompt
            System.out.print("$ ");
            String input = scanner.nextLine();

            // Exit if input is empty or contains only whitespace
            if (input.trim().isEmpty()) {
                continue;
            }

            // Exit shell if the user types "exit"
            if (input.equals("exit")) {
                break;
            }

            // Tokenize input, respecting quoted strings
            List<String> tokens = tokenizeInput(input);

            if (tokens.isEmpty()) {
                continue;
            }

            // Handle commands
            String command = tokens.get(0);
            List<String> arguments = tokens.subList(1, tokens.size());

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

        scanner.close();
    }

    private static List<String> tokenizeInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inSingleQuote = false, inDoubleQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            if ((c == ' ' || c == '\t') && !inSingleQuote && !inDoubleQuote) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
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

    private static void handleEcho(List<String> arguments) {
        System.out.println(String.join(" ", arguments));
    }



    private static void handleCat(List<String> arguments) {
        StringBuilder result = new StringBuilder();
        boolean firstFile = true; // Track if this is the first file being processed
    
        for (String filePath : arguments) {
            try {
                String content = Files.readString(Paths.get(filePath));
                if (!firstFile) {
                    result.append("."); // Append '.' only after the first file
                }
                result.append(content);
                firstFile = false; // Mark that at least one file has been processed
            } catch (IOException e) {
                System.out.println("cat: " + filePath + ": No such file");
            }
        }
    
        // Print the result if any content was appended
        if (result.length() > 0) {
            System.out.println(result.toString());
        }
    }
   
}


