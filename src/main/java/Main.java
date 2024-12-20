import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Set<String> commands = Set.of("cd", "echo", "exit", "pwd", "type");
        Scanner scanner = new Scanner(System.in);
        String cwd = Path.of("").toAbsolutePath().toString();
        
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            if (input.equals("exit 0")) {
                System.exit(0);
            } else if (input.startsWith("echo ")) {
                String[] echoArgs = parseInput(input.substring(5).trim()); // Trim input before parsing
                System.out.println(String.join(" ", echoArgs).trim()); // Join without extra spaces and trim
            } else if (input.startsWith("cat ")) {
                String[] catArgs = parseInput(input.substring(4).trim()); // Trim input before parsing
                StringBuilder output = new StringBuilder();

                for (String fileName : catArgs) {
                    Path filePath = Path.of(fileName);
                    if (Files.exists(filePath)) {
                        List<String> lines = Files.readAllLines(filePath);
                        for (String line : lines) {
                            output.append(line).append(" "); // Append lines with spaces
                        }
                    } else {
                        System.out.printf("cat: %s: No such file or directory%n", fileName);
                    }
                }
                System.out.println(output.toString().trim()); // Remove extra space at the end
            } else if (input.startsWith("type ")) {
                String arg = input.substring(5);
                if (commands.contains(arg)) {
                    System.out.printf("%s is a shell builtin%n", arg);
                } else {
                    String path = getPath(arg);
                    if (path == null) {
                        System.out.printf("%s: not found%n", arg);
                    } else {
                        System.out.printf("%s is %s%n", arg, path);
                    }
                }
            } else if (input.equals("pwd")) {
                System.out.println(cwd);
            } else if (input.startsWith("cd ")) {
                String dir = input.substring(3).trim();
                
                if (dir.equals("~")) {
                    dir = System.getenv("HOME");
                } else if (!dir.startsWith("/")) {
                    dir = cwd + "/" + dir;
                }
                
                if (Files.isDirectory(Path.of(dir))) {
                    cwd = Path.of(dir).normalize().toString();
                } else {
                    System.out.printf("cd: %s: No such file or directory%n", dir);
                }
            } else {
                String command = input.split(" ")[0];
                String path = getPath(command);
                if (path == null) {
                    System.out.printf("%s: command not found%n", command);
                } else {
                    String fullPath = path + input.substring(command.length());
                    Process p = Runtime.getRuntime().exec(fullPath.split(" "));
                    p.getInputStream().transferTo(System.out);
                }
            }
        }
    }

    private static String[] parseInput(String input) {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\'') {
                inSingleQuotes = !inSingleQuotes; // Toggle the inSingleQuotes flag
                currentArg.append(c); // Keep the single quote if inside it
            } else if (c == '\"') {
                inDoubleQuotes = !inDoubleQuotes; // Toggle the inDoubleQuotes flag
            } else if (c == '\\') {
                if (i + 1 < input.length()) {
                    char nextChar = input.charAt(i + 1);
                    if (nextChar == '\'' || nextChar == '\"' || nextChar == '\\') {
                        currentArg.append(nextChar); // Append escaped character
                        i++; // Skip the next character
                    } else {
                        currentArg.append(c); // Append backslash as is
                    }
                } else {
                    currentArg.append(c); // Append backslash as is
                }
            } else if (c == ' ' && !inSingleQuotes && !inDoubleQuotes) {
                if (currentArg.length() > 0) {
                    args.add(currentArg.toString().trim()); // Ensure no leading/trailing spaces
                    currentArg.setLength(0); // Reset for the next argument
                }
            } else {
                currentArg.append(c); // Add the character to the current argument
            }
        }

        // Add the last argument if there's any
        if (currentArg.length() > 0) {
            args.add(currentArg.toString().trim()); // Trim extra spaces
        }

        return args.toArray(new String[0]);
    }

    private static String getPath(String command) {
        for (String path : System.getenv("PATH").split(":")) {
            Path fullPath = Path.of(path, command);
            if (Files.isRegularFile(fullPath)) {
                return fullPath.toString();
            }
        }
        return null;
    }
}
