import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class MainBoss {
    private int x, y;
    private int width, height;
    private int maxHealth;
    private int currentHealth;
    private String stationType;
    private BufferedImage sprite;
    private Random random;
    
    private int moveDirection = 1; // 1 for right, -1 for left
    private int speed = 2;
    private int shootCooldown = 0;
    private int maxShootCooldown = 60; // Shoot every second at 60 FPS
    private int phase = 1; // Boss phases get harder
    private boolean hit = false;
    private int hitTimer = 0;
    
    public MainBoss(int x, int y, String stationType) {
        this.x = x;
        this.y = y;
        this.stationType = stationType;
        this.random = new Random();
        
        // Load sprite and set dimensions
        this.sprite = SpriteLoader.getMainBossStation(stationType);
        if (sprite != null) {
            this.width = sprite.getWidth() * 2; // Scale up for boss (reduced from 3x)
            this.height = sprite.getHeight() * 2;
        } else {
            this.width = 80;
            this.height = 80;
        }
        
        // Boss has massive health - scales with difficulty
        this.maxHealth = 150;
        this.currentHealth = maxHealth;
    }
    
    public void update(int screenWidth, int screenHeight) {
        // Hit flash effect
        if (hit) {
            hitTimer--;
            if (hitTimer <= 0) {
                hit = false;
            }
        }
        
        // Movement pattern - move side to side
        x += moveDirection * speed;
        
        // Bounce off screen edges
        if (x <= 0) {
            moveDirection = 1;
            // Move down slightly when hitting edge
            y += 20;
        } else if (x >= screenWidth - width) {
            moveDirection = -1;
            // Move down slightly when hitting edge
            y += 20;
        }
        
        // Update shooting cooldown
        if (shootCooldown > 0) {
            shootCooldown--;
        }
        
        // Phase transitions based on health
        if (currentHealth < maxHealth * 0.66 && phase == 1) {
            phase = 2;
            speed = 3; // Faster movement
            maxShootCooldown = 40; // Shoot more frequently
        } else if (currentHealth < maxHealth * 0.33 && phase == 2) {
            phase = 3;
            speed = 4; // Even faster
            maxShootCooldown = 30; // Rapid fire
        }
    }
    
    public boolean canShoot() {
        if (shootCooldown <= 0) {
            shootCooldown = maxShootCooldown;
            return true;
        }
        return false;
    }
    
    public boolean takeDamage(int damage) {
        currentHealth -= damage;
        hit = true;
        hitTimer = 10;
        return currentHealth <= 0; // Returns true if boss is destroyed
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering hints for smooth scaling
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        // Flash effect when hit
        if (hit && hitTimer % 4 < 2) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        
        // Draw boss sprite
        if (sprite != null) {
            g2d.drawImage(sprite, x, y, width, height, null);
        } else {
            // Fallback rectangle
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, width, height);
        }
        
        // Reset composite
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Draw health bar above boss
        drawHealthBar(g2d);
        
        // Draw phase indicator
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("PHASE " + phase, x, y - 40);
    }
    
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = width;
        int barHeight = 12;
        int barX = x;
        int barY = y - 25;
        
        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Health fill
        double healthPercent = (double) currentHealth / maxHealth;
        int fillWidth = (int) (barWidth * healthPercent);
        
        // Color based on health
        if (healthPercent > 0.66) {
            g2d.setColor(Color.GREEN);
        } else if (healthPercent > 0.33) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.RED);
        }
        
        g2d.fillRect(barX, barY, fillWidth, barHeight);
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
        
        // Health text
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String healthText = currentHealth + "/" + maxHealth;
        FontMetrics fm = g2d.getFontMetrics();
        int textX = barX + (barWidth - fm.stringWidth(healthText)) / 2;
        g2d.drawString(healthText, textX, barY + 9);
    }
    
    public String getLaserType() {
        // Return laser type based on station color
        if (stationType.contains("blue")) return "pixel_laser_blue";
        if (stationType.contains("green")) return "pixel_laser_green";
        if (stationType.contains("red")) return "pixel_laser_red";
        if (stationType.contains("yellow")) return "pixel_laser_yellow";
        return "pixel_laser_blue"; // Default
    }
    
    public int getPointValue() {
        return 500; // Massive points for defeating boss
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getPhase() { return phase; }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}