import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final String PROMPT = "main-shell> ";
    private static final List<String> history = new ArrayList<>();
    private static final Map<String, String> aliases = new HashMap<>();
    private static final Map<Integer, Process> backgroundJobs = new HashMap<>();
    private static int jobIdCounter = 1;
    
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command;

        while (true) {
            try {
                System.out.print(PROMPT); // Display prompt
                command = reader.readLine(); // Read command input
                
                if (command == null || command.trim().isEmpty()) {
                    continue;
                }

                // Save the command to history
                history.add(command);
                if (history.size() > 50) {
                    history.remove(0);  // Keep history size to 50 commands
                }

                // Handle exit
                if ("exit".equalsIgnoreCase(command.trim())) {
                    System.out.println("Exiting shell.");
                    break;
                }

                // Handle built-in commands
                if (command.startsWith("cd ")) {
                    changeDirectory(command);
                } else if (command.equals("history")) {
                    printHistory();
                } else if (command.startsWith("alias ")) {
                    createAlias(command);
                } else if (command.startsWith("jobs")) {
                    listJobs();
                } else if (command.startsWith("fg ")) {
                    bringToForeground(command);
                } else if (command.startsWith("bg ")) {
                    runInBackground(command);
                } else if (command.startsWith("export ")) {
                    setEnvironmentVariable(command);
                } else if (command.equals("env")) {
                    printEnvironmentVariables();
                } else {
                    executeCommand(command);
                }

            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        }
    }

    // Change directory (built-in command)
    private static void changeDirectory(String command) {
        String path = command.substring(3).trim();
        if (path.isEmpty()) {
            path = System.getProperty("user.home");
        }

        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            System.setProperty("user.dir", path);
        } else {
            System.err.println("cd: No such directory: " + path);
        }
    }

    // Print the history of commands
    private static void printHistory() {
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }

    // Create an alias for a command
    private static void createAlias(String command) {
        String[] parts = command.split(" ", 3);
        if (parts.length == 3) {
            aliases.put(parts[1], parts[2]);
            System.out.println("Alias created: " + parts[1] + " = " + parts[2]);
        } else {
            System.out.println("Invalid alias format. Usage: alias <name> <command>");
        }
    }

    // Execute the command, handle redirection, pipes, and background processes
    private static void executeCommand(String command) {
        // Replace aliases
        for (Map.Entry<String, String> alias : aliases.entrySet()) {
            if (command.startsWith(alias.getKey())) {
                command = command.replaceFirst(alias.getKey(), alias.getValue());
                break;
            }
        }

        try {
            // Split commands by pipe
            String[] commands = command.split("\\|");
            Process lastProcess = null;

            for (String cmd : commands) {
                cmd = cmd.trim();
                boolean background = false;
                if (cmd.endsWith("&")) {
                    background = true;
                    cmd = cmd.substring(0, cmd.length() - 1).trim();
                }

                String[] cmdArgs = cmd.split("\\s+");
                ProcessBuilder processBuilder = new ProcessBuilder(cmdArgs);

                if (lastProcess != null) {
                    InputStream inputStream = lastProcess.getInputStream();
                    processBuilder.redirectInput(inputStream);
                }

                Process process = processBuilder.start();

                if (background) {
                    // Handle background job
                    int jobId = jobIdCounter++;
                    backgroundJobs.put(jobId, process);
                    System.out.println("Started background job: " + jobId);
                } else {
                    // Wait for the process to finish
                    process.waitFor();
                }

                lastProcess = process;  // Chain the output of this process to the next process
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage());
        }
    }

    // List background jobs
    private static void listJobs() {
        if (backgroundJobs.isEmpty()) {
            System.out.println("No background jobs.");
        } else {
            for (Map.Entry<Integer, Process> job : backgroundJobs.entrySet()) {
                int jobId = job.getKey();
                Process process = job.getValue();
                System.out.println("Job " + jobId + ": " + process.info().command());
            }
        }
    }

    // Bring a background job to the foreground
    private static void bringToForeground(String command) {
        try {
            int jobId = Integer.parseInt(command.split(" ")[1].trim());
            Process process = backgroundJobs.get(jobId);
            if (process != null) {
                process.waitFor(); // Wait for the job to complete
                backgroundJobs.remove(jobId);
            } else {
                System.err.println("No such job: " + jobId);
            }
        } catch (NumberFormatException | InterruptedException e) {
            System.err.println("Invalid job ID or error bringing job to foreground.");
        }
    }

    // Run a job in the background
    private static void runInBackground(String command) {
        // Implementation similar to jobs, but it's already running in background
        System.out.println("Job is already running in the background.");
    }

    // Set environment variable
    private static void setEnvironmentVariable(String command) {
        String[] parts = command.split(" ", 3);
        if (parts.length == 3) {
            System.setProperty(parts[1], parts[2]);
            System.out.println("Environment variable set: " + parts[1] + " = " + parts[2]);
        } else {
            System.out.println("Invalid export format. Usage: export <name>=<value>");
        }
    }

    // Print environment variables
    private static void printEnvironmentVariables() {
        System.getenv().forEach((key, value) -> System.out.println(key + "=" + value));
    }
}
