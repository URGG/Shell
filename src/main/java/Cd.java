import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Cd {

    // Current working directory
    private static String cwd = System.getProperty("user.dir");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display the prompt
            System.out.print("$ ");
            String input = scanner.nextLine();

            // Parse the input command (split by space)
            String[] command = input.split(" ", 2);

            if (command[0].equals("cd")) {
                // Handle the "cd" command
                String result = handleCd(command);
                if (result != null) {
                    System.out.println(result);  // Print error message if any
                }
            } else {
                System.out.println("Command not recognized");
            }
        }
    }

    public static String handleCd(String[] command) {
        if (command.length == 1) {
            // No directory argument, simply print the current directory
            return cwd;
        }

        // Get the directory from the command argument
        String dir = command[1].trim();

        // Handle the special case of "~" (home directory)
        if (dir.equals("~")) {
            dir = System.getenv("HOME");
        } else if (dir.equals(".")) {
            // No change needed for current directory (.)
            return null;
        }

        // If path is not absolute, resolve it relative to current working directory
        Path path = Paths.get(cwd, dir);

        // Normalize and get the absolute path
        path = path.toAbsolutePath().normalize();

        // Check if the path exists and is a directory
        if (Files.isDirectory(path)) {
            cwd = path.toString();  // Update current working directory
            return null;
        } else {
            return "cd: " + dir + ": No such file or directory";
        }
    }
}
