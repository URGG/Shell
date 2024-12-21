import static command.Pwd.pwd;
import static util.StringUtils.parseCommand;
import java.io.File;
import java.nio.file.Files;


public class Main implements Strategy {
    public static final String HOME_DIR = System.getenv("HOME");

    @Override
    public String command(String input) {
        String[] tokens = parseCommand(input);
        if (tokens.length > 1) {
            String dir = tokens[1];
            dir = getAbsPath(dir);
            return checkDir(dir);
        }
        return null; // No directory specified
    }

    public static String getAbsPath(String dir) {
        // Handle absolute path
        if (dir.startsWith(File.separator)) {
            return dir; // Return as is
        } 
        // Handle parent directory navigation
        else if (dir.startsWith("..")) {
            File currentDir = new File(pwd);
            File moveTo = currentDir.getParentFile(); // Start from the current directory

            // Split the path by "/"
            String[] parts = dir.split(File.separator);
            for (String part : parts) {
                if (part.equals("..")) {
                    moveTo = moveTo.getParentFile(); // Move up one directory
                } else if (!part.isEmpty() && !part.equals(".")) {
                    moveTo = new File(moveTo, part); // Move into the specified directory
                }
            }
            return moveTo.getAbsolutePath(); // Return the resolved absolute path
        } 
        // Handle home directory
        else if ("~".equals(dir)) {
            return HOME_DIR; // Return home directory
        } 
        // Handle current directory
        else if (dir.startsWith(".")) {
            return new File(pwd, dir.substring(1)).getAbsolutePath(); // Current directory
        } 
        // Handle relative path
        else {
            return new File(pwd, dir).getAbsolutePath(); // Append to current working directory
        }
    }

    private static String checkDir(String dir) {
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            pwd = file.getAbsolutePath(); // Update the current working directory
            return null; // Successful change
        } else {
            return String.format("cd: %s: No such file or directory%n", dir); // Error message
        }
    }

    // For testing purposes
    public static void main(String[] args) {
        String input = "cd /tmp/mango/raspberry/apple"; // Example input
        Main cd = new Main();
        String command = cd.command(input);
        System.out.println(command); // Output the result of the command
    }
}