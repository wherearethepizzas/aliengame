import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Ball extends Rectangle implements KeyListener{
    private int x, y, diameter;
    private Color color;
    private final int SPEED = 1;
    private int airSpeed, speedAfterCollsion;
    private boolean inAir, left, right, canJump;
    private int deltaX;
    private final int INITIALAIRSPEED = -14;
    private final double GRAVITATIONAL_C = 1;

    public Ball(int x, int y, int diameter, Color color) {
        super(x - diameter / 2, y - diameter / 2, diameter, diameter);
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
        g.setColor(Color.magenta);
        g.drawRect(x - diameter / 2, y - diameter / 2, diameter, diameter);
    }

    public void jump(ArrayList<Platform> platforms) {
        inAir = true;
        airSpeed = INITIALAIRSPEED;
    }

    public int getXCoord() {
        return x;
    }

    public int getYCoord() {
        return y;
    }
    // Updates the position of the Bounds
    public Rectangle getBounds() {
        return new Rectangle(x - diameter / 2, y - diameter / 2, diameter, diameter);
    }

    public boolean onPlatform(Platform p) {
        // If the center of the ball is within the platform's x coordinates contact is made
        if ((x) > p.getXCoord() & (x) < (p.getXCoord()+p.getW()) & (y+(diameter/2)) == p.getYCoord() ) {
            return true;
        } else {
            return false;
        }
    }

    public Platform getClosestPlatform(ArrayList<Platform> platforms) {
        int pointer = ((y + diameter) + GamePanel.HEADROOM) / (GamePanel.platformSpacing) - 1;
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
            deltaX = platform.getSpeed();
            canJump = true;
        }
        
        // if the ball is not moving get out of the method
        if(!inAir & !this.onPlatform(platform)) {
            return;
        }
        
        if (!inAir)
            if(!onPlatform(platform))
                inAir = true;

        if (inAir) {
            canJump = false;
            speedAfterCollsion = 1;
            if (canMoveHere(x, y + airSpeed, platform)) {
                y += airSpeed;
                airSpeed += GRAVITATIONAL_C;
            } else {
                //Banged onto the bottom of a platform
                if (airSpeed < 0) {
                    y = platform.getYCoord() + platform.getH() + (diameter / 2);
                }
                //Falling and landed on a platform
                if (airSpeed > 0 & this.onPlatform(platform)) {
                    y = platform.getYCoord() - (diameter / 2);
                    landed();
                } else {
                    //Keep falling
                    airSpeed = speedAfterCollsion;
                }
            }
        }

        if (canMoveHere(x + deltaX, y, getClosestPlatform(platforms))) {
            moveX(); 
        }
        
       
    }
    // Collision detection handling
    public boolean canMoveHere(int x, int y, Platform p) {
        // Creates a virtual hitbox
        Rectangle nextPlace = new Rectangle(x - this.diameter / 2, y - this.diameter / 2, this.diameter, this.diameter);
        
        if (x <= ((diameter / 2)) || x >= (GamePanel.WIDTH - (diameter / 2)))
            return false;
        if (y <= (diameter / 2) || y >= (GamePanel.HEIGHT - (diameter / 2)))
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
        int keyCode = e.getKeyCode();
        if ((keyCode == KeyEvent.VK_UP) & !inAir & this.onPlatform(getClosestPlatform(GamePanel.platforms))) {
            canJump = true;
            jump(GamePanel.platforms);
        }

        if (keyCode == KeyEvent.VK_LEFT & this.onPlatform(getClosestPlatform(GamePanel.platforms))) {
            left = true;
            deltaX = -SPEED;
        }
        
        if (keyCode == KeyEvent.VK_RIGHT & this.onPlatform(getClosestPlatform(GamePanel.platforms))) {
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

