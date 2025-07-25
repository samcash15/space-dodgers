import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BossAsteroid extends Asteroid {
    private int maxHealth;
    private int health;
    private boolean hit = false;
    private int hitTimer = 0;
    private Color originalColor;
    
    public BossAsteroid(int x, int y, int size, double speed) {
        super(x, y, size, speed);
        this.maxHealth = size / 15; // Health based on size
        this.health = maxHealth;
        
        // Darker, more menacing color
        Random random = new Random();
        int grayValue = 50 + random.nextInt(50);
        this.originalColor = new Color(grayValue + 50, grayValue, grayValue);
    }
    
    @Override
    public void update() {
        super.update();
        
        if (hit) {
            hitTimer--;
            if (hitTimer <= 0) {
                hit = false;
            }
        }
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Save original transform
        var oldTransform = g2d.getTransform();
        
        // Rotate around asteroid center
        g2d.translate(getX() + getSize()/2, getY() + getSize()/2);
        g2d.rotate(Math.toRadians(getRotation()));
        g2d.translate(-getSize()/2, -getSize()/2);
        
        // Flash red when hit
        Color drawColor = hit ? Color.RED : originalColor;
        g2d.setColor(drawColor);
        
        // Draw boss asteroid (more complex shape)
        int[] xPoints = new int[12];
        int[] yPoints = new int[12];
        
        Random random = new Random(getX() + getY()); // Consistent shape
        for (int i = 0; i < 12; i++) {
            double angle = i * Math.PI / 6;
            double radius = getSize()/2 + (random.nextInt(10) - 5);
            xPoints[i] = (int)(getSize()/2 + radius * Math.cos(angle));
            yPoints[i] = (int)(getSize()/2 + radius * Math.sin(angle));
        }
        
        g2d.fillPolygon(xPoints, yPoints, 12);
        
        // Draw spikes for intimidation
        g2d.setColor(drawColor.brighter());
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;
            int spikeX = (int)(getSize()/2 + (getSize()/2 - 5) * Math.cos(angle));
            int spikeY = (int)(getSize()/2 + (getSize()/2 - 5) * Math.sin(angle));
            int tipX = (int)(getSize()/2 + (getSize()/2 + 8) * Math.cos(angle));
            int tipY = (int)(getSize()/2 + (getSize()/2 + 8) * Math.sin(angle));
            
            g2d.drawLine(spikeX, spikeY, tipX, tipY);
            g2d.drawLine(spikeX - 1, spikeY - 1, tipX, tipY);
        }
        
        // Draw darker outline
        g2d.setColor(drawColor.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 12);
        
        // Draw health bar above boss using sprites
        drawHealthBarSprite(g2d);
        
        // Restore transform
        g2d.setTransform(oldTransform);
    }
    
    public boolean takeDamage() {
        health--;
        hit = true;
        hitTimer = 5; // Flash for 5 frames
        return health <= 0;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public boolean isBoss() {
        return true;
    }
    
    private int getRotation() {
        return rotation;
    }
    
    private void drawHealthBarSprite(Graphics2D g2d) {
        int barWidth = getSize() + 20;
        int barHeight = 8;
        int barX = -10;
        int barY = -barHeight - 15;
        
        double healthPercent = (double)health / maxHealth;
        
        // Choose health bar color based on health percentage
        String barType;
        if (healthPercent > 0.6) {
            barType = "horizontal_bar_green";
        } else if (healthPercent > 0.3) {
            barType = "horizontal_bar_yellow";
        } else {
            barType = "horizontal_bar_red";
        }
        
        // Load health bar sprite
        BufferedImage healthBarSprite = SpriteLoader.getHealthBar(barType);
        
        if (healthBarSprite != null) {
            // Draw background (grey bar)
            BufferedImage greyBar = SpriteLoader.getHealthBar("horizontal_bar_grey");
            if (greyBar != null) {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                   RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2d.drawImage(greyBar, barX, barY, barWidth, barHeight, null);
            }
            
            // Draw health portion
            int healthWidth = (int)(barWidth * healthPercent);
            if (healthWidth > 0) {
                g2d.drawImage(healthBarSprite, barX, barY, healthWidth, barHeight, 
                            0, 0, (int)(healthBarSprite.getWidth() * healthPercent), 
                            healthBarSprite.getHeight(), null);
            }
        } else {
            // Fallback to original health bar if sprites fail
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            Color healthColor = healthPercent > 0.5 ? Color.GREEN : 
                               healthPercent > 0.25 ? Color.YELLOW : Color.RED;
            g2d.setColor(healthColor);
            g2d.fillRect(barX + 1, barY + 1, (int)((barWidth - 2) * healthPercent), barHeight - 2);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}