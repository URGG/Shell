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
                handleCat(arguments.subList(1, arguments.size()));
            } else if (command.equals("cd")) {
                handleCd(arguments.size() > 1 ? arguments.get(1) : null);
            } else {
                System.out.printf("%s: command not found%n", command);
            }
        }
    }

    private static void handleCat(List<String> files) {
        StringBuilder output = new StringBuilder();
        for (String filePath : files) {
            File file = new File(currentDirectory, filePath);
            if (file.exists() && file.isFile()) {
                try {
                    output.append(Files.readString(file.toPath()));
                } catch (Exception e) {
                    System.out.printf("cat: %s: Error reading file%n", filePath);
                    return;
                }
            } else {
                System.out.printf("cat: %s: No such file or directory%n", filePath);
                return;
            }
        }
        System.out.println(output.toString().replaceAll("\n", ""));
    }

    private static void handleCd(String path) {
        if (path == null || path.equals("~")) {
            // Change to home directory
            currentDirectory = new File(System.getProperty("user.home"));
        } else {
            File newDir = new File(currentDirectory, path);
            if (newDir.exists() && newDir.isDirectory()) {
                currentDirectory = newDir.getAbsoluteFile();
            } else {
                System.out.printf("cd: %s: No such file or directory%n", path);
            }
        }
    }

    private static List<String> parseArguments(String input) {
        List<String> arguments = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = '"';

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else if (c == '\\') {
                    if (i + 1 < input.length()) {
                        char next = input.charAt(i + 1);
                        if (next == '"' || next == '\\' || next == '$') {
                            currentArg.append(next);
                            i++;
                        } else {
                            currentArg.append(c);
                        }
                    }
                } else {
                    currentArg.append(c);
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuotes = true;
                    quoteChar = c;
                } else if (c == ' ') {
                    if (currentArg.length() > 0) {
                        arguments.add(currentArg.toString());
                        currentArg.setLength(0);
                    }
                } else {
                    currentArg.append(c);
                }
            }
        }

        if (currentArg.length() > 0) {
            arguments.add(currentArg.toString());
        }

        return arguments;
    }
}
