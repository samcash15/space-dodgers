import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SpaceDodger extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int DELAY = 16; // ~60 FPS
    
    private Timer timer;
    private Player player;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Bullet> bullets;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Explosion> explosions;
    private Random random;
    
    private int score;
    private int asteroidSpawnCounter;
    private int powerUpSpawnCounter;
    private int bossSpawnCounter;
    private int difficulty;
    private boolean gameRunning;
    private boolean gameOver;
    private boolean shootingPressed = false;
    
    public SpaceDodger() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        initGame();
    }
    
    private void initGame() {
        player = new Player(WIDTH / 2 - 25, HEIGHT - 100);
        asteroids = new ArrayList<>();
        bullets = new ArrayList<>();
        powerUps = new ArrayList<>();
        explosions = new ArrayList<>();
        random = new Random();
        
        score = 0;
        asteroidSpawnCounter = 0;
        powerUpSpawnCounter = 0;
        bossSpawnCounter = 0;
        difficulty = 1;
        gameRunning = true;
        gameOver = false;
        shootingPressed = false;
        
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (gameRunning && !gameOver) {
            drawGame(g);
        } else if (gameOver) {
            drawGameOver(g);
        }
    }
    
    private void drawGame(Graphics g) {
        // Draw stars background
        g.setColor(Color.WHITE);
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillRect(x, y, 2, 2);
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
        
        // Draw power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }
        
        // Draw explosions
        for (Explosion explosion : explosions) {
            explosion.draw(g);
        }
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("Level: " + difficulty, 10, 60);
        if (player.hasBlaster()) {
            g.setColor(Color.YELLOW);
            g.drawString("BLASTER ACTIVE", 10, 90);
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
    
    private void update() {
        if (!gameRunning || gameOver) return;
        
        // Update player
        player.update();
        
        // Handle shooting
        if (shootingPressed && player.canShoot()) {
            bullets.add(new Bullet(player.getX() + player.getWidth() / 2 - 2, player.getY()));
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
                        }
                        bulletIter.remove();
                        break;
                    } else {
                        // Regular asteroid
                        explosions.add(new Explosion(asteroid.getX(), asteroid.getY(), asteroid.getSize()));
                        asteroidIter.remove();
                        bulletIter.remove();
                        score += 25; // Extra points for shooting
                        break;
                    }
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
                gameOver = true;
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
        if (bossSpawnCounter >= 600 && difficulty >= 2) { // Spawn boss every ~10 seconds after level 2
            if (random.nextInt(100) < 20) { // 20% chance
                spawnBossAsteroid();
            }
            bossSpawnCounter = 0;
        }
        
        // Increase difficulty
        if (score > 0 && score % 200 == 0) {
            difficulty = score / 200 + 1;
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
        
        if (key == KeyEvent.VK_LEFT) {
            player.setMovingLeft(true);
        } else if (key == KeyEvent.VK_RIGHT) {
            player.setMovingRight(true);
        } else if (key == KeyEvent.VK_SPACE) {
            if (gameOver) {
                initGame();
            } else if (player.hasBlaster()) {
                shootingPressed = true;
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT) {
            player.setMovingLeft(false);
        } else if (key == KeyEvent.VK_RIGHT) {
            player.setMovingRight(false);
        } else if (key == KeyEvent.VK_SPACE) {
            shootingPressed = false;
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