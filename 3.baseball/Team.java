import java.util.Arrays;

public class Team {
    private final String name;
    private final int wins;
    private final int losses;
    private final int remaining;
    private final int[] remainingAgainst;

    public Team(String name, int wins, int losses, int remaining, int[] remainingAgainst) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.remaining = remaining;
        this.remainingAgainst = Arrays.copyOf(remainingAgainst, remainingAgainst.length);
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getRemaining() {
        return remaining;
    }

    public int[] getRemainingAgainst() {
        return Arrays.copyOf(remainingAgainst, remainingAgainst.length);
    }
}
