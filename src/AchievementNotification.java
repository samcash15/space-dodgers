import java.awt.*;

public class AchievementNotification {
    private Achievement achievement;
    private float alpha;
    private int displayTime;
    private int maxDisplayTime;
    private int yOffset;
    
    public AchievementNotification(Achievement achievement) {
        this.achievement = achievement;
        this.alpha = 0.0f;
        this.maxDisplayTime = 300; // 5 seconds at 60 FPS
        this.displayTime = maxDisplayTime;
        this.yOffset = 0;
    }
    
    public void update() {
        if (displayTime > 0) {
            displayTime--;
            
            // Fade in for first 30 frames
            if (displayTime > maxDisplayTime - 30) {
                alpha = Math.min(1.0f, alpha + 0.05f);
            }
            // Fade out for last 60 frames
            else if (displayTime < 60) {
                alpha = Math.max(0.0f, alpha - 0.02f);
            }
            // Stay visible in between
            else {
                alpha = 1.0f;
            }
        }
    }
    
    public void draw(Graphics g, int x, int y) {
        if (displayTime <= 0 || alpha <= 0) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Save original composite
        Composite originalComposite = g2d.getComposite();
        
        // Set transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        // Notification dimensions
        int width = 350;
        int height = 70;
        int actualY = y + yOffset;
        
        // Draw simple solid background for testing
        g2d.setColor(new Color(0, 150, 0, 200));
        g2d.fillRoundRect(x, actualY, width, height, 10, 10);
        
        // Draw border
        g2d.setColor(new Color(255, 215, 0)); // Gold color
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, actualY, width, height, 10, 10);
        
        // Draw achievement icon (trophy symbol)
        g2d.setColor(new Color(255, 215, 0)); // Gold
        int iconSize = 24;
        int iconX = x + 15;
        int iconY = actualY + height/2 - iconSize/2;
        
        // Draw trophy shape
        g2d.fillOval(iconX, iconY, iconSize, iconSize);
        g2d.fillRect(iconX + iconSize/4, iconY + iconSize - 5, iconSize/2, 8);
        g2d.fillRect(iconX + iconSize/6, iconY + iconSize + 3, iconSize*2/3, 4);
        
        // Draw text
        g2d.setColor(Color.WHITE);
        
        // "Achievement Unlocked!" header
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Achievement Unlocked!", iconX + iconSize + 10, actualY + 20);
        
        // Achievement name
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(achievement.getName(), iconX + iconSize + 10, actualY + 40);
        
        // Achievement description
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(220, 220, 220));
        g2d.drawString(achievement.getDescription(), iconX + iconSize + 10, actualY + 58);
        
        // Restore original composite
        g2d.setComposite(originalComposite);
    }
    
    public boolean isFinished() {
        return displayTime <= 0;
    }
    
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }
    
    public int getHeight() {
        return 70;
    }
}