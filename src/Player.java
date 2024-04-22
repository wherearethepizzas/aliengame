import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player extends Rectangle implements KeyListener{
    private int x, y;
    private final int SPEED = 5;
    private int airSpeed, speedAfterCollsion;
    private boolean inAir, left, right, canJump;
    private int deltaX;
    private final int INITIALAIRSPEED = -14;
    private final double GRAVITATIONAL_C = 1;
    private BufferedImage image;
    public static int height = 35;
    public static int width = 25;
    private static int levitationRoom = 1;

    public Player(int x, int y) {
        super(x, y, width, height - levitationRoom);
        this.x = x;
        this.y = y;
        fetchImage();
    }

    private void fetchImage() {
        File file = new File("lib/alien.png");
        try {
            image =  ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.drawRect(x,y,width,height - levitationRoom);
        g.drawImage(image, x, y, width,height + levitationRoom, null);
        
    }

    public void jump(ArrayList<Platform> platforms) {
        inAir = true;
        airSpeed = INITIALAIRSPEED;
    }

    // Updates the position of the Bounds
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height - levitationRoom);
    }

    public boolean onPlatform(Platform p) {
        // If the center of the Player is within the platform's x coordinates contact is made
        if ((this.getBounds().getCenterX()) > p.getBounds().x & (this.getBounds().getCenterX()) < (p.getBounds().getMaxX()) & (this.getBounds().getMaxY()) == p.getBounds().y ) {
            return true;
        } else {
            setInAir(true);
            return false;
        }
    }

    public Platform getClosestPlatform(ArrayList<Platform> platforms) {
        int pointer = ((y + height/2) + GamePanel.HEADROOM) / (GamePanel.platformSpacing) - 1;
        System.out.println(pointer);
        Platform closestPlatform;
        // Unelegant way of handling index out of Bounds for closest platform calculation
        try {
            closestPlatform = platforms.get(pointer);
        } catch (Exception IndexOutOfBoundsException) {
            // Upper part of the screen
            if (y < 300) {
                pointer = 0;
            } else {
                pointer = 6;
            }
            closestPlatform = platforms.get(pointer);
        }
        return closestPlatform;
    }

    public void updatePosition(ArrayList<Platform> platforms) {
        
        Platform platform = getClosestPlatform(platforms);
        
        if (this.onPlatform(platform)) {
            deltaX += platform.getSpeed();
            canJump = true;
        }  
        
        if (!inAir){
            if(!onPlatform(platform)) 
                inAir = true;
        }

        if (inAir) {
            canJump = false;
            speedAfterCollsion = 1;
            if (canMoveHere(x, y + airSpeed, platform)) {
                y += airSpeed;
                airSpeed += GRAVITATIONAL_C;
            } else {
                //On the edge of the platform
                if (!this.onPlatform(platform) & (this.getBounds().getMaxY() == platform.getBounds().y)) {
                    //On the right edge of platform
                    if ((this.getBounds().getCenterX()) >= platform.getMaxX()) {
                        deltaX += width / 2;
                    } else
                    //On the left edge of platform
                    if ((this.getBounds().getCenterX()) <= platform.getMinX()) {
                        deltaX -= width / 2;
                    }
                } 
                //Banged onto the bottom of a platform
                if (airSpeed < 0) {
                    y = platform.getYCoord() + platform.getH() + 1;
                }
                //Falling and landed on a platform
                if (airSpeed > 0 & this.onPlatform(platform)) {
                    y = platform.getYCoord() - (this.getBounds().height);
                    landed();
                } else {
                    //Keep falling
                    airSpeed = speedAfterCollsion;
                }
            }
        }

        if ((right || left || canJump) & canMoveHere(x + deltaX, y, getClosestPlatform(platforms))) {
            moveX(); 
        }

        // System.out.println("x: " + x + " | platform: " + platform.getBounds().x + ", " + platform.getBounds().getMaxX());
        
        deltaX = 0;
        
       
    }
    // Collision detection handling
    public boolean canMoveHere(int x, int y, Platform p) {
        // Creates a virtual hitbox
        Rectangle nextPlace = new Rectangle(x, y, width, height - levitationRoom);
        
        if (this.getBounds().x <= 0 || this.getBounds().x >= (GamePanel.WIDTH - (Player.width)))
            return false;
        if (this.getBounds().y <= (height) || this.getBounds().y >= (GamePanel.HEIGHT))
            return false;
        return !nextPlace.intersects(p.getBounds());
    }

    public void moveX() {
        x += deltaX;

    }

    public void setDeltaX(int d) {
        deltaX = d;
    }

    public void setInAir(boolean b) {
        inAir = b;
    }

    public void landed() {
        inAir = false;
        airSpeed = 0;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Platform platform = getClosestPlatform(GamePanel.platforms);
        int keyCode = e.getKeyCode();
        if ((keyCode == KeyEvent.VK_UP) & !inAir & this.onPlatform(platform)) {
            canJump = true;
            jump(GamePanel.platforms);
        }

        if (keyCode == KeyEvent.VK_LEFT) {
            left = true;
            deltaX = -(SPEED);
        }
        
        if (keyCode == KeyEvent.VK_RIGHT) {
            right = true;
            deltaX = SPEED;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if ((keyCode == KeyEvent.VK_UP)) {
            canJump = false;
        }

        if (keyCode == KeyEvent.VK_LEFT) {
            left = false;
            deltaX = 0;
        }
        
        if (keyCode == KeyEvent.VK_RIGHT) {
            right = false;
            deltaX = 0;
        }
    }

}


