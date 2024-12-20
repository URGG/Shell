import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        Set<String> commands = Set.of("cd", "echo", "exit", "pwd", "type", "cat");
        Scanner scanner = new Scanner(System.in);
        String cwd = Path.of("").toAbsolutePath().toString();
        
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            if (input.equals("exit 0")) {
                System.exit(0);
            } else if (input.startsWith("echo ")) {
                String[] echoArgs = parseInput(input.substring(5)); // Renamed variable
                System.out.println(String.join(" ", echoArgs)); // Print the quoted content
            } else if (input.startsWith("cat ")) {
                String[] catArgs = parseInput(input.substring(4)); // Renamed variable
                // Handle the cat command with args as file names
                for (String fileName : catArgs) {
                    Path filePath = Path.of(fileName);
                    if (Files.exists(filePath)) {
                        Files.lines(filePath).forEach(System.out::println);
                    } else {
                        System.out.printf("cat: %s: No such file or directory%n", fileName);
                    }
                }
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
                
                // Handle the ~ character
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
        boolean inQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '\'') {
                inQuotes = !inQuotes; // Toggle the inQuotes flag
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    args.add(currentArg.toString());
                    currentArg.setLength(0); // Reset for the next argument
                }
            } else {
                currentArg.append(c);
            }
        }

        // Add the last argument if it exists
        if (currentArg.length() > 0) {
            args.add(currentArg.toString());
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