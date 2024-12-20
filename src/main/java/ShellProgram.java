import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShellProgram {

    private static String currentDirectory = System.getProperty("user.dir");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                continue;
            }
            if (input.equals("exit")) {
                break;
            }
            handleInput(input);
        }
        scanner.close();
    }

    private static void handleInput(String input) {
        List<String> tokens = parseInput(input);
        if (tokens.isEmpty()) {
            return;
        }
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

    private static List<String> parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inSingleQuotes) {
                if (c == '\'') {
                    inSingleQuotes = false;
                } else {
                    currentToken.append(c);
                }
            } else if (inDoubleQuotes) {
                if (c == '"') {
                    inDoubleQuotes = false;
                } else if (c == '\\' && i + 1 < input.length() && "\"$".indexOf(input.charAt(i + 1)) >= 0) {
                    currentToken.append(input.charAt(++i));
                } else {
                    currentToken.append(c);
                }
            } else {
                if (Character.isWhitespace(c)) {
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                } else if (c == '\'') {
                    inSingleQuotes = true;
                } else if (c == '"') {
                    inDoubleQuotes = true;
                } else {
                    currentToken.append(c);
                }
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private static void handleEcho(List<String> arguments) {
        System.out.println(String.join(" ", arguments));
    }

    private static void handleCat(List<String> arguments) {
        if (arguments.isEmpty()) {
            System.out.println("cat: missing operand");
            return;
        }

        StringBuilder output = new StringBuilder();

        for (String filePath : arguments) {
            File file = filePath.startsWith("/") ? new File(filePath) : new File(currentDirectory, filePath);

            if (file.exists() && file.isFile()) {
                try (Scanner fileScanner = new Scanner(file)) {
                    while (fileScanner.hasNextLine()) {
                        output.append(fileScanner.nextLine()).append(".");
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

        if (output.length() > 0 && output.charAt(output.length() - 1) == '.') {
            output.setLength(output.length() - 1);
        }

        System.out.println(output);
    }
}

