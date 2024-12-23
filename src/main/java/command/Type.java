package command;

import java.util.Set;

import util.FileUtils;
import static util.StringUtils.parseCommand;


public class Type implements Strategy {
    public static final Set<String> builtin = Set.of("echo", "exit", "type", "pwd", "cd");

    @Override
    public String command(String input) {
        String[] tokens = parseCommand(input);

        String command = tokens[1];
        if (builtin.contains(command)) {
            return command + " is a shell builtin\n";
        }
        String dir = FileUtils.pathInclued(command);
        if (dir != null) {
            return String.format("%s is %s\n", command, dir);
        } else {
            return command + ": not found\n";
        }
    }
}
