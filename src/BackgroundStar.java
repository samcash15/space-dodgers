import java.awt.*;
import java.util.Random;

public class BackgroundStar {
    private double x, y;
    private double speed;
    private int size;
    private int alpha;
    private Color color;
    
    public BackgroundStar(int screenWidth, int screenHeight) {
        Random random = new Random();
        this.x = random.nextDouble() * screenWidth;
        this.y = random.nextDouble() * screenHeight;
        this.speed = 0.2 + random.nextDouble() * 0.8; // Very slow movement
        this.size = 1 + random.nextInt(2); // 1 or 2 pixel size
        this.alpha = 80 + random.nextInt(120); // Subtle transparency (80-200)
        
        // Subtle white/blue tint
        int brightness = 200 + random.nextInt(56);
        this.color = new Color(brightness, brightness, Math.min(255, brightness + 30), alpha);
    }
    
    public void update(int screenHeight) {
        y += speed;
        
        // Reset star at top when it goes off bottom
        if (y > screenHeight) {
            y = -size;
            // Randomize position and properties when recycling
            Random random = new Random();
            this.speed = 0.2 + random.nextDouble() * 0.8;
            this.alpha = 80 + random.nextInt(120);
            int brightness = 200 + random.nextInt(56);
            this.color = new Color(brightness, brightness, Math.min(255, brightness + 30), alpha);
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        
        if (size == 1) {
            g2d.fillRect((int)x, (int)y, 1, 1);
        } else {
            // Add subtle glow for larger stars
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha / 3));
            g2d.fillRect((int)x - 1, (int)y - 1, 4, 4);
            g2d.setColor(color);
            g2d.fillRect((int)x, (int)y, 2, 2);
        }
    }
}