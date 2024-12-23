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
                case "pwd":
                    handlePwd();
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

        for (char c : input.toCharArray()) {
            if (c == '\'') {
                if (inDoubleQuotes) {
                    currentToken.append(c);
                } else {
                    inSingleQuotes = !inSingleQuotes;
                }
            } else if (c == '"') {
                if (inSingleQuotes) {
                    currentToken.append(c);
                } else {
                    inDoubleQuotes = !inDoubleQuotes;
                }
            } else if (Character.isWhitespace(c) && !inSingleQuotes && !inDoubleQuotes) {
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

    private static void handleEcho(List<String> args) {
        String output = String.join(" ", args);
        System.out.println(output);
    }

    private static void handleCat(List<String> files) {
        StringBuilder output = new StringBuilder();

        for (String filePath : files) {
            // Handle quoted paths correctly
            if ((filePath.startsWith("\"") && filePath.endsWith("\"")) ||
                (filePath.startsWith("'") && filePath.endsWith("'"))) {
                filePath = filePath.substring(1, filePath.length() - 1);
            }

            // Resolve the absolute file path
            File file = new File(filePath);

            if (!file.isAbsolute()) {
                // Resolve relative to current directory
                file = new File(currentDirectory, filePath);
            }

            if (file.exists() && file.isFile()) {
                try {
                    // Read file content
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
            // If no argument is provided, change to the user's home directory
            currentDirectory = new File(System.getProperty("user.home"));
            return;
        }

        String path = args.get(0);

        // Handle ~ (tilde) shorthand for the user's home directory
        if (path.startsWith("~")) {
            path = System.getProperty("user.home") + path.substring(1);
        }

        // Handle quoted paths
        if ((path.startsWith("\"") && path.endsWith("\"")) ||
            (path.startsWith("'") && path.endsWith("'"))) {
            path = path.substring(1, path.length() - 1);
        }

        File newDir = new File(path);

        if (!newDir.isAbsolute()) {
            // Resolve relative to current directory
            newDir = new File(currentDirectory, path);
        }

        if (newDir.exists() && newDir.isDirectory()) {
            currentDirectory = newDir;
        } else {
            System.out.printf("cd: %s: No such file or directory%n", path);
        }
    }

    private static void handlePwd() {
        // Output the current working directory
        System.out.println(currentDirectory.getAbsolutePath());
    }
}
