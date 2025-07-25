import java.awt.*;

public class Player {
    private int x, y;
    private int width = 50;
    private int height = 40;
    private int speed = 5;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean hasBlaster = false;
    private long lastShotTime = 0;
    private long shotCooldown = 250; // milliseconds between shots
    
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void update() {
        if (movingLeft && x > 0) {
            x -= speed;
        }
        if (movingRight && x < 800 - width) {
            x += speed;
        }
    }
    
    public void draw(Graphics g) {
        // Draw spaceship body
        g.setColor(Color.CYAN);
        int[] xPoints = {x + width/2, x, x + width};
        int[] yPoints = {y, y + height, y + height};
        g.fillPolygon(xPoints, yPoints, 3);
        
        // Draw cockpit
        g.setColor(Color.BLUE);
        g.fillOval(x + width/2 - 8, y + height/2 - 5, 16, 10);
        
        // Draw engine flames when moving
        if (movingLeft || movingRight) {
            g.setColor(Color.ORANGE);
            g.fillRect(x + 10, y + height, 8, 10);
            g.fillRect(x + width - 18, y + height, 8, 10);
            g.setColor(Color.YELLOW);
            g.fillRect(x + 12, y + height + 5, 4, 5);
            g.fillRect(x + width - 16, y + height + 5, 4, 5);
        }
        
        // Draw blaster indicator if player has it
        if (hasBlaster) {
            g.setColor(Color.YELLOW);
            g.fillRect(x + width/2 - 2, y - 5, 4, 5);
            g.setColor(Color.RED);
            g.fillRect(x + width/2 - 1, y - 8, 2, 3);
        }
    }
    
    public void setMovingLeft(boolean moving) {
        this.movingLeft = moving;
    }
    
    public void setMovingRight(boolean moving) {
        this.movingRight = moving;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHasBlaster(boolean hasBlaster) {
        this.hasBlaster = hasBlaster;
    }
    
    public boolean hasBlaster() {
        return hasBlaster;
    }
    
    public boolean canShoot() {
        if (!hasBlaster) return false;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= shotCooldown) {
            lastShotTime = currentTime;
            return true;
        }
        return false;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}