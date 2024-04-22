import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LosePanel {
    
    private BufferedImage image;

    public LosePanel() {
        fetchImage();
    }

    private void fetchImage() {
        try {
            File file = new File("lib/lose.jpg");
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        g.drawImage(image, 100, 200, 400, 200,null);
    }
}
