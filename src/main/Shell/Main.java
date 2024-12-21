import java.io.File;
import java.util.Scanner;

public class Main {
    private static String currentDir = System.getProperty("user.dir"); // Start in the current working directory

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print(currentDir + " $ "); // Prompt with the current directory
            input = scanner.nextLine();

            if (input.equals("exit")) {
                break; // Exit the loop on "exit" command
            }

            String result = changeDirectory(input);
            if (result != null) {
                System.out.println(result); // Print error messages if any
            }
        }

        scanner.close();
    }

    private static String changeDirectory(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length > 1) {
            String dir = tokens[1];
            String absPath = getAbsolutePath(dir);
            return checkDirectory(absPath);
        }
        return "cd: missing argument"; // Error if no directory is specified
    }

    private static String getAbsolutePath(String dir) {
        if (dir.startsWith("/")) {
            return dir; // Absolute path
        } else if ("~".equals(dir)) {
            return System.getProperty("user.home"); // Home directory
        } else if (dir.startsWith(".")) {
            return new File(currentDir, dir).getAbsolutePath(); // Current directory
        } else if (dir.startsWith("..")) {
            return new File(currentDir, dir).getAbsolutePath(); // Parent directory
        } else {
            return new File(currentDir, dir).getAbsolutePath(); // Relative path
        }
    }

    private static String checkDirectory(String dir) {
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            currentDir = file.getAbsolutePath(); // Update current directory
            return null; // Successful change
        } else {
            return String.format("cd: %s: No such file or directory", dir); // Error message
        }
    }
}