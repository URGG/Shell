import Builtin.Strategy;


public class Processer {
    private Strategy strategy;

    public Processer() {
    }

    public Processer(Strategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    String processCommand(String commands){
        return strategy.command(commands);
    }
}
