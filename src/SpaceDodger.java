import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SpaceDodger extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int DELAY = 16; // ~60 FPS
    
    private Timer timer;
    private Player player;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Bullet> bullets;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Explosion> explosions;
    private ArrayList<EnemyShip> enemyShips;
    private ArrayList<EnemyBullet> enemyBullets;
    private ArrayList<BackgroundStar> backgroundStars;
    private ArrayList<AchievementNotification> achievementNotifications;
    private Random random;
    
    // Achievement tracking
    private AchievementManager achievementManager;
    private int totalEnemyKills;
    private int totalAsteroidDestroyed;
    private int totalBossKills;
    private int totalDamageTaken;
    private int totalShotsFired;
    
    private int score;
    private int asteroidSpawnCounter;
    private int powerUpSpawnCounter;
    private int bossSpawnCounter;
    private int enemySpawnCounter;
    private int difficulty;
    private boolean gameRunning;
    private boolean gameOver;
    private boolean shootingPressed = false;
    
    // Game states
    private GameState currentState;
    private ShipSelectionMenu shipMenu;
    private AchievementViewer achievementViewer;
    private String selectedShipType;
    
    private enum GameState {
        MENU, ACHIEVEMENTS, PLAYING, PAUSED, GAME_OVER
    }
    
    public SpaceDodger() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        initMenu();
    }
    
    private void initMenu() {
        currentState = GameState.MENU;
        shipMenu = new ShipSelectionMenu(this);
        random = new Random();
        
        // Initialize achievement system
        if (achievementManager == null) {
            achievementManager = new AchievementManager();
            System.out.println("Achievement Manager initialized");
        }
        if (achievementNotifications == null) {
            achievementNotifications = new ArrayList<>();
        }
        if (achievementViewer == null) {
            achievementViewer = new AchievementViewer(this, achievementManager);
        }
        
        // Stop existing timer before creating new one
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    public void startGameWithSelectedShip(String shipType) {
        this.selectedShipType = shipType;
        initGame();
    }
    
    public void showAchievements() {
        currentState = GameState.ACHIEVEMENTS;
    }
    
    public void returnToMenu() {
        currentState = GameState.MENU;
    }
    
    private void initGame() {
        currentState = GameState.PLAYING;
        player = new Player(WIDTH / 2 - 25, HEIGHT - 200, selectedShipType);
        player.setHasBlaster(true); // Start with blaster ability
        player.resetHealth(); // Reset player health
        asteroids = new ArrayList<>();
        bullets = new ArrayList<>();
        powerUps = new ArrayList<>();
        explosions = new ArrayList<>();
        enemyShips = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        
        // Initialize achievement notifications if not already done
        if (achievementNotifications == null) {
            achievementNotifications = new ArrayList<>();
        } else {
            // Clear any existing notifications when starting new game
            achievementNotifications.clear();
        }
        
        // Initialize background stars
        backgroundStars = new ArrayList<>();
        for (int i = 0; i < 50; i++) { // Subtle amount of stars
            backgroundStars.add(new BackgroundStar(WIDTH, HEIGHT));
        }
        
        score = 0;
        asteroidSpawnCounter = 0;
        powerUpSpawnCounter = 0;
        bossSpawnCounter = 0;
        enemySpawnCounter = 0;
        difficulty = 1;
        gameRunning = true;
        gameOver = false;
        shootingPressed = false;
        
        // Reset achievement tracking variables
        totalEnemyKills = 0;
        totalAsteroidDestroyed = 0;
        totalBossKills = 0;
        totalDamageTaken = 0;
        totalShotsFired = 0;
        
        // Debug: Check if achievement manager is ready
        if (achievementManager != null) {
            System.out.println("Achievement manager ready in game");
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        switch (currentState) {
            case MENU:
                shipMenu.paintComponent(g);
                break;
            case ACHIEVEMENTS:
                achievementViewer.paintComponent(g);
                break;
            case PLAYING:
                if (gameRunning && !gameOver) {
                    drawGame(g);
                }
                break;
            case PAUSED:
                drawGame(g); // Draw game in background
                drawPauseOverlay(g); // Draw pause overlay on top
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
        }
        
        // Always draw achievement notifications on top of everything
        drawAchievementNotifications(g);
    }
    
    private void drawGame(Graphics g) {
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
            for (int i = 0; i < 50; i++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                g2d.fillRect(x, y, 2, 2);
            }
        }
        
        // Draw moving background stars
        for (BackgroundStar star : backgroundStars) {
            star.draw(g);
        }
        
        // Draw player
        player.draw(g);
        
        // Draw asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(g);
        }
        
        // Draw bullets
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        
        // Draw enemy bullets
        for (EnemyBullet enemyBullet : enemyBullets) {
            enemyBullet.draw(g);
        }
        
        // Draw power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }
        
        // Draw explosions
        for (Explosion explosion : explosions) {
            explosion.draw(g);
        }
        
        // Draw enemy ships
        for (EnemyShip enemyShip : enemyShips) {
            enemyShip.draw(g);
        }
        
        // Draw score and health
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("Level: " + difficulty, 10, 60);
        g.drawString("Health: " + player.getCurrentHealth() + "/" + player.getMaxHealth(), 10, 90);
        if (player.hasBlaster()) {
            g.setColor(Color.YELLOW);
            g.drawString("BLASTER ACTIVE", 10, 120);
        }
    }
    
    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOverText = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, x, HEIGHT / 2 - 50);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreText = "Final Score: " + score;
        fm = g.getFontMetrics();
        x = (WIDTH - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, x, HEIGHT / 2);
        
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String restartText = "Press SPACE to restart";
        fm = g.getFontMetrics();
        x = (WIDTH - fm.stringWidth(restartText)) / 2;
        g.drawString(restartText, x, HEIGHT / 2 + 50);
    }
    
    private void drawPauseOverlay(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // "PAUSED" text
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        String pausedText = "PAUSED";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(pausedText)) / 2;
        g2d.drawString(pausedText, x, HEIGHT / 2 - 50);
        
        // Instructions
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        String resumeText = "Press ESC to resume";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(resumeText)) / 2;
        g2d.drawString(resumeText, x, HEIGHT / 2 + 20);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        String menuText = "Press M to return to menu";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(menuText)) / 2;
        g2d.drawString(menuText, x, HEIGHT / 2 + 60);
    }
    
    private void drawAchievementNotifications(Graphics g) {
        // Draw achievement notifications
        for (int i = 0; i < achievementNotifications.size(); i++) {
            AchievementNotification notification = achievementNotifications.get(i);
            notification.draw(g, WIDTH - 370, 20 + i * 57);
        }
    }
    
    private void update() {
        // Always update achievement notifications regardless of game state
        updateAchievementNotifications();
        
        if (currentState == GameState.MENU) {
            return;
        }
        
        if (currentState == GameState.PAUSED) {
            return; // Don't update game logic when paused
        }
        
        if (currentState == GameState.GAME_OVER) {
            return;
        }
        
        if (currentState == GameState.ACHIEVEMENTS) {
            return; // Don't update game logic when viewing achievements
        }
        
        if (!gameRunning || gameOver) {
            currentState = GameState.GAME_OVER;
            return;
        }
        
        // Update background stars
        for (BackgroundStar star : backgroundStars) {
            star.update(HEIGHT);
        }
        
        // Update player
        player.update();
        
        // Handle shooting
        if (shootingPressed && player.canShoot()) {
            bullets.add(new Bullet(player.getX() + player.getWidth() / 2 - 2, player.getY()));
            totalShotsFired++;
            if (achievementManager != null) {
                achievementManager.updateProgress(Achievement.AchievementType.SHOTS_FIRED, 1);
            }
            if (totalShotsFired == 1) {
                System.out.println("First shot fired! Shots: " + totalShotsFired);
            }
        }
        
        // Update explosions
        Iterator<Explosion> explosionIter = explosions.iterator();
        while (explosionIter.hasNext()) {
            Explosion explosion = explosionIter.next();
            explosion.update();
            if (explosion.isFinished()) {
                explosionIter.remove();
            }
        }
        
        // Update bullets
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            bullet.update();
            
            // Remove bullets that go off screen
            if (bullet.getY() < 0) {
                bulletIter.remove();
                continue;
            }
            
            // Check collision with asteroids
            Iterator<Asteroid> asteroidIter = asteroids.iterator();
            while (asteroidIter.hasNext()) {
                Asteroid asteroid = asteroidIter.next();
                if (bullet.getBounds().intersects(new Rectangle(asteroid.getX(), asteroid.getY(), 
                                                                asteroid.getSize(), asteroid.getSize()))) {
                    
                    // Handle boss asteroids differently
                    if (asteroid instanceof BossAsteroid) {
                        BossAsteroid boss = (BossAsteroid) asteroid;
                        if (boss.takeDamage()) {
                            // Boss destroyed
                            explosions.add(new Explosion(asteroid.getX(), asteroid.getY(), asteroid.getSize()));
                            asteroidIter.remove();
                            score += 100; // Big bonus for destroying boss
                            totalBossKills++;
                            if (achievementManager != null) {
                                achievementManager.updateProgress(Achievement.AchievementType.BOSS_KILLS, 1);
                            }
                        }
                        bulletIter.remove();
                        break;
                    } else {
                        // Regular asteroid
                        explosions.add(new Explosion(asteroid.getX(), asteroid.getY(), asteroid.getSize()));
                        asteroidIter.remove();
                        bulletIter.remove();
                        score += 25; // Extra points for shooting
                        totalAsteroidDestroyed++;
                        if (achievementManager != null) {
                            achievementManager.updateProgress(Achievement.AchievementType.ASTEROID_DESTROYED, 1);
                        }
                        break;
                    }
                }
            }
            
            // Check collision with enemy ships
            Iterator<EnemyShip> enemyIter = enemyShips.iterator();
            while (enemyIter.hasNext()) {
                EnemyShip enemy = enemyIter.next();
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    if (enemy.takeDamage()) {
                        // Enemy destroyed
                        explosions.add(new Explosion(enemy.getX(), enemy.getY(), 
                                                   Math.max(enemy.getWidth(), enemy.getHeight())));
                        enemyIter.remove();
                        score += enemy.getPointValue();
                        totalEnemyKills++;
                        System.out.println("Enemy ship killed! Total enemy kills: " + totalEnemyKills);
                        if (achievementManager != null) {
                            achievementManager.updateProgress(Achievement.AchievementType.ENEMY_KILLS, 1);
                        }
                    }
                    bulletIter.remove();
                    break;
                }
            }
        }
        
        // Update asteroids
        Iterator<Asteroid> iter = asteroids.iterator();
        while (iter.hasNext()) {
            Asteroid asteroid = iter.next();
            asteroid.update();
            
            // Remove asteroids that go off screen
            if (asteroid.getY() > HEIGHT) {
                iter.remove();
                score += 10; // Points for dodging
            }
            
            // Check collision with player
            if (checkCollision(player, asteroid)) {
                int damage;
                if (asteroid instanceof BossAsteroid) {
                    // Boss asteroids cause instant death
                    damage = player.getMaxHealth();
                } else {
                    // Regular asteroids deal 25 damage (1/4 of health)
                    damage = 25;
                }
                
                if (player.takeDamage(damage)) {
                    gameOver = true;
                }
                totalDamageTaken += damage;
                if (achievementManager != null) {
                    achievementManager.updateProgress(Achievement.AchievementType.DAMAGE_TAKEN, damage);
                }
                // Remove asteroid on hit to prevent continuous damage
                iter.remove();
                explosions.add(new Explosion(asteroid.getX(), asteroid.getY(), asteroid.getSize()));
            }
        }
        
        // Update power-ups
        Iterator<PowerUp> powerUpIter = powerUps.iterator();
        while (powerUpIter.hasNext()) {
            PowerUp powerUp = powerUpIter.next();
            powerUp.update();
            
            // Remove power-ups that go off screen
            if (powerUp.getY() > HEIGHT) {
                powerUpIter.remove();
                continue;
            }
            
            // Check collision with player
            if (player.getBounds().intersects(powerUp.getBounds())) {
                if (powerUp.getType() == PowerUp.PowerUpType.BLASTER) {
                    player.setHasBlaster(true);
                }
                powerUpIter.remove();
                score += 50; // Bonus points for power-up
            }
        }
        
        // Update enemy ships
        Iterator<EnemyShip> enemyShipIter = enemyShips.iterator();
        while (enemyShipIter.hasNext()) {
            EnemyShip enemyShip = enemyShipIter.next();
            enemyShip.update();
            
            // Remove enemy ships that go off screen
            if (enemyShip.getY() > HEIGHT) {
                enemyShipIter.remove();
                continue;
            }
            
            // Check collision with player - enemy ships deal 50 damage (1/2 of health)
            if (player.getBounds().intersects(enemyShip.getBounds())) {
                if (player.takeDamage(50)) {
                    gameOver = true;
                }
                totalDamageTaken += 50;
                if (achievementManager != null) {
                    achievementManager.updateProgress(Achievement.AchievementType.DAMAGE_TAKEN, 50);
                }
                // Remove enemy ship on collision
                enemyShipIter.remove();
                explosions.add(new Explosion(enemyShip.getX(), enemyShip.getY(), 
                                           Math.max(enemyShip.getWidth(), enemyShip.getHeight())));
            }
            
            // Enemy shooting
            if (enemyShip.canShoot()) {
                int bulletX = enemyShip.getX() + enemyShip.getWidth() / 2 - 3;
                int bulletY = (int) (enemyShip.getY() + enemyShip.getHeight() - 10); // Spawn from front of ship
                String laserType = enemyShip.getLaserType();
                enemyBullets.add(new EnemyBullet(bulletX, bulletY, laserType));
            }
        }
        
        // Update enemy bullets
        Iterator<EnemyBullet> enemyBulletIter = enemyBullets.iterator();
        while (enemyBulletIter.hasNext()) {
            EnemyBullet enemyBullet = enemyBulletIter.next();
            enemyBullet.update();
            
            // Remove enemy bullets that go off screen
            if (enemyBullet.getY() > HEIGHT) {
                enemyBulletIter.remove();
                continue;
            }
            
            // Check collision with player - enemy bullets deal 50 damage (1/2 of health)
            if (player.getBounds().intersects(enemyBullet.getBounds())) {
                if (player.takeDamage(50)) {
                    gameOver = true;
                }
                totalDamageTaken += 50;
                if (achievementManager != null) {
                    achievementManager.updateProgress(Achievement.AchievementType.DAMAGE_TAKEN, 50);
                }
                enemyBulletIter.remove();
            }
        }
        
        // Spawn new asteroids
        asteroidSpawnCounter++;
        int spawnRate = Math.max(30 - difficulty * 2, 10); // Spawn faster as difficulty increases
        if (asteroidSpawnCounter >= spawnRate) {
            spawnAsteroid();
            asteroidSpawnCounter = 0;
        }
        
        // Spawn power-ups
        powerUpSpawnCounter++;
        if (powerUpSpawnCounter >= 300) { // Spawn every ~5 seconds
            if (random.nextInt(100) < 30 && !player.hasBlaster()) { // 30% chance when player doesn't have blaster
                spawnPowerUp();
            }
            powerUpSpawnCounter = 0;
        }
        
        // Spawn boss asteroids
        bossSpawnCounter++;
        if (bossSpawnCounter >= 240 && difficulty >= 1) { // Spawn boss every ~4 seconds starting at level 1
            if (random.nextInt(100) < (30 + difficulty * 5)) { // 35% chance at level 1, increasing with difficulty
                spawnBossAsteroid();
            }
            bossSpawnCounter = 0;
        }
        
        // Spawn enemy ships
        enemySpawnCounter++;
        if (enemySpawnCounter >= 120 && difficulty >= 1) { // Spawn enemy every ~2 seconds
            if (random.nextInt(100) < 40) { // 40% chance
                spawnEnemyShip();
            }
            enemySpawnCounter = 0;
        }
        
        // Increase difficulty
        if (score > 0 && score % 200 == 0) {
            difficulty = score / 200 + 1;
        }
        
        // Update achievement progress during gameplay
        if (achievementManager != null) {
            achievementManager.setProgress(Achievement.AchievementType.SCORE, score);
            achievementManager.setProgress(Achievement.AchievementType.SURVIVAL, difficulty);
        }
    }
    
    private void updateAchievementNotifications() {
        // Check for new achievement unlocks and create notifications
        if (achievementManager != null) {
            java.util.List<Achievement> recentUnlocks = achievementManager.getRecentUnlocks();
            if (recentUnlocks.size() > 0) {
                System.out.println("Found " + recentUnlocks.size() + " recent unlocks");
            }
            for (Achievement achievement : recentUnlocks) {
                AchievementNotification notification = new AchievementNotification(achievement);
                achievementNotifications.add(notification);
                System.out.println("Achievement unlocked: " + achievement.getName());
                System.out.println("Created notification. Total notifications: " + achievementNotifications.size());
            }
        } else {
            System.out.println("Achievement manager is null!");
        }
        
        // Update achievement notifications
        Iterator<AchievementNotification> notificationIter = achievementNotifications.iterator();
        while (notificationIter.hasNext()) {
            AchievementNotification notification = notificationIter.next();
            notification.update();
            if (notification.isFinished()) {
                notificationIter.remove();
            }
        }
        
        // Adjust notification positions to stack properly with tighter spacing
        for (int i = 0; i < achievementNotifications.size(); i++) {
            achievementNotifications.get(i).setYOffset(i * 57); // 75 * 0.75 = ~57
        }
    }
    
    private void spawnAsteroid() {
        int x = random.nextInt(WIDTH - 30);
        int size = 20 + random.nextInt(30);
        double speed = 2 + difficulty * 0.5 + random.nextDouble() * 2;
        asteroids.add(new Asteroid(x, -size, size, speed));
    }
    
    private void spawnBossAsteroid() {
        int x = random.nextInt(WIDTH - 100);
        int size = 60 + random.nextInt(40); // Larger than regular asteroids
        double speed = 1 + difficulty * 0.3 + random.nextDouble(); // Slower but more menacing
        asteroids.add(new BossAsteroid(x, -size, size, speed));
    }
    
    private void spawnPowerUp() {
        int x = random.nextInt(WIDTH - 30);
        powerUps.add(new PowerUp(x, -30, PowerUp.PowerUpType.BLASTER));
    }
    
    private void spawnEnemyShip() {
        int x = random.nextInt(WIDTH - 60);
        String[] enemyTypes = SpriteLoader.getAvailableEnemyShips();
        String enemyType = enemyTypes[random.nextInt(enemyTypes.length)];
        enemyShips.add(new EnemyShip(x, -40, enemyType));
    }
    
    private boolean checkCollision(Player player, Asteroid asteroid) {
        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), 
                                               player.getWidth(), player.getHeight());
        Rectangle asteroidBounds = new Rectangle(asteroid.getX(), asteroid.getY(),
                                                 asteroid.getSize(), asteroid.getSize());
        return playerBounds.intersects(asteroidBounds);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (currentState == GameState.MENU) {
            shipMenu.keyPressed(e);
        } else if (currentState == GameState.ACHIEVEMENTS) {
            achievementViewer.keyPressed(e);
        } else if (currentState == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) {
                player.setMovingLeft(true);
            } else if (key == KeyEvent.VK_RIGHT) {
                player.setMovingRight(true);
            } else if (key == KeyEvent.VK_SPACE) {
                shootingPressed = true;
            } else if (key == KeyEvent.VK_ESCAPE) {
                currentState = GameState.PAUSED;
            }
        } else if (currentState == GameState.PAUSED) {
            if (key == KeyEvent.VK_ESCAPE) {
                currentState = GameState.PLAYING;
            } else if (key == KeyEvent.VK_M) {
                initMenu();
            }
        } else if (currentState == GameState.GAME_OVER) {
            if (key == KeyEvent.VK_SPACE) {
                initMenu();
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (currentState == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) {
                player.setMovingLeft(false);
            } else if (key == KeyEvent.VK_RIGHT) {
                player.setMovingRight(false);
            } else if (key == KeyEvent.VK_SPACE) {
                shootingPressed = false;
            }
        } else if (currentState == GameState.PAUSED) {
            // Stop any movement when game is paused
            if (key == KeyEvent.VK_LEFT) {
                player.setMovingLeft(false);
            } else if (key == KeyEvent.VK_RIGHT) {
                player.setMovingRight(false);
            } else if (key == KeyEvent.VK_SPACE) {
                shootingPressed = false;
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Dodger");
        SpaceDodger game = new SpaceDodger();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}