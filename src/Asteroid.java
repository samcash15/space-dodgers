import java.awt.*;
import java.util.Random;

public class Asteroid {
    private double x, y;
    private int size;
    private double speed;
    protected int rotation;
    private int rotationSpeed;
    private Color color;
    private Random random;
    
    public Asteroid(int x, int y, int size, double speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.rotation = 0;
        this.random = new Random();
        this.rotationSpeed = random.nextInt(10) - 5;
        
        // Random gray color for variety
        int grayValue = 100 + random.nextInt(100);
        this.color = new Color(grayValue, grayValue, grayValue);
    }
    
    public void update() {
        y += speed;
        rotation += rotationSpeed;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Save original transform
        var oldTransform = g2d.getTransform();
        
        // Rotate around asteroid center
        g2d.translate(x + size/2, y + size/2);
        g2d.rotate(Math.toRadians(rotation));
        g2d.translate(-size/2, -size/2);
        
        // Draw asteroid (irregular polygon)
        g2d.setColor(color);
        int[] xPoints = new int[8];
        int[] yPoints = new int[8];
        
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            double radius = size/2 + (random.nextInt(10) - 5);
            xPoints[i] = (int)(size/2 + radius * Math.cos(angle));
            yPoints[i] = (int)(size/2 + radius * Math.sin(angle));
        }
        
        g2d.fillPolygon(xPoints, yPoints, 8);
        
        // Draw darker outline
        g2d.setColor(color.darker());
        g2d.drawPolygon(xPoints, yPoints, 8);
        
        // Restore transform
        g2d.setTransform(oldTransform);
    }
    
    public int getX() {
        return (int)x;
    }
    
    public int getY() {
        return (int)y;
    }
    
    public int getSize() {
        return size;
    }
}