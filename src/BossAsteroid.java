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
        
        // Restore transform before drawing health bar
        g2d.setTransform(oldTransform);
        
        // Draw health bar above boss using sprites (outside rotation)
        drawHealthBarSprite(g2d);
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
        int barHeight = 6;
        int barX = getX() - 10;
        int barY = getY() - barHeight - 10;
        
        double healthPercent = (double)health / maxHealth;
        
        // Draw background (dark gray)
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Choose health bar color based on health percentage
        Color healthColor = Color.GREEN;
        if (healthPercent <= 0.25) {
            healthColor = Color.RED;
        } else if (healthPercent <= 0.5) {
            healthColor = Color.ORANGE;
        } else if (healthPercent <= 0.75) {
            healthColor = Color.YELLOW;
        }
        
        // Draw health portion
        int healthWidth = (int)(barWidth * healthPercent);
        if (healthWidth > 0) {
            g2d.setColor(healthColor);
            g2d.fillRect(barX + 1, barY + 1, healthWidth - 2, barHeight - 2);
        }
        
        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
}