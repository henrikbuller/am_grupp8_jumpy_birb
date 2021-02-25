import java.util.Comparator;

public class HighScoreEntry implements Comparable<HighScoreEntry> {
    private String playerName;
    private int playerScore;

    public HighScoreEntry(String playerName, int playerScore) {
        super();
        this.playerName = playerName;
        this.playerScore = playerScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

@Override
public int compareTo(HighScoreEntry o) {
   var comp = Comparator.comparing(HighScoreEntry::getPlayerScore);
    return comp.compare(this, o);
}

@Override
public String toString() {
    return playerName + " " + playerScore;
}



}