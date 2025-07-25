import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemyShip {
    private double x, y;
    private int width, height;
    private double speed;
    private String shipType;
    private BufferedImage sprite;
    private Random random;
    private double movePattern;
    private int health;
    private boolean hit = false;
    private int hitTimer = 0;
    private int shootTimer = 0;
    private int shootCooldown;
    
    public EnemyShip(int x, int y, String shipType) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.sprite = SpriteLoader.getEnemyShip(shipType);
        this.random = new Random();
        
        if (sprite != null) {
            this.width = (int)(sprite.getWidth() * 1.5); // Consistent scale with player
            this.height = (int)(sprite.getHeight() * 1.5);
        } else {
            this.width = 40;
            this.height = 40;
        }
        
        // Different ship types have different properties
        if (shipType.contains("station")) {
            this.speed = 0.5 + random.nextDouble() * 1.0; // Stations are slower but tougher
            this.health = 3;
        } else if (shipType.contains("small")) {
            this.speed = 2.0 + random.nextDouble() * 2.0; // Small ships are faster
            this.health = 1;
        } else {
            this.speed = 1.0 + random.nextDouble() * 1.5; // Medium ships
            this.health = 2;
        }
        
        this.movePattern = random.nextDouble() * Math.PI * 2; // Random movement pattern
        
        // Set shooting cooldown based on ship type
        if (shipType.contains("station")) {
            this.shootCooldown = 90 + random.nextInt(60); // Stations shoot less frequently
        } else if (shipType.contains("small")) {
            this.shootCooldown = 120 + random.nextInt(80); // Small ships shoot less
        } else {
            this.shootCooldown = 100 + random.nextInt(70); // Medium ships
        }
        this.shootTimer = random.nextInt(shootCooldown); // Random initial delay
    }
    
    public void update() {
        // Move down
        y += speed;
        
        // Add horizontal movement pattern for more interesting AI
        movePattern += 0.05;
        x += Math.sin(movePattern) * 0.8;
        
        // Keep ships on screen horizontally
        if (x < 0) x = 0;
        if (x > 1200 - width) x = 1200 - width;
        
        // Handle hit flash effect
        if (hit) {
            hitTimer--;
            if (hitTimer <= 0) {
                hit = false;
            }
        }
        
        // Update shooting timer
        shootTimer++;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering hints for crisp pixel art
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        if (sprite != null) {
            // Flash red when hit
            if (hit) {
                // Create a red tinted version
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2d.setColor(Color.RED);
                g2d.fillRect((int)x, (int)y, width, height);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            
            g2d.drawImage(sprite, (int)x, (int)y, width, height, null);
        } else {
            // Fallback rectangle if sprite fails to load
            g2d.setColor(hit ? Color.RED : Color.MAGENTA);
            g2d.fillRect((int)x, (int)y, width, height);
            g2d.setColor(Color.WHITE);
            g2d.drawRect((int)x, (int)y, width, height);
        }
        
        // Draw health indicator for tougher ships
        if (health > 1) {
            drawHealthBar(g2d);
        }
    }
    
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = width;
        int barHeight = 4;
        int barX = (int)x;
        int barY = (int)y - 8;
        
        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Health bar - different colors based on ship type
        Color healthColor = Color.GREEN;
        if (shipType.contains("red")) healthColor = Color.RED;
        else if (shipType.contains("blue")) healthColor = Color.CYAN;
        else if (shipType.contains("yellow")) healthColor = Color.YELLOW;
        
        g2d.setColor(healthColor);
        int healthWidth = (int)(barWidth * (health / (shipType.contains("station") ? 3.0 : 2.0)));
        g2d.fillRect(barX + 1, barY + 1, healthWidth - 2, barHeight - 2);
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
    
    public boolean takeDamage() {
        health--;
        hit = true;
        hitTimer = 5; // Flash for 5 frames
        return health <= 0;
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
    
    public String getShipType() {
        return shipType;
    }
    
    public boolean canShoot() {
        if (shootTimer >= shootCooldown) {
            shootTimer = 0;
            return true;
        }
        return false;
    }
    
    public String getLaserType() {
        // Map ship colors to laser colors
        if (shipType.contains("blue")) {
            return "pixel_laser_blue";
        } else if (shipType.contains("green")) {
            return "pixel_laser_green";
        } else if (shipType.contains("red")) {
            return "pixel_laser_red";
        } else if (shipType.contains("yellow")) {
            return "pixel_laser_yellow";
        } else {
            // Default for stations or other ships
            return "pixel_laser_red";
        }
    }
    
    public int getPointValue() {
        // Different point values based on ship type
        if (shipType.contains("station")) {
            return 75; // Stations are worth more
        } else if (shipType.contains("small")) {
            return 30; // Small ships are worth less but faster
        } else {
            return 50; // Medium ships
        }
    }
}