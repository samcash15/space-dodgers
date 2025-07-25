import java.awt.*;

public class Bullet {
    private double x, y;
    private int width = 4;
    private int height = 15;
    private double speed = 10;
    private Color color = Color.RED;
    private int trailLength = 5;
    
    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void update() {
        y -= speed;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw trail effect
        for (int i = 0; i < trailLength; i++) {
            int alpha = 255 - (i * 40);
            if (alpha < 0) alpha = 0;
            g2d.setColor(new Color(255, 200, 0, alpha));
            g2d.fillRect((int)x, (int)y + (i * 3), width, height - (i * 2));
        }
        
        // Draw main bullet
        g2d.setColor(color);
        g2d.fillRect((int)x, (int)y, width, height);
        
        // Draw bright tip
        g2d.setColor(Color.WHITE);
        g2d.fillRect((int)x + 1, (int)y, width - 2, 3);
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public double getY() {
        return y;
    }
}