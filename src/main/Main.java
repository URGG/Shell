import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print("$ "); // Prompt
            input = scanner.nextLine();

            if (input.equals("exit")) {
                break; // Exit the loop on "exit" command
            }

            if (input.startsWith("cat ")) {
                // Extract file paths from the command
                String[] parts = input.split(" ");
                List<String> filePaths = List.of(parts).subList(1, parts.length);
                handleCat(filePaths);
            } else {
                System.out.println("Unknown command: " + input);
            }
        }

        scanner.close();
    }

    private static void handleCat(List<String> filePaths) {
        StringBuilder result = new StringBuilder();
        boolean isFirstContent = true; // Flag to track the first valid content

        for (String filePath : filePaths) {
            String content = readFileContent(filePath);
            if (content != null && !content.isEmpty()) {
                if (!isFirstContent) {
                    result.append("."); // Append a dot before additional content
                }
                result.append(content);
                isFirstContent = false; // Mark that the first content has been processed
            }
        }

        // Print the final result, if any
        if (result.length() > 0) {
            System.out.println(result.toString());
        }
    }

    private static String readFileContent(String filePath) {
        try {
            // Read the file content and trim whitespace
            return Files.readString(Paths.get(filePath)).trim();
        } catch (IOException e) {
            // Handle missing file errors gracefully
            System.out.println("cat: " + filePath + ": No such file");
            return null; // Return null if the file cannot be read
        }
    }
}