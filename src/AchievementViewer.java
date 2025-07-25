import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class AchievementViewer {
    private AchievementManager achievementManager;
    private SpaceDodger game;
    private int scrollOffset = 0;
    private final int ITEMS_PER_PAGE = 8;
    private final int ITEM_HEIGHT = 80;
    
    public AchievementViewer(SpaceDodger game, AchievementManager achievementManager) {
        this.game = game;
        this.achievementManager = achievementManager;
    }
    
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, 1200, 800);
        
        // Draw title
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "ACHIEVEMENTS";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (1200 - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 80);
        
        // Draw progress summary
        int unlockedCount = achievementManager.getUnlockedCount();
        int totalCount = achievementManager.getTotalCount();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String progress = "Progress: " + unlockedCount + "/" + totalCount + " (" + 
                         (int)((double)unlockedCount / totalCount * 100) + "%)";
        fm = g2d.getFontMetrics();
        int progressX = (1200 - fm.stringWidth(progress)) / 2;
        g2d.drawString(progress, progressX, 120);
        
        // Draw controls at top
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.drawString("ESC: Back to Menu", 50, 140);
        g2d.drawString("↑/↓: Scroll", 250, 140);
        
        List<Achievement> achievements = achievementManager.getAllAchievements();
        
        // Draw achievements list
        int startY = 170;
        int currentY = startY;
        
        // Sort achievements: unlocked first, then by type
        achievements.sort((a, b) -> {
            if (a.isUnlocked() != b.isUnlocked()) {
                return a.isUnlocked() ? -1 : 1; // Unlocked first
            }
            return a.getType().compareTo(b.getType());
        });
        
        for (int i = scrollOffset; i < Math.min(achievements.size(), scrollOffset + ITEMS_PER_PAGE); i++) {
            Achievement achievement = achievements.get(i);
            drawAchievement(g2d, achievement, 50, currentY);
            currentY += ITEM_HEIGHT + 10;
        }
    }
    
    private void drawAchievement(Graphics2D g2d, Achievement achievement, int x, int y) {
        int width = 1100;
        int height = ITEM_HEIGHT;
        
        // Draw background
        if (achievement.isUnlocked()) {
            // Unlocked - green gradient
            GradientPaint gradient = new GradientPaint(
                x, y, new Color(0, 100, 0, 180),
                x, y + height, new Color(0, 150, 0, 180)
            );
            g2d.setPaint(gradient);
        } else {
            // Locked - gray
            g2d.setColor(new Color(50, 50, 50, 180));
        }
        g2d.fillRoundRect(x, y, width, height, 10, 10);
        
        // Draw border
        if (achievement.isUnlocked()) {
            g2d.setColor(new Color(255, 215, 0)); // Gold
        } else {
            g2d.setColor(new Color(150, 150, 150)); // Gray
        }
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 10, 10);
        
        // Draw trophy icon
        int iconSize = 32;
        int iconX = x + 20;
        int iconY = y + height/2 - iconSize/2;
        
        if (achievement.isUnlocked()) {
            g2d.setColor(new Color(255, 215, 0)); // Gold trophy
        } else {
            g2d.setColor(new Color(100, 100, 100)); // Gray trophy
        }
        
        // Draw simple trophy shape
        g2d.fillOval(iconX, iconY, iconSize, iconSize);
        g2d.fillRect(iconX + iconSize/4, iconY + iconSize - 8, iconSize/2, 10);
        g2d.fillRect(iconX + iconSize/6, iconY + iconSize + 2, iconSize*2/3, 6);
        
        // Draw achievement text
        int textX = iconX + iconSize + 20;
        
        // Achievement name
        g2d.setColor(achievement.isUnlocked() ? Color.WHITE : Color.LIGHT_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(achievement.getName(), textX, y + 25);
        
        // Achievement description
        g2d.setColor(achievement.isUnlocked() ? Color.LIGHT_GRAY : Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(achievement.getDescription(), textX, y + 50);
        
        // Progress bar and text
        int progressX = textX + 600;
        int progressY = y + 15;
        int progressWidth = 200;
        int progressHeight = 20;
        
        // Progress background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(progressX, progressY, progressWidth, progressHeight);
        
        // Progress fill
        double progressPercent = achievement.getProgressPercent();
        if (progressPercent > 0) {
            if (achievement.isUnlocked()) {
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.YELLOW);
            }
            int fillWidth = (int)(progressWidth * progressPercent);
            g2d.fillRect(progressX + 1, progressY + 1, fillWidth - 2, progressHeight - 2);
        }
        
        // Progress border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(progressX, progressY, progressWidth, progressHeight);
        
        // Progress text
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String progressText = achievement.getCurrentValue() + "/" + achievement.getTargetValue();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(progressText);
        g2d.drawString(progressText, progressX + (progressWidth - textWidth) / 2, progressY + 15);
        
        // Completion status
        if (achievement.isUnlocked()) {
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("✓ UNLOCKED", progressX, progressY + 50);
        } else {
            g2d.setColor(Color.ORANGE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            int remaining = achievement.getTargetValue() - achievement.getCurrentValue();
            g2d.drawString(remaining + " more to unlock", progressX, progressY + 50);
        }
    }
    
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_ESCAPE) {
            // Return to main menu
            game.returnToMenu();
        } else if (key == KeyEvent.VK_UP) {
            scrollOffset = Math.max(0, scrollOffset - 1);
        } else if (key == KeyEvent.VK_DOWN) {
            int maxScroll = Math.max(0, achievementManager.getAllAchievements().size() - ITEMS_PER_PAGE);
            scrollOffset = Math.min(maxScroll, scrollOffset + 1);
        }
    }
}