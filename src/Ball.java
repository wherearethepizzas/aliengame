import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Ball extends Rectangle implements KeyListener{
    private int x, y, diameter;
    private Color color;
    private final int SPEED = 1;
    private int airSpeed, speedAfterCollsion;
    private boolean inAir, left, right, moving, canJump;
    private int deltaX, deltaY;
    private final int INITIALAIRSPEED = -15;
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
        //if the center of the ball is within the platform's x coordinates contact is made
        if ((x) >= p.getXCoord() & (x) <= (p.getXCoord()+p.getW()) & (y+(diameter/2)) == p.getYCoord() ) {
            return true;
        } else {
            return false;
        }
    }

    public Platform getClosestPlatform(ArrayList<Platform> platforms) {
        int pointer = ((y + diameter) + GamePanel.HEADROOM) / (GamePanel.platformSpacing) - 1;
        Platform closestPlatform;
        //Unelegant way of handling index out of Bounds for closest platform calculation
        try {
            closestPlatform = platforms.get(pointer);
        } catch (Exception IndexOutOfBoundsException) {
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
        moving = false;
        Platform platform = getClosestPlatform(platforms);

        if (this.onPlatform(platform)) {
            deltaX = platform.getSpeed();
            System.out.println(deltaX);
            moveX();
        }
        // if the ball is not moving get out of the method
        if(!inAir & !left & !right & !canJump) {
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

        if (canMoveHere(x + deltaX, y + deltaY, getClosestPlatform(platforms))) {
            moveX();
            y += deltaY; 
        }
        moving = true;
       
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

        if (keyCode == KeyEvent.VK_DOWN) {
            deltaY +=SPEED;
        }

        if (keyCode == KeyEvent.VK_LEFT) {
            left = true;
            deltaX = -SPEED;
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

        if (keyCode == KeyEvent.VK_DOWN) {
            deltaY = 0;
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

