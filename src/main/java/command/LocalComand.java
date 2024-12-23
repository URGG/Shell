package command;

import java.io.IOException;

import static util.StringUtils.parseCommand;


public class LocalComand implements Strategy{
    String dir;

    public LocalComand(String dir) {
        this.dir = dir;
    }

    @Override
    public String command(String input) {
        try {
            String[] tokens = parseCommand(input);
            tokens[0] = dir;
            Process process = Runtime.getRuntime().exec(tokens);
            process.getInputStream().transferTo(System.out);
            //Files.copy(process.getInputStream(), System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
