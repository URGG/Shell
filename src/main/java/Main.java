import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static String cwd = System.getProperty("user.dir");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display the prompt
            System.out.print("$ ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            // Exit command
            if (input.equals("exit")) {
                System.exit(0);
            }

            // Handle `pwd`
            if (input.equals("pwd")) {
                System.out.println(cwd);
            }
            // Handle `echo` with quoted arguments
            else if (input.startsWith("echo ")) {
                String[] argsEcho = parseInput(input.substring(5)); 
                System.out.println(String.join(" ", argsEcho));
            }
            // Handle `cd`
            else if (input.startsWith("cd ")) {
                String dir = input.substring(3).trim();
                String result = handleCd(dir);
                if (result != null) {
                    System.out.println(result); 
                }
            }
            // Handle `cat`
            else if (input.startsWith("cat ")) {
                String[] argsCat = parseInput(input.substring(4));
                // Print all file contents continuously without extra newlines
                for (String fileName : argsCat) {
                    Path filePath = Path.of(fileName);
                    if (Files.exists(filePath)) {
                        try {
                            List<String> lines = Files.readAllLines(filePath);
                            // Print the file content without adding extra newline between files
                            for (String line : lines) {
                                System.out.print(line);
                            }
                        } catch (Exception e) {
                            System.out.printf("cat: %s: Error reading file%n", fileName);
                        }
                    } else {
                        System.out.printf("cat: %s: No such file or directory%n", fileName);
                    }
                }
                System.out.println(); // Print a final newline at the end of `cat` output
            }
        }
    }

    private static String handleCd(String dir) {
        if (dir.equals("~")) {
            dir = System.getenv("HOME");
        }

        Path path = Paths.get(cwd, dir).toAbsolutePath().normalize();

        if (Files.isDirectory(path)) {
            cwd = path.toString();
            return null;
        } else {
            return "cd: " + dir + ": No such file or directory";
        }
    }

    private static String[] parseInput(String input) {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inDoubleQuotes = false;
        boolean inSingleQuotes = false;
        boolean escaping = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escaping) {
                if (c == 'n') {
                    currentArg.append('\n');  // Handle newline escape
                } else if (c == '\"' || c == '\\' || c == '$') {
                    currentArg.append(c); // Handle escape for special characters
                } else {
                    currentArg.append('\\');  // Just append the backslash if unrecognized
                    currentArg.append(c);
                }
                escaping = false;
            } else if (c == '\\') {
                escaping = true;  // Start of an escape sequence
            } else if (c == '\"') {
                inDoubleQuotes = !inDoubleQuotes;  // Toggle double-quote mode
            } else if (c == '\'') {
                if (inDoubleQuotes) {
                    currentArg.append(c);  // Add single quotes inside double quotes
                } else {
                    inSingleQuotes = !inSingleQuotes;  // Toggle single-quote mode
                }
            } else if (c == ' ' && !inDoubleQuotes && !inSingleQuotes) {
                if (currentArg.length() > 0) {
                    args.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            } else {
                currentArg.append(c);
            }
        }

        if (currentArg.length() > 0) {
            args.add(currentArg.toString());
        }

        return args.toArray(new String[0]);
    }
}
