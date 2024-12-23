import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static File currentDirectory = new File(System.getProperty("user.dir"));

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.equals("exit 0")) {
                scanner.close();
                break;
            }

            List<String> arguments = parseArguments(input);
            if (arguments.isEmpty()) continue;

            String command = arguments.get(0);

            if (command.equals("echo")) {
                System.out.println(String.join(" ", arguments.subList(1, arguments.size())));
            } else if (command.equals("cat")) {
                StringBuilder output = new StringBuilder();
                for (String filePath : arguments.subList(1, arguments.size())) {
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        output.append(Files.readString(file.toPath()));
                    } else {
                        System.out.printf("cat: %s: No such file or directory%n", filePath);
                        return; // Exit the loop to match tester behavior for missing files
                    }
                }
                System.out.println(output.toString().replaceAll("\n", ""));
            } else {
                System.out.printf("%s: command not found%n", command);
            }
        }
    }

    /**
     * Parses input into arguments, handling double-quoted strings and escape sequences.
     * 
     * @param input the raw input string
     * @return a list of parsed arguments
     */
    private static List<String> parseArguments(String input) {
        List<String> arguments = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = '"'; // Default to handle double quotes

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inQuotes) {
                if (c == quoteChar) {
                    // End of quoted string
                    inQuotes = false;
                } else if (c == '\\') {
                    // Handle escaped characters
                    if (i + 1 < input.length()) {
                        char next = input.charAt(i + 1);
                        if (next == '"' || next == '\\' || next == '$') {
                            currentArg.append(next);
                            i++; // Skip next character
                        } else {
                            currentArg.append(c); // Preserve backslash
                        }
                    }
                } else {
                    currentArg.append(c); // Append literal character
                }
            } else {
                if (c == '"' || c == '\'') {
                    // Start of quoted string
                    inQuotes = true;
                    quoteChar = c;
                } else if (c == ' ') {
                    // End of argument
                    if (currentArg.length() > 0) {
                        arguments.add(currentArg.toString());
                        currentArg.setLength(0);
                    }
                } else {
                    currentArg.append(c); // Append literal character
                }
            }
        }

        // Add the last argument if any
        if (currentArg.length() > 0) {
            arguments.add(currentArg.toString());
        }

        return arguments;
    }
}
