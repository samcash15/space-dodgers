import java.awt.*;

public class PowerUp {
    private double x, y;
    private int size = 30;
    private double speed = 2;
    private PowerUpType type;
    private int pulseTimer = 0;
    
    public enum PowerUpType {
        BLASTER, MEDKIT
    }
    
    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    public void update() {
        y += speed;
        pulseTimer++;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Pulsing effect
        double scale = 1.0 + 0.1 * Math.sin(pulseTimer * 0.1);
        int drawSize = (int)(size * scale);
        int offset = (size - drawSize) / 2;
        
        // Draw outer glow based on type
        if (type == PowerUpType.BLASTER) {
            g2d.setColor(new Color(255, 255, 0, 100)); // Yellow glow for blaster
        } else if (type == PowerUpType.MEDKIT) {
            g2d.setColor(new Color(0, 255, 0, 100)); // Green glow for health
        }
        g2d.fillOval((int)x - 5 + offset, (int)y - 5 + offset, drawSize + 10, drawSize + 10);
        
        // Draw power-up based on type
        if (type == PowerUpType.BLASTER) {
            // Draw blaster icon (gun shape)
            g2d.setColor(Color.YELLOW);
            g2d.fillRoundRect((int)x + offset, (int)y + drawSize/3 + offset, 
                              drawSize * 3/4, drawSize/3, 5, 5);
            g2d.fillRect((int)x + drawSize * 3/4 + offset, (int)y + drawSize/4 + offset, 
                         drawSize/4, drawSize/2);
            
            // Draw details
            g2d.setColor(Color.ORANGE);
            g2d.fillRect((int)x + 2 + offset, (int)y + drawSize/3 + 2 + offset, 
                         drawSize/4, drawSize/3 - 4);
        } else if (type == PowerUpType.MEDKIT) {
            // Draw medkit cross
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int)x + drawSize/3 + offset, (int)y + drawSize/6 + offset, 
                         drawSize/3, drawSize * 2/3);
            g2d.fillRect((int)x + drawSize/6 + offset, (int)y + drawSize/3 + offset, 
                         drawSize * 2/3, drawSize/3);
            
            // Draw red cross on white background
            g2d.setColor(Color.RED);
            int crossThickness = drawSize/6;
            g2d.fillRect((int)x + drawSize/2 - crossThickness/2 + offset, 
                         (int)y + drawSize/4 + offset, 
                         crossThickness, drawSize/2);
            g2d.fillRect((int)x + drawSize/4 + offset, 
                         (int)y + drawSize/2 - crossThickness/2 + offset, 
                         drawSize/2, crossThickness);
        }
        
        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)x + offset, (int)y + offset, drawSize, drawSize);
    }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
    
    public double getY() {
        return y;
    }
    
    public PowerUpType getType() {
        return type;
    }
}