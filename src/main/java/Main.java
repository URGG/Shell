import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.lang.*;

public class Main {
    private static File currentDirectory = new File(System.getProperty("user.dir"));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            if (input.equals("exit 0")) {
                scanner.close();
                break;
            } else if (input.startsWith("echo ")) {
                List<String> arguments = parseInput(input.substring(5));
                // Print the arguments without additional quotes
                System.out.println(String.join(" ", arguments));
            } else if (input.startsWith("type ")) {
                List<String> builtInCommands = Arrays.asList("echo", "type", "exit", "pwd", "cd");
                String command = input.substring(5).trim();
                if (builtInCommands.contains(command)) {
                    System.out.println(command + " is a shell builtin");
                } else {
                    checkExecutable(command);
                }
            } else if (input.equals("pwd")) {
                System.out.println(currentDirectory.getAbsolutePath());
            } else if (input.startsWith("cd ")) {
                String path = input.substring(3).trim();
                changeDirectory(path);
            } else {
                executeExternalCommand(input);
            }
        }
    }

    private static void executeExternalCommand(String input) {
        String[] commandParts = input.split(" ");
        String command = commandParts[0];
        String path = getPath(command);
        
        if (path == null) {
            System.out.printf("%s: command not found%n", command);
        } else {
            try {
                // Use ProcessBuilder to handle command and arguments
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(path, Arrays.copyOfRange(commandParts, 1, commandParts.length));
                Process p = processBuilder.start();
                p.getInputStream().transferTo(System.out);
                p.getErrorStream().transferTo(System.err);
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
            }
        }
    }

    private static void changeDirectory(String path) {
        File newDirectory;
        if (path.startsWith("/")) {
            // Absolute path
            newDirectory = new File(path);
        } else if (path.equals("~")) {
            newDirectory = new File(System.getenv("HOME"));
        } else {
            // Relative path
            newDirectory = currentDirectory.toPath().resolve(path).normalize().toFile();
        }
        if (newDirectory.exists() && newDirectory.isDirectory()) {
            currentDirectory = newDirectory;
        } else {
            System.out.println("cd: " + path + ": No such file or directory");
        }
    }

    private static void checkExecutable(String command) {
        if (System.getenv("PATH") != null) {
            String pathEnv = System.getenv("PATH");
            String[] paths = pathEnv.split(":");
            boolean found = false;
            for (String path : paths) {
                File file = new File(path + "/" + command);
                if (file.exists() && file.canExecute()) {
                    System.out.println(command + " is " + file.getAbsolutePath());
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println(command + ": not found");
            }
        } else {
            System.out.println(command + ": not found");
        }
    }

    private static List<String> parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // Toggle the inQuotes flag
            } else if (c == ' ' && !inQuotes) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Reset the current token
                }
            } else {
                currentToken.append(c);
            }
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString()); // Add the last token
        }
        return tokens;
    }

    private static String getPath(String input) {
        for (String path : System.getenv("PATH").split(":")) {
            Path file = Path.of(path, input);
            if (Files.isReadable(file)) {
                return file.toString();
            }
        }
        return null;
    }
}