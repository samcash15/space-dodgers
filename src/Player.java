import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    private int x, y;
    private int width = 50;
    private int height = 40;
    private int speed = 8;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean hasBlaster = false;
    private long lastShotTime = 0;
    private long shotCooldown = 250; // milliseconds between shots
    private String shipType;
    private BufferedImage shipSprite;
    private int thrusterFrame = 0;
    private int maxHealth = 100;
    private int currentHealth = 100;
    private boolean hit = false;
    private int hitTimer = 0;
    
    public Player(int x, int y, String shipType) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.shipSprite = SpriteLoader.getPlayerShip(shipType);
        
        if (shipSprite != null) {
            this.width = (int)(shipSprite.getWidth() * 1.5); // Smaller scale for better gameplay
            this.height = (int)(shipSprite.getHeight() * 1.5);
        }
    }
    
    public void update() {
        if (movingLeft && x > 0) {
            x -= speed;
        }
        if (movingRight && x < 1200 - width) {
            x += speed;
        }
        
        // Update thruster animation when moving
        if (movingLeft || movingRight) {
            thrusterFrame = (thrusterFrame + 1) % 5; // Cycle through 5 thruster frames
        }
        
        // Handle hit flash effect
        if (hit) {
            hitTimer--;
            if (hitTimer <= 0) {
                hit = false;
            }
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering hints for crisp pixel art
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        // Draw thruster flames when moving (behind ship)
        if (movingLeft || movingRight) {
            BufferedImage thrusterSprite = SpriteLoader.getThruster("thruster-" + (thrusterFrame + 1));
            if (thrusterSprite != null) {
                int thrusterScale = 2;
                int thrusterWidth = thrusterSprite.getWidth() * thrusterScale;
                int thrusterHeight = thrusterSprite.getHeight() * thrusterScale;
                
                // Draw thrusters at the back of the ship
                g2d.drawImage(thrusterSprite, 
                            x + width/4 - thrusterWidth/2, 
                            y + height - 5,
                            thrusterWidth, thrusterHeight, null);
                g2d.drawImage(thrusterSprite, 
                            x + 3*width/4 - thrusterWidth/2, 
                            y + height - 5,
                            thrusterWidth, thrusterHeight, null);
            }
        }
        
        // Draw ship sprite with hit flash effect
        if (shipSprite != null) {
            // Flash red when hit
            if (hit) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2d.setColor(Color.RED);
                g2d.fillRect(x, y, width, height);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            
            g2d.drawImage(shipSprite, x, y, width, height, null);
        } else {
            // Fallback to original drawing if sprite fails
            g2d.setColor(hit ? Color.RED : Color.CYAN);
            int[] xPoints = {x + width/2, x, x + width};
            int[] yPoints = {y, y + height, y + height};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        
        // Draw blaster indicator if player has it
        if (hasBlaster) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x + width/2 - 4, y - 8, 8, 8);
            g2d.setColor(Color.RED);
            g2d.fillOval(x + width/2 - 2, y - 6, 4, 4);
        }
        
        // Draw health bar
        drawHealthBar(g2d);
    }
    
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = width;
        int barHeight = 6;
        int barX = x;
        int barY = y + height + 15; // Below where thrusters extend
        
        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Health bar - color changes based on health percentage
        double healthPercent = (double) currentHealth / maxHealth;
        Color healthColor = Color.GREEN;
        if (healthPercent <= 0.25) {
            healthColor = Color.RED;
        } else if (healthPercent <= 0.5) {
            healthColor = Color.ORANGE;
        } else if (healthPercent <= 0.75) {
            healthColor = Color.YELLOW;
        }
        
        g2d.setColor(healthColor);
        int healthWidth = (int)(barWidth * healthPercent);
        g2d.fillRect(barX + 1, barY + 1, healthWidth - 2, barHeight - 2);
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= shotCooldown) {
            lastShotTime = currentTime;
            return true;
        }
        return false;
    }
    
    public Rectangle getBounds() {
        // Smaller hitbox - 70% of actual sprite size for more precise collision
        int hitboxWidth = (int)(width * 0.7);
        int hitboxHeight = (int)(height * 0.7);
        int offsetX = (width - hitboxWidth) / 2;
        int offsetY = (height - hitboxHeight) / 2;
        return new Rectangle(x + offsetX, y + offsetY, hitboxWidth, hitboxHeight);
    }
    
    public boolean takeDamage(int damage) {
        currentHealth -= damage;
        hit = true;
        hitTimer = 10; // Flash for 10 frames
        return currentHealth <= 0;
    }
    
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public void resetHealth() {
        currentHealth = maxHealth;
        hit = false;
        hitTimer = 0;
    }
}