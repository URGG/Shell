
package Builtin;

import java.util.Arrays;

import static util.StringUtils.parseCommand;


public class Echo implements Strategy {
    @Override
    public String command(String input) {
        String[] tokens = parseCommand(input);
        return String.join(" ", Arrays.stream(tokens, 1, tokens.length).toArray(String[]::new))+'\n';
    }

    public static void main(String[] args) {
        Echo echo = new Echo();
        String input;
        String command;

        // > echo 'shell\\\ntest'
        // Expected: "shell\\\ntest"
        input="echo 'shell\\\\\\ntest'";
        command = echo.command(input);
        assert "\"shell\\\\\\ntest\"".equals(command);
        System.out.println(command);

        // > echo "mixed\"quote'hello'\\"
        // Expected: "mixed"quote'hello'\"
        input="echo \"mixed\\\"quote'hello'\\\\\"";
        command = echo.command(input);
        assert "\"mixed\"quote'hello'\\\"".equals(command);
        System.out.println(command);


        input="echo \"script'hello'\\\\n'example\"";
        command = echo.command(input);
        System.out.println(command);

        input="echo \"script'test'\\\\n'world\"";
        command = echo.command(input);
        System.out.println(command);
        input="echo \"script\\\"insidequotes\"test\\\"";
        command = echo.command(input);
        System.out.println(command);
    }
}

