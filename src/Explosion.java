import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Explosion {
    private double x, y;
    private ArrayList<Particle> particles;
    private int lifespan;
    private boolean finished;
    private Random random;
    
    public Explosion(double x, double y, int size) {
        this.x = x;
        this.y = y;
        this.particles = new ArrayList<>();
        this.lifespan = 30;
        this.finished = false;
        this.random = new Random();
        
        // Create particles based on size
        int particleCount = size / 2;
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(x + size/2, y + size/2));
        }
    }
    
    public void update() {
        lifespan--;
        if (lifespan <= 0) {
            finished = true;
            return;
        }
        
        for (Particle p : particles) {
            p.update();
        }
    }
    
    public void draw(Graphics g) {
        for (Particle p : particles) {
            p.draw(g);
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    private class Particle {
        private double x, y;
        private double vx, vy;
        private Color color;
        private int size;
        private int life;
        
        public Particle(double x, double y) {
            this.x = x;
            this.y = y;
            
            // Random velocity
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 2 + random.nextDouble() * 4;
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed;
            
            // Random warm color (red, orange, yellow)
            int r = 200 + random.nextInt(56);
            int g = random.nextInt(200);
            int b = 0;
            this.color = new Color(r, g, b);
            
            this.size = 3 + random.nextInt(5);
            this.life = 20 + random.nextInt(10);
        }
        
        public void update() {
            x += vx;
            y += vy;
            vx *= 0.95; // Slow down
            vy *= 0.95;
            life--;
        }
        
        public void draw(Graphics g) {
            if (life <= 0) return;
            
            // Fade out based on life
            int alpha = Math.min(255, life * 12);
            Color fadeColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            g.setColor(fadeColor);
            
            int drawSize = (int)(size * (life / 30.0));
            g.fillOval((int)x - drawSize/2, (int)y - drawSize/2, drawSize, drawSize);
        }
    }
}