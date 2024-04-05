import java.awt.*;
import java.awt.event.KeyEvent;

public class Ball {
    private int x, y, diameter;
    private Color color;
    private int velocityY = 0;
    private final int X_SPEED = 5;
    private static final int GRAVITATIONAL_C = 10;

    public Ball(int x, int y, int diameter, Color color) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
    }

    public void jump() {
        velocityY = 10;
    }

    public void fallG(double t) {
        y = (int)(velocityY*t) - (int)(0.5*GRAVITATIONAL_C*t*t); // Newton's law of motion
    }

    public void moveLeft() {
        x -= X_SPEED; 
    }

    public void moveRight() {
        x += X_SPEED; 
    }

    public int getXCoord() {
        return x;
    }

    public int getYCoord() {
        return y;
    }

    public boolean onPlatform(Platform p) {
        if ((x+(diameter/2)) >= p.getXCoord() & (x+(diameter/2)) <= (p.getXCoord()+p.getW()) & (y+(diameter/2)) == p.getYCoord() ) {
            return true;
        } else {
            return false;
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            jump();
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft();
        }
    }

}

