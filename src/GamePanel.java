import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 650;
    public static final int FPS_SET = 120;
    public static final int UPS_SET = 30;
    public static final int GROUND_Y = 600;
    public static final int HEADROOM = 90;
    public static final int BALL_SIZE = 14;
    public static final int PLATFROM_WIDTH = 130;
    public static final int PLATFORM_HEIGHT = 6;
    public static ArrayList<Platform> platforms;
    public static int platformSpacing;
    private Thread gameThread;
    private Player player;
    private Image backImage;
    private Clip bgClip, hitClip, landClip, winClip, loseClip;
    private File file;
    private boolean gameRunning = true;
    private boolean win = false;
    public static int score = 0;
    

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        initializePlatforms();
        initializePlayer();
        fetchImage();
        addKeyListener(player);
        startGameLoop();
    }

    private void initializePlayer() {
        player = new Player((WIDTH / 2) - (Player.width / 2), GROUND_Y - Player.height);
        player.setInAir(true);
    }

    private void fetchImage() {
        try {
            backImage = ImageIO.read(new File("lib/space.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Clip fetchAudio(Clip clip, String s) {

        switch (s) {
            case "b":
                file = new File("lib/bg_music.wav");
                break;
            case "w":
                file = new File("lib/win.wav");
                break;
            case "d":
                file = new File("lib/game_over.wav");
                break;
            case "l":
                file = new File("lib/land.wav");
                break;
            case "h":
                file = new File("lib/hit.wav");
                break;
            default:
                break;
        }
    
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clip;
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        bgClip = fetchAudio(bgClip, "b");
        bgClip.setMicrosecondPosition(1000);   
        
        long timePerFrame = 1000000000 / FPS_SET;
        long timerPerUpdate = 1000000000 / UPS_SET;
        long prevUpdate = System.nanoTime();
        long prevFrame = System.nanoTime();
        
        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();
        
        double deltaU = 0;
        double deltaF = 0;
        
        bgClip.start();
        
        while (gameRunning) {
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
            long currentT = System.nanoTime();

            deltaU += (currentT - prevUpdate) / timerPerUpdate;
            deltaF += (currentT - prevFrame) / timePerFrame;

            
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
                prevUpdate = currentT;
            }

            if (deltaF >= 1) {
                repaint();
                frames++;
                deltaF--;
                prevFrame = currentT;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    private void initializePlatforms() {
        platforms = new ArrayList<>();
        int numPlatforms = 7; 
        int platformWidth = PLATFROM_WIDTH;
        int platformHeight = PLATFORM_HEIGHT;
        platformSpacing = (GROUND_Y - HEADROOM -(platformHeight*(numPlatforms-1))) / (numPlatforms - 1);
        Random random = new Random();

        // Add platforms at the start and end
        platforms.add(new Platform((WIDTH/2)- platformWidth/2, GROUND_Y, platformWidth, platformHeight, Color.MAGENTA, true));
        platforms.add(new Platform((WIDTH/2)- platformWidth/2, HEADROOM, platformWidth, platformHeight, Color.GREEN, false));
        platforms.get(0).setVisited(true);
        platforms.get(1).setLastP(true);
        platforms.get(1).setSpeed(2);

        // Generate some speeds
        int range = 7;
        ArrayList<Integer> speedsArr = new ArrayList<>();
        int num, prevNum;
        num = 0; // initial value

        //Ensures no contiguous platforms have the same speed
        for (int i = 0; i < numPlatforms - 2; i++) {
            prevNum = num;
            do {
                num = random.nextInt(range) + 3;
            } while ((Math.abs(num - prevNum)) < 2 || num > 7); 

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
       
        // Sort platforms by height in ascending order for collision detection (Highest platform is index 0)
        platforms.sort(new Comparator<Platform>() {

            @Override
            public int compare(Platform o1, Platform o2) {
                return (o1.getYCoord() - o2.getYCoord());
            }
            
        });
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
                // Check for collision with player
                if (player.getClosestPlatform(platforms).getBounds().intersects(player.getBounds())) {
                    player.setDeltaX(platform.getSpeed());
                    hitClip = fetchAudio(hitClip, "h");
                    hitClip.setMicrosecondPosition(900);
                    hitClip.start();
                    player.moveX();
                }
            }
            if (win) {
                platform.setSpeed(0);
            }
        }
        
        player.updatePosition(platforms);
        
        if (player.getClosestPlatform(platforms).getVisited() == false) {
            if (player.onPlatform(player.getClosestPlatform(platforms))) {
                player.getClosestPlatform(platforms).setVisited(true);
                score += 10;
                landClip = fetchAudio(landClip, "l");
                landClip.start();
                // Bonus points for making it to the end
                if (player.getClosestPlatform(platforms).isLastPlatform()) {
                    score += 20;
                    winClip = fetchAudio(winClip, "w");
                    winClip.start();
                    win = true;
                }
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backImage, 0, 0, null);
        for (Platform platform : platforms) {
            platform.draw(g);
        }
        player.draw(g);
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        
        if (win) {
            new WinPanel().draw(g);
            gameRunning = false;
            bgClip.stop();
        } else if (player.getBounds().getY() > GROUND_Y) {
            new LosePanel().draw(g);
            gameRunning = false;
            bgClip.stop();
            loseClip = fetchAudio(loseClip, "d");
            loseClip.start();
        }
        
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jumping Ball Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new GamePanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

}