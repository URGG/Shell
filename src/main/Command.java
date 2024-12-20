import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
public enum Command {
  PWD("pwd") {
    @Override
    public String getOutput(String[] commandParts) {
      return System.getProperty("user.dir");
    }
  },
  ECHO("echo") {
    @Override
    public String getOutput(String[] commandParts) {
      if (commandParts.length == 1)
        return "no arguments given!";
      return Arrays.stream(commandParts)
          .skip(1)
          .collect(Collectors.joining(" "));
    }
  },
  EXIT("exit") {
    @Override
    public String getOutput(String[] commandParts) {
      int exitCode;
      try {
        exitCode =
            commandParts.length > 1 ? Integer.parseInt(commandParts[1]) : 0;
      } catch (NumberFormatException e) {
        return "exit: " + commandParts[1] + ": numeric argument required";
      }
      System.exit(exitCode);
      return "";
    }
  },
  TYPE("type") {
    @Override
    public String getOutput(String[] commandParts) {
      if (commandParts.length == 1)
        return "type: no arguments given!";
      if (getCommand(commandParts[1]) != null)
        return String.format("%s is a shell builtin", commandParts[1]);
      Optional<String> absolutePath =
          FileSystemService.getAbsolutePath(commandParts[1]);
      return absolutePath
          .map(s -> String.format("%s is %s", commandParts[1], s))
          .orElseGet(() -> String.format("%s: not found", commandParts[1]));
    }
  };
  private final String command;
  Command(String command) { this.command = command; }
  public static Command getCommand(String command) {
    for (Command c : Command.values()) {
      if (c.command.equals(command)) {
        return c;
      }
    }
    return null;
  }
  public abstract String getOutput(String[] commandParts);
} 
