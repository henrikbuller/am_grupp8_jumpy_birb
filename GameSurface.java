import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.nio.file.*;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

//import com.sun.tools.classfile.ConstantPool.UnexpectedEntry;

/**
 * A simple panel with a space invaders "game" in it. This is just to
 * demonstrate the bare minimum of stuff than can be done drawing on a panel.
 * This is by no means very good code.
 * 
 */

public class GameSurface extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 6260582674762246325L;

    private boolean gameOver;
    private Timer timer;
    private List<Rectangle> pipes;
    private Rectangle bird;
    private int yMotion;
    private final int width = 400;
    private final int height = 400;
    private int score = 0;
    private String playerName;
    private int scoreListCount = 0;
    Integer fakeScore = 1;

    List<HighScoreEntry> highScoreList = new ArrayList<>();

    public BufferedImage pip;

    public GameSurface() {
        this.gameOver = false;
        this.pipes = new ArrayList<>();

        addPipes(width, height);

        this.bird = new Rectangle(width / 3, width / 2, 40, 28);

        this.timer = new Timer(20, this);
        this.timer.start();
    }

    private void restart() {
        this.gameOver = false;
        this.pipes = new ArrayList<>();

        addPipes(width, height);

        this.bird = new Rectangle(width / 3, width / 2, 40, 28);

        this.timer = new Timer(20, this);
        this.timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // g.drawImage(go, 80, 100, null);
        try {
            repaint(g);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void addPipes(final int width, final int height) {
        int randomHeight = ThreadLocalRandom.current().nextInt(height / 4, height / 2);
        int gap = 150;

        // top pipe
        pipes.add(new Rectangle(width, 0, 50, (height - (randomHeight + gap))));
        // console log for bugfixes
        // System.out.println("top pipe:\t" + "x: " + width + "\ty: " + 0 + "\theight: "
        // + (height - (randomHeight + gap)));

        // bottom pipe
        pipes.add(new Rectangle(width, (height - randomHeight), 50, randomHeight));
        // console log for bugfixes
        // System.out.println("bottom pipe:\t" + "x:" + width + "\ty:" + (height -
        // randomHeight) + "\theight:" + randomHeight);

    }

    /**
     * Call this method when the graphics needs to be repainted on the graphics
     * surface.
     * 
     * @param g the graphics to paint on
     * @throws IOException
     */
    private void repaint(Graphics g) throws IOException {
        final Dimension d = this.getSize();

        if (gameOver) {

            if (isItHighscore(score)) {

                playerName = JOptionPane.showInputDialog(this, "Highscore! Write your name:");

                if (playerName == null) {
                    playerName = JOptionPane.showInputDialog(this, "You did a highscore, enter your name");
                } else if (playerName != null) {
                    if (playerName.length() > 8) {
                        playerName = JOptionPane.showInputDialog(this, "Write your name, max 8 characters");
                    }
                }

                HighScoreEntry player = new HighScoreEntry(playerName, score);
                highScoreList.add(player);
                
                Comparator<HighScoreEntry> comparator = Comparator.comparing(HighScoreEntry::getPlayerScore);
                Collections.sort(highScoreList, comparator.reversed());
                
                saveHighscores();

            }

            if (highScoreList.size() > 0) {
                readHighscoresFromFile(highscoreFile);
                addToListAgain();
            } 
            

            g.setColor(Color.white);
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Game Over!", d.height / 6, d.width / 6);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(toString(), d.height / 3, ((d.width / 4)));

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Highscores:", d.height / 5, ((d.width / 3)));

            scoreListCount = 0;
            score = 0;
            return;
        }

        // fill the background
        BufferedImage bg = ImageIO.read(Path.of("images/bluemoon.png").toFile());
        g.drawImage(bg, 0, 0, null);

        // draw the pipes
        pip = ImageIO.read(Path.of("images/redPipe.png").toFile());
        for (Rectangle pipe : pipes) {
            g.drawImage(pip, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // draw the bird
        BufferedImage img = ImageIO.read(Path.of("images/GBirdUp.png").toFile());
        g.drawImage(img, bird.x, bird.y, null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this will trigger on the timer event
        // if the game is not over yet it will
        // update the positions of all pipes
        // and check for collision with the space ship

        if (gameOver) {
            timer.stop();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return;
        }

        final List<Rectangle> toRemove = new ArrayList<>();

        for (Rectangle pipe : pipes) {
            pipe.translate(-1, 0);
            if (pipe.x + pipe.width < 0) {
                // we add to another list and remove later
                // to avoid concurrent modification in a for-each loop
                toRemove.add(pipe);
            }

            if (pipe.intersects(bird)) {
                gameOver = true;
            }
        }

        pipes.removeAll(toRemove);

        // add new pipes for every one that was removed
        for (int i = 0; i < toRemove.size(); ++i) {
            // Dimension d = getSize();
            addPipes(width, height);
        }

        this.repaint();

        bird.y -= yMotion;

        if (bird.y < 0 || bird.y > height) {
            gameOver = true;
        }

        // Awards one point if bird passes trough a set of pipes
        if (pipes.get(0).x == (width / 3 - bird.width) && !gameOver) {
            score++;
            System.out.println("Current score: " + score);
        }

    }

    public boolean isItHighscore(int score) {
        // highScores.put("lena", 0);

        HighScoreEntry placeholder = new HighScoreEntry("player", score);

        if (score == 0) {
            return false;
        }
        if (highScoreList.size() <= 10) {
            return true;
        }

        // Lägg in placeholder i listan
        // highScoreList.add(placeholder);

        // Ta bort lägsta värdet i Listan

        int indexOfLastElement = highScoreList.size() - 1;

        if (placeholder.getPlayerScore() > highScoreList.get(indexOfLastElement).getPlayerScore()) {
            highScoreList.remove(indexOfLastElement);
            return true;
        }

        return false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // this event triggers when we release a key and then
        // we will move the space ship if the game is not over yet

        final int maxHeight = this.getSize().height - bird.height - 10;
        final int kc = e.getKeyCode();

        if (!gameOver && kc == KeyEvent.VK_SPACE && bird.y < maxHeight) {
            jump();
        } else if (gameOver && kc == KeyEvent.VK_SPACE) {
            restart();
        }
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "You scored: " + score;
    }

    public void jump() {
        bird.translate(0, -50);
        if (yMotion < 0) {
            yMotion = 0;
        }
        yMotion -= 3;

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // do nothing
    }

    Path highscoreFile = FileSystems.getDefault().getPath("savefiles/saved_highscores.txt"); 
 
    public void saveHighscores() {
        try {
            writeHighscoresToFile(highScoreList, highscoreFile);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void printHighScores() {
        try {
            readHighscoresFromFile(highscoreFile);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        emptyHighscoreList();
    }

    private static void writeHighscoresToFile(List<HighScoreEntry> highScoreList, Path outputFile) throws IOException {
        try (var writer = Files.newBufferedWriter(outputFile)) {
            highScoreList.forEach((highScoreEntry) -> {
                try {
                    writer.append(String.valueOf(highScoreEntry));
                    writer.newLine();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }
    }

    static List<HighScoreEntry> placeholderList = new ArrayList<>();
    private static void readHighscoresFromFile(Path highscoreFile) throws IOException {
        try (var reader = Files.newBufferedReader(highscoreFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split(" ");
                HighScoreEntry playerEntry = new HighScoreEntry(arr[0], Integer.parseInt(arr[1]));
                placeholderList.add(playerEntry);
              
            }
        }

    }
    
    private void emptyHighscoreList() {
       highScoreList.clear();
        
    }
    
    private void addToListAgain() {
        placeholderList.forEach((highScoreEntry) -> {
            highScoreList.add(highScoreEntry);
        });
    }

}
