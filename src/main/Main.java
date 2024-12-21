import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main{
    public static void main(String[] args) {
        // Example arguments; replace with actual arguments or integrate argument handling
        handleCat(List.of("/path/to/file1", "/path/to/file2", "/path/to/file3"));
    }

    private static void handleCat(List<String> arguments) {
        StringBuilder result = new StringBuilder();
        boolean isFirstContent = true; // Flag to track the first valid content

        for (String filePath : arguments) {
            try {
                // Read the file content and trim whitespace
                String content = Files.readString(Paths.get(filePath)).trim();

                // Skip appending if the content is empty
                if (!content.isEmpty()) {
                    if (!isFirstContent) {
                        result.append("."); // Append a dot before additional content
                    }
                    result.append(content);
                    isFirstContent = false; // Mark that the first content has been processed
                }
            } catch (IOException e) {
                // Handle missing file errors gracefully
                System.out.println("cat: " + filePath + ": No such file");
            }
        }

        // Print the final result, if any
        System.out.println(result.toString());
    }
}
