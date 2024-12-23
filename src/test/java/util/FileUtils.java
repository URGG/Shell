package util;

import java.io.File;
import java.nio.file.Files;

public class FileUtils {


    public static String[] paths = System.getenv("PATH").split(File.pathSeparator);

    public static String pathInclued(String command) {
        for (String dir : paths) {
            File file = new File(dir, command);
            if (file.exists() && Files.isRegularFile(file.toPath())) {
                return file.getPath();
            }
        }
        return null;
    }
}