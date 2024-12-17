import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class main {
    private static int windowCount = 10;
    static int totalWindows = 0;
    static int runtimeInSeconds = 10; // Default runtime in seconds
    static int baseSpeed = 5; // Default base speed of windows
    static int windowLimit = 15; // Default limit for the number of windows

    public static void main(String[] args) {
        // Display a warning dialog before starting the program
        int response = JOptionPane.showConfirmDialog(
                null,
                "This program will display multiple moving windows and may affect your system performance. Do you want to continue?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // Exit if the user chooses "No"
        if (response != JOptionPane.YES_OPTION) {
            System.exit(0);
        }

        // Display settings dialog to let the user modify program settings
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.setVisible(true);

        boolean autoOpen = settingsDialog.isAutoOpen();
        boolean duplicateOnClose = settingsDialog.isDuplicateOnClose();

        // Use the values from the dialog for configuring the program
        int initialWindowCount = settingsDialog.getWindowCount();

        // Schedule program exit after the user-defined runtime
        new Timer(main.runtimeInSeconds * 1000, event -> System.exit(0)).start();

        // Create prank windows
        for (int i = 0; i < initialWindowCount; i++) {
            PrankWindow prank = new PrankWindow(autoOpen, duplicateOnClose);
            prank.start();
            totalWindows++;
        }
    }
}

class PrankWindow extends JFrame {
    private final Random random = new Random();
    JWindow redOverlay = new JWindow();

    private static boolean windowOpen = false;
    private static boolean duplicateOnClose; // Updated to use setting from UI

    public PrankWindow(boolean autoOpen, boolean duplicateOnCloseSetting) {
        duplicateOnClose = duplicateOnCloseSetting;
        // Set basic properties for the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (autoOpen && !windowOpen) {
            Timer discoTimer = new Timer(100, e -> { // Change color every 100ms
                Color randomColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 100);
                redOverlay.setBackground(randomColor); // Random semi-transparent color
            });
            discoTimer.start();
            redOverlay.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                    Toolkit.getDefaultToolkit().getScreenSize().height);
            redOverlay.setAlwaysOnTop(true);
            redOverlay.setVisible(true);
            windowOpen = true;
        }

        // Add a window listener to generate new windows when this one is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                int newSpeed = 10; // Increase speed for new windows
                if (duplicateOnClose && main.totalWindows < main.windowLimit) {
                    for (int i = 0; i < 2 && main.totalWindows < main.windowLimit; i++) {
                        PrankWindow newPrank = new PrankWindow(false, duplicateOnClose);
                        newPrank.start(newSpeed);
                        main.totalWindows++;
                    }
                }
                main.totalWindows--;
            }
        });
    }

    public PrankWindow() {
        // Add a funny message
        JLabel label = new JLabel("WARNING MALWARE DETECTED!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        Timer labelDiscoTimer = new Timer(100, e -> {
            Color randomTextColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            Color randomBgColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            label.setForeground(randomTextColor);
            label.setBackground(randomBgColor);
            label.setOpaque(true); // Ensure background color is visible
        });
        labelDiscoTimer.start();
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
        setSize(400, 100);
        setBackground(Color.RED);
        setAlwaysOnTop(true);
        setVisible(true);

        // Schedule automatic window closing for this window specifically
        new Timer(500 + random.nextInt(1500), e -> { // Randomize timer between 500ms and 2000ms
            if (main.totalWindows < main.windowLimit) {
                PrankWindow newPrank = new PrankWindow(false, duplicateOnClose);
                newPrank.start();
                main.totalWindows++;
            } else {
                dispose();
                main.totalWindows--;
            }
        }).start();
    }

    // Bounce the window around the screen
    public void start() {
        start(main.baseSpeed + random.nextInt(5)); // Base speed with slight randomness
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

// A dialog for modifying program settings
class SettingsDialog extends JDialog {
    private int windowCount = 10; // Default value
    private final JTextField windowCountField;

    public SettingsDialog() {
        setTitle("Settings");
        setSize(300, 150);
        setModal(true);
        setLayout(new GridLayout(3, 2));

        // Add components for input
        add(new JLabel("Number of Windows: ", SwingConstants.RIGHT));
        windowCountField = new JTextField(String.valueOf(windowCount));
        add(windowCountField);

        // Add components for runtime and speed
        add(new JLabel("Program Runtime (seconds): ", SwingConstants.RIGHT));
        JTextField runtimeField = new JTextField("10");
        add(runtimeField);

        add(new JLabel("Base Speed of Windows: ", SwingConstants.RIGHT));
        JTextField speedField = new JTextField("5");
        add(speedField);

        // Add OK button to save settings
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            try {
                windowCount = Integer.parseInt(windowCountField.getText());
                int runtime = Integer.parseInt(runtimeField.getText());
                if (windowCount <= 0 || runtime <= 0) throw new NumberFormatException();
                main.runtimeInSeconds = runtime; // Pass runtime to main
                main.baseSpeed = Integer.parseInt(speedField.getText()); // Pass speed to main
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid positive integers", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(okButton);

        // Add Cancel button to exit without saving
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            windowCount = 0; // Exit the program if settings are canceled
            dispose();
        });
        add(cancelButton);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public int getWindowCount() {
        return windowCount;
    }

    public boolean isAutoOpen() {
        return false; // Default or placeholder value
    }

    public boolean isDuplicateOnClose() {
        return false; // Default or placeholder value
    }
}