import javax.swing.JFrame;
import javax.swing.JLabel;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Color;
import java.io.IOException;

/**
 * This is a very small "game" just to show the absolute basics of
 * how to draw on a surface in a frame using Swing/AWT.
 * 
 */
public class App {
    public static void main(String[] args) throws IOException {
             
        JFrame main = new JFrame("Invaders");
        
        
        GameSurface gs = new GameSurface();
       
        main.setSize(400, 400);
        main.setResizable(true);
        main.add(gs);
        main.addKeyListener(gs);
        main.setDefaultCloseOperation(EXIT_ON_CLOSE);
        main.setVisible(true);
    }
}