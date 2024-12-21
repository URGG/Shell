import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void Main(String[] args) {
        // Example arguments; replace with actual arguments or integrate argument handling
        List<String> filePaths = List.of("/path/to/file1", "/path/to/file2", "/path/to/file3");
        handleCat(filePaths);
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