import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;

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
                System.out.println(input.substring(5));
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
                    dir = System.getenv("HOME"); // Get the home directory
                } else if (!dir.startsWith("/")) {
                    dir = cwd + "/" + dir; // Handle relative paths
                }
                
                // Check if the directory exists
                if (Files.isDirectory(Path.of(dir))) {
                    cwd = Path.of(dir).normalize().toString(); // Update current working directory
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