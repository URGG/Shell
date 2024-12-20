import java.util.Scanner;
import java.util.Set;
public class Main {
  public static void main(String[] args) throws Exception {
    Set<String> commands = Set.of("echo", "exit", "type");
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("$ ");
      String input = scanner.nextLine();
      if (input.equals("exit 0")) {
        System.exit(0);
      } else if (input.startsWith("echo ")) {
        System.out.println(input.substring(5));
      } else if (input.startsWith("type ")) {
        String arg = input.substring(5);
        if (commands.contains(arg)) {
          System.out.printf("%s is a shell builtin%n", arg);
        } else {
          System.out.printf("%s: not found%n", arg);
        }
      } else {
        System.out.printf("%s: command not found%n", input);
      }
    }
  }
}