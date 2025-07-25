import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class ShipSelectionMenu extends JPanel implements KeyListener {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    
    private String[] availableShips;
    private int selectedShipIndex = 0;
    private boolean menuActive = true;
    private SpaceDodger game;
    
    public ShipSelectionMenu(SpaceDodger game) {
        this.game = game;
        this.availableShips = SpriteLoader.getAvailablePlayerShips();
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!menuActive) return;
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background sprite
        BufferedImage background = SpriteLoader.getBackground("background-black");
        if (background != null) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                               RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
        } else {
            // Fallback to starfield
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 100; i++) {
                int x = (i * 37) % WIDTH;
                int y = (i * 23) % HEIGHT;
                g2d.fillRect(x, y, 1, 1);
            }
        }
        
        // Title
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "🚀 SELECT YOUR SHIP 🚀";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WIDTH - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 100);
        
        // Instructions
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        String instructions = "Use LEFT/RIGHT arrows to select • SPACE to confirm";
        fm = g2d.getFontMetrics();
        int instrX = (WIDTH - fm.stringWidth(instructions)) / 2;
        g2d.drawString(instructions, instrX, 150);
        
        // Ship preview area - centered vertically in the middle of screen
        int shipY = HEIGHT / 2;
        int shipSpacing = 200;
        int startX = (WIDTH - (availableShips.length * shipSpacing)) / 2 + shipSpacing/2;
        
        for (int i = 0; i < availableShips.length; i++) {
            int shipX = startX + i * shipSpacing;
            
            // Highlight selected ship
            if (i == selectedShipIndex) {
                g2d.setColor(new Color(0, 255, 255, 100));
                g2d.fillRoundRect(shipX - 80, shipY - 80, 160, 160, 20, 20);
                g2d.setColor(Color.CYAN);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(shipX - 80, shipY - 80, 160, 160, 20, 20);
            }
            
            // Load and draw ship sprite
            BufferedImage shipSprite = SpriteLoader.getPlayerShip(availableShips[i]);
            if (shipSprite != null) {
                // Scale up the sprite for better visibility
                int scale = 3;
                int drawWidth = shipSprite.getWidth() * scale;
                int drawHeight = shipSprite.getHeight() * scale;
                
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                   RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2d.drawImage(shipSprite, 
                            shipX - drawWidth/2, 
                            shipY - drawHeight/2,
                            drawWidth, drawHeight, null);
            }
        }
        
        // Selection arrows
        if (selectedShipIndex > 0) {
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.drawString("◀", startX - 120, shipY + 15);
        }
        
        if (selectedShipIndex < availableShips.length - 1) {
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.drawString("▶", startX + (availableShips.length - 1) * shipSpacing + 80, shipY + 15);
        }
        
        // Bottom instructions
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String confirm = "Press SPACE to start your adventure!";
        fm = g2d.getFontMetrics();
        int confirmX = (WIDTH - fm.stringWidth(confirm)) / 2;
        g2d.drawString(confirm, confirmX, HEIGHT - 120);
        
        // Achievements option
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String achievements = "Press A for Achievements";
        fm = g2d.getFontMetrics();
        int achievementsX = (WIDTH - fm.stringWidth(achievements)) / 2;
        g2d.drawString(achievements, achievementsX, HEIGHT - 80);
        
        // Additional controls
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String controls = "← → to select ship";
        fm = g2d.getFontMetrics();
        int controlsX = (WIDTH - fm.stringWidth(controls)) / 2;
        g2d.drawString(controls, controlsX, HEIGHT - 40);
    }
    
    
    public String getSelectedShip() {
        return availableShips[selectedShipIndex];
    }
    
    public boolean isMenuActive() {
        return menuActive;
    }
    
    public void setMenuActive(boolean active) {
        this.menuActive = active;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (!menuActive) return;
        
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT && selectedShipIndex > 0) {
            selectedShipIndex--;
            repaint();
        } else if (key == KeyEvent.VK_RIGHT && selectedShipIndex < availableShips.length - 1) {
            selectedShipIndex++;
            repaint();
        } else if (key == KeyEvent.VK_SPACE) {
            menuActive = false;
            game.startGameWithSelectedShip(getSelectedShip());
        } else if (key == KeyEvent.VK_A) {
            game.showAchievements();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
}