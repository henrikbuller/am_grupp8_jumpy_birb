import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreTest {

    private static List<HighScoreEntry> highScoreList = new ArrayList<>();

    HighScoreEntry player1 = new HighScoreEntry("Bosse", 3);
    HighScoreEntry player2 = new HighScoreEntry("Nisse", 2);
    HighScoreEntry player3 = new HighScoreEntry("Lena", 5);

    public void game() {
        highScoreList.add(player1);
        highScoreList.add(player2);
        highScoreList.add(player3);

    }

    public static void main(String[] args) {

    }

}
