import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class main {
    public static void main(String[] args) {
        // Define the number of prank windows
        int windowCount = 1;

        // Schedule program exit after 10 seconds
        new Timer(100000, event -> System.exit(0)).start();

        // Create prank windows
        for (int i = 0; i < windowCount; i++) {
            PrankWindow prank = new PrankWindow();
            prank.start();
        }
    }
}

class PrankWindow extends JFrame {
    private Random random = new Random();
    JWindow redOverlay = new JWindow();

    public PrankWindow() {
        // Set basic properties for the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        redOverlay.setBackground(new Color(255, 0, 0, 100)); // Semi-transparent red tint
        redOverlay.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        redOverlay.setAlwaysOnTop(true);
        redOverlay.setVisible(true);

        // Add a window listener to generate new windows when this one is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                int newSpeed = 10; // Increase speed for new windows
                for (int i = 0; i < 2; i++) {
                    PrankWindow newPrank = new PrankWindow();
                    newPrank.start(newSpeed);
                }
            }
        });

        // Add a funny message
        JLabel label = new JLabel("WARNING MALWARE DETECTED!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label);

        // Add a key listener for emergency stop
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0); // Emergency stop on pressing ESC key
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // Randomize the window's starting position
        setLocation(random.nextInt(Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()),
                random.nextInt(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()));
        setAlwaysOnTop(true);
        setVisible(true);

        // Schedule automatic window closing for this window specifically
        new Timer(500 + random.nextInt(1500), e -> { // Randomize timer between 500ms and 2000ms
            PrankWindow newPrank = new PrankWindow();
            newPrank.start();
        }).start();
    }

    // Bounce the window around the screen
    public void start() {
        start(random.nextInt(10) + 1); // Random speed between 1 and 10
    }

    public void start(int speed) {
        new Thread(() -> {
            try {
                int dx = random.nextBoolean() ? speed : -speed; // Random horizontal direction
                int dy = random.nextBoolean() ? speed : -speed; // Random vertical direction
                int x = random.nextInt(900);
                int y = random.nextInt(600);
                while (true) {
                    x += dx;
                    y += dy;

                    // Check boundaries and reverse direction if needed
                    if (x <= 0 || x >= Toolkit.getDefaultToolkit().getScreenSize().width - getWidth())
                        dx = -dx;
                    if (y <= 0 || y >= Toolkit.getDefaultToolkit().getScreenSize().height - getHeight())
                        dy = -dy;

                    setLocation(x, y);

                    // Pause for a short duration
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}