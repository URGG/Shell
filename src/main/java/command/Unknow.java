package command;


public class Unknow implements Strategy{
    @Override
    public String command(String input) {
        return input + ": command not found\n";
    }
}
