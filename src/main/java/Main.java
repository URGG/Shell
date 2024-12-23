import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static File currentDirectory = new File(System.getProperty("user.dir"));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            if (input.trim().isEmpty()) {
                continue;
            }

            List<String> tokens = tokenize(input);
            String command = tokens.get(0);
            List<String> arguments = tokens.subList(1, tokens.size());

            switch (command) {
                case "echo":
                    handleEcho(arguments);
                    break;
                case "cat":
                    handleCat(arguments);
                    break;
                case "cd":
                    handleCd(arguments);
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.out.printf("%s: command not found%n", command);
            }
        }
    }

    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean escapeNext = false;

        for (char c : input.toCharArray()) {
            if (escapeNext) {
                // If escapeNext is true, we add the current character as is and reset escapeNext
                currentToken.append(c);
                escapeNext = false;
            } else if (c == '\\') {
                // If we encounter a backslash, mark the next character as escaped
                escapeNext = true;
            } else if (c == '"' && !inSingleQuotes) {
                // Toggle double quotes state if we're not inside single quotes
                if (inDoubleQuotes) {
                    tokens.add(currentToken.toString()); // Add token when closing double quotes
                    currentToken.setLength(0); // Reset current token builder
                }
                inDoubleQuotes = !inDoubleQuotes; // Toggle the state
            } else if (c == '\'' && !inDoubleQuotes) {
                // Toggle single quotes state if we're not inside double quotes
                inSingleQuotes = !inSingleQuotes;
            } else if (Character.isWhitespace(c) && !inSingleQuotes && !inDoubleQuotes) {
                // Handle token separation on whitespace when not inside quotes
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Reset token builder
                }
            } else {
                // Append regular characters or characters inside quotes to the current token
                currentToken.append(c);
            }
        }

        // Add the last token if exists
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private static void handleEcho(List<String> args) {
        String output = String.join(" ", args);
        System.out.println(output);
    }

    private static void handleCat(List<String> files) {
        StringBuilder output = new StringBuilder();

        for (String filePath : files) {
            File file;
            // Handle quoted paths correctly
            if ((filePath.startsWith("\"") && filePath.endsWith("\"")) ||
                (filePath.startsWith("'") && filePath.endsWith("'"))) {
                filePath = filePath.substring(1, filePath.length() - 1);
            }

            // Resolve file relative to current directory
            file = new File(currentDirectory, filePath);

            if (file.exists() && file.isFile()) {
                try {
                    output.append(Files.readString(file.toPath()));
                } catch (IOException e) {
                    System.out.printf("cat: %s: Error reading file%n", filePath);
                    return;
                }
            } else {
                System.out.printf("cat: %s: No such file or directory%n", filePath);
                return;
            }
        }

        // Print the concatenated output without line breaks
        System.out.print(output.toString());
    }

    private static void handleCd(List<String> args) {
        if (args.isEmpty()) {
            currentDirectory = new File(System.getProperty("user.home"));
            return;
        }

        String path = args.get(0);

        if ((path.startsWith("\"") && path.endsWith("\"")) ||
            (path.startsWith("'") && path.endsWith("'"))) {
            path = path.substring(1, path.length() - 1);
        }

        File newDir = new File(currentDirectory, path);

        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir;
        } else {
            System.out.printf("cd: %s: No such file or directory%n", path);
        }
    }
}
