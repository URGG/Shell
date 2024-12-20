import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class InputParser {
  public Command parseCommand(String input, PathEnv pathEnv) {
    
    List<String> argsList = new ArrayList<>();
    String commandString = "";
    int i = 0;
    StringBuilder sb = new StringBuilder();
    while (i < input.length()) {
      // remove preceding whitespace
      while (Character.isWhitespace(input.charAt(i))) {
        i++;
      }
    
      // get command
      if (commandString.isEmpty()) {
        while (i < input.length() && !Character.isWhitespace(input.charAt(i))) {
          sb.append(input.charAt(i));
          i++;
        }
        commandString = sb.toString();
      }
      // get single quote arg
      if (i < input.length() && input.charAt(i) == '\'') {
        i++;
        sb = new StringBuilder();
        while (input.charAt(i) != '\'') {
          sb.append(input.charAt(i));
          i++;
        }
        argsList.add(sb.toString());
      }
      // get unquoted arg
      if (i < input.length() && !Character.isWhitespace(input.charAt(i)) &&
          input.charAt(i) != '\'') {
        sb = new StringBuilder();
        while (i < input.length() && !Character.isWhitespace(input.charAt(i))) {
          sb.append(input.charAt(i));
          i++;
        }
        argsList.add(sb.toString());
      }
      i++;
    }
    //        String[] substrings = input.split(" ");
    //        String commandString = substrings[0];
    String[] args = argsList.toArray(new String[0]);
    Command command = switch (commandString) {
            case "exit" -> new ExitCommand(args);
            case "echo" -> new EchoCommand(args);
            case "type" -> new TypeCommand(args, pathEnv);
            case "pwd" -> new PrintWorkingDirectoryCommand();
            case "cd" -> new ChangeDirectoryCommand(args);
            default -> new UnknownCommand(commandString, args, pathEnv);
        };
        return command;
    }
}


