import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();

            if (input.equals("exit")) {
                break;
            }

            List<String> tokens = parseInput(input);

            if (tokens.isEmpty()) {
                continue;
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
    }

    private static List<String> parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == '\\' && inQuotes && i + 1 < input.length()) {
                char next = input.charAt(i + 1);
                if (next == '"' || next == '\\') {
                    currentToken.append(next);
                    i++;
                } else {
                    currentToken.append(c);
                }
            } else if (!inQuotes && Character.isWhitespace(c)) {
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

        System.out.print(output.toString());
    }
}
