import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 650;
    public static final int FPS_SET = 120;
    public static final int GROUND_Y = 600;
    public static final int HEADROOM = 50;
    public static final int BALL_SIZE = 14;
    public static final int PLATFROM_WIDTH = 60;
    public static final int PLATFORM_HEIGHT = 6;
    public static int platformSpacing;
    private Thread gameThread;

    public static ArrayList<Platform> platforms;
    private Ball ball;
    private boolean gameRunning = true;
    private int score = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        initializePlatforms();
        initializeBall();
        addKeyListener(ball);
        startGameLoop();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long timePerFrame = FPS_SET * 1000000000;
        long lastT = System.nanoTime();
        while (gameRunning) {
            long currentT = System.nanoTime();
            //This ensures that we can control the frames per second of the game
            if (currentT - lastT > timePerFrame) {
                update();
                repaint();
                //A way to slow the game down (might delete later)
                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lastT = currentT;
        }
    }

    private void initializePlatforms() {
        platforms = new ArrayList<>();
        int numPlatforms = 7; 
        int platformWidth = PLATFROM_WIDTH;
        int platformHeight = PLATFORM_HEIGHT;
        platformSpacing = (HEIGHT-(HEADROOM*2)-(platformHeight*(numPlatforms-1))) / (numPlatforms - 1);
        Random random = new Random();

        // Add static platforms at the start and end
        platforms.add(new Platform((WIDTH/2)- platformWidth/2, GROUND_Y, platformWidth, platformHeight, Color.MAGENTA, true));
        platforms.add(new Platform((WIDTH/2)- platformWidth/2, HEADROOM, platformWidth, platformHeight, Color.GREEN, true));

        // Generate some speeds
        int range = 4;
        ArrayList<Integer> speedsArr = new ArrayList<>();

        int num = random.nextInt(range);
        speedsArr.add(num);

        //Ensures no contiguous platforms have the same speed
        for (int i = 1; i < numPlatforms; i++) {
            do {
            num = random.nextInt(range) + 1;
            } while (i > 0 & num == speedsArr.get(i - 1)); 
            
            speedsArr.add(num);
            
        }

        // Add moving platforms
        for (int i = 1; i < numPlatforms - 1; i++) {
            int platformX = (WIDTH/2)- platformWidth/2;
            int platformY = (HEADROOM + (platformHeight*i)) + (platformSpacing*i);
            Platform p = new Platform(platformX, platformY, platformWidth, platformHeight, Color.BLUE, false);
            boolean b = random.nextBoolean();
            if (b) {
                p.setSpeed(speedsArr.get(i-1));
            } else {
                p.setSpeed(-speedsArr.get(i-1));
            }

            platforms.add(p);
        }

        // Sort platforms by height in ascending order for collision detection
        platforms.sort(new Comparator<Platform>() {

            @Override
            public int compare(Platform o1, Platform o2) {
                return (o1.getYCoord() - o2.getYCoord());
            }
            
        });
    }

    private void initializeBall() {
        int ballSize = BALL_SIZE;
        double startX = (platforms.get(0).getXCoord() + platforms.get(0).getW() / 2.0);
        int startY = GROUND_Y - (ballSize/2);
        ball = new Ball((int)startX, startY, ballSize, Color.RED);
        // System.out.println(ball.onPlatform(platforms.get(0)));
    }

    private void update() {
        // Check for collisions with platforms
        for (Platform platform : platforms) {
            if (!platform.isStatic()) {
                platform.move();
                // Check for collisions with screen edges
                if (platform.getXCoord() < 0 || (platform.getXCoord() + platform.getW()) > WIDTH) {
                    platform.setSpeed(-platform.getSpeed()); 
                }
                // Check for collision with ball
            }
        }
        ball.updatePosition(platforms);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Platform platform : platforms) {
            platform.draw(g);
            g.drawLine(0, platform.getYCoord(), WIDTH, platform.getYCoord());
        }
        ball.draw(g);
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
        g.drawLine(0,600,WIDTH,600);
        g.drawLine(0,HEADROOM,WIDTH,HEADROOM);
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