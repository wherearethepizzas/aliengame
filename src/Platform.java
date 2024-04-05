import java.awt.*;

public class Platform extends Rectangle{
    private int x, y, width, height, speed;
    private Color color;
    private boolean isStatic;

    public Platform(int x, int y, int width, int height, Color color, boolean isStatic) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.isStatic = isStatic;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getXCoord() {
        return x;
    }

    public int getYCoord() {
        return y;
    }

    public int getW() {
        return width;
    }

    public int getH() {
        return height;
    }

    public void setSpeed(int s) {
        speed = s;
    }

    public int getSpeed() {
        return speed;
    }

    public void move() {
        x+=speed;
    }
        
}

