import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GamePanel extends JPanel implements KeyListener {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 650;
    public static final int GROUND_Y = 600;
    public static final int HEADROOM = 50;
    public static final int BALL_SIZE = 15;

    private List<Platform> platforms;
    private Ball ball;
    private boolean gameRunning = true;
    private int score = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        addKeyListener(this);
        setFocusable(true);
        initializePlatforms();
        initializeBall();
        startGameLoop();
    }

    private void initializePlatforms() {
        platforms = new ArrayList<>();
        int numPlatforms = 7; 
        int platformWidth = 60;
        int platformHeight = 7;
        int platformSpacing = (HEIGHT-(HEADROOM*2)-(platformHeight*(numPlatforms-2))) / (numPlatforms - 1);
        Random random = new Random();

        // Add static platforms at the start and end
        platforms.add(new Platform((WIDTH/2)- platformWidth/2, GROUND_Y - (2*platformHeight), platformWidth, platformHeight, Color.MAGENTA, true));
        platforms.add(new Platform((WIDTH/2)- platformWidth/2, HEADROOM + platformHeight, platformWidth, platformHeight, Color.GREEN, true));

        // Generate some unique speeds
        Set<Integer> speedsSet = new HashSet<>();
        while (speedsSet.size() < numPlatforms-2) {
            int r = random.nextInt(7) + 5;
            speedsSet.add(r);
        }

        // Add the speeds to an array
        ArrayList<Integer> speedsArr = new ArrayList<>();
        for (int speed : speedsSet) {
            speedsArr.add(speed);
        }

        // Add moving platforms
        for (int i = 1; i < numPlatforms - 1; i++) {
            int platformX = (WIDTH/2)- platformWidth/2;
            int platformY = (platformWidth) + (platformSpacing*i);
            Platform p = new Platform(platformX, platformY, platformWidth, platformHeight, Color.BLUE, false);
            boolean b = random.nextBoolean();
            if (b) {
                p.setSpeed(speedsArr.get(i-1));
            } else {
                p.setSpeed(-speedsArr.get(i-1));
            }

            platforms.add(p);
        }
    }

    private void initializeBall() {
        int ballSize = BALL_SIZE;
        double startX = (platforms.get(0).getXCoord() + platforms.get(0).getW() / 2.0);
        int startY = GROUND_Y - (2*platforms.get(0).getH()) - (ballSize/2);
        ball = new Ball((int)startX, startY, ballSize, Color.RED);
        System.out.println(ball.onPlatform(platforms.get(0)));
    }

    private void startGameLoop() {
        new Thread(() -> {
            while (gameRunning) {
                update();
                repaint();
                try {
                    Thread.sleep(20); // Adjust as needed for the game speed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void update() {
        // ball.fallG();
        System.out.println("Working");
        // Check for collisions with platforms
        for (Platform platform : platforms) {
            if (!platform.isStatic()) {
                platform.move();
                // Check for collisions with screen edges
                if (platform.getXCoord() < 0 || (platform.getXCoord() + platform.getW()) > WIDTH) {
                    platform.setSpeed(-platform.getSpeed()); 
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Platform platform : platforms) {
            platform.draw(g);
        }
        ball.draw(g);
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            ball.jump();
        } else if (keyCode == KeyEvent.VK_LEFT) {
            ball.moveLeft();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            ball.moveRight();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jumping Ball Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new GamePanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}