package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {
    public static final char SPACE = ' ';
    public static final char SINGLE = '\'';
    public static final char DOUBLE = '"';
    public static final char BACKSLASH = '\\';

    public static String[] parseCommand(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (SINGLE == character) {
                i = processSingleQuote(SINGLE, input, i, sb);
                continue;
            }
            if (DOUBLE == character) {
                i = processDubleQuote(DOUBLE, input, i, sb);
                continue;
            }
            if (SPACE != character) {
                if (BACKSLASH == character) {
                    sb.append(input.charAt(++i));
                } else {
                    sb.append(character);
                }
            } else if (!sb.isEmpty()) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        if (!sb.isEmpty()) {
            tokens.add(sb.toString());
        }
        return tokens.toArray(tokens.toArray(new String[0]));
    }
    private static int processSingleQuote(char quote, String input, int i, StringBuilder sb) {
        char character;
        while (quote != (character = input.charAt(++i))) {
            if (BACKSLASH == character) {
                char nextChar = input.charAt(++i);
                if (quote == nextChar) {
                    sb.append(nextChar);
                } else {
                    sb.append(character);
                    sb.append(nextChar);
                }
            } else {
                sb.append(character);
            }
        }
        return i;
    }

    private static int processDubleQuote(char quote, String input, int i, StringBuilder sb) {
        char character;
        while (quote != (character = input.charAt(++i))) {
            if (BACKSLASH == character) {
                char nextChar = input.charAt(++i);
                if (quote == nextChar) {
                    sb.append(nextChar);
                } else if (BACKSLASH == nextChar) {
                    sb.append(nextChar);
                } else {
                    sb.append(character);
                    sb.append(nextChar);
                }
            } else {
                sb.append(character);
            }
        }
        return i;
    }

    public static void main(String[] args) {
        String input = "echo world     script";
        String[] strings = parseCommand(input);
        System.out.println(Arrays.asList(strings));
    }
}