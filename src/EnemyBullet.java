import java.awt.*;
import java.awt.image.BufferedImage;

public class EnemyBullet {
    private double x, y;
    private int width = 6;
    private int height = 16;
    private double speed = 4;
    private String laserType;
    private BufferedImage sprite;
    
    public EnemyBullet(int x, int y, String laserType) {
        this.x = x;
        this.y = y;
        this.laserType = laserType;
        this.sprite = SpriteLoader.getBullet(laserType);
        
        if (sprite != null) {
            this.width = sprite.getWidth() * 1;
            this.height = sprite.getHeight() * 1;
        }
    }
    
    public void update() {
        y += speed; // Move downward toward player
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering hints for crisp pixel art
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        if (sprite != null) {
            g2d.drawImage(sprite, (int)x, (int)y, width, height, null);
        } else {
            // Fallback colored rectangle based on laser type
            Color fallbackColor = getFallbackColor();
            g2d.setColor(fallbackColor);
            g2d.fillRect((int)x, (int)y, width, height);
            
            // Add bright tip
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int)x + 1, (int)y, width - 2, 3);
        }
    }
    
    private Color getFallbackColor() {
        switch (laserType) {
            case "pixel_laser_blue": return Color.CYAN;
            case "pixel_laser_green": return Color.GREEN;
            case "pixel_laser_red": return Color.RED;
            case "pixel_laser_yellow": return Color.YELLOW;
            default: return Color.MAGENTA;
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public double getY() {
        return y;
    }
    
    public int getX() {
        return (int)x;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}