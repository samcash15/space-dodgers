import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {
    private static final Map<String, BufferedImage> sprites = new HashMap<>();
    private static final String RESOURCES_PATH = "resources/sprites/";
    
    public static BufferedImage loadSprite(String category, String filename) {
        String key = category + "/" + filename;
        
        if (sprites.containsKey(key)) {
            return sprites.get(key);
        }
        
        try {
            // Try multiple possible paths
            String[] possiblePaths = {
                RESOURCES_PATH + category + "/" + filename,
                "ai-java-game/" + RESOURCES_PATH + category + "/" + filename,
                "../" + RESOURCES_PATH + category + "/" + filename,
                "./" + RESOURCES_PATH + category + "/" + filename
            };
            
            BufferedImage sprite = null;
            IOException lastException = null;
            
            for (String path : possiblePaths) {
                try {
                    sprite = ImageIO.read(new File(path));
                    break;
                } catch (IOException e) {
                    lastException = e;
                    // Continue to next path
                }
            }
            
            if (sprite != null) {
                sprites.put(key, sprite);
                return sprite;
            } else {
                if (lastException != null) {
                    lastException.printStackTrace();
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static BufferedImage getPlayerShip(String shipType) {
        return loadSprite("player", shipType + ".png");
    }
    
    public static BufferedImage getEnemyShip(String shipType) {
        return loadSprite("enemy_ships", shipType + ".png");
    }
    
    public static BufferedImage getAsteroid(String asteroidType) {
        return loadSprite("asteroids", asteroidType + ".png");
    }
    
    public static BufferedImage getBullet(String bulletType) {
        return loadSprite("bullets", bulletType + ".png");
    }
    
    public static BufferedImage getHealthBar(String barType) {
        return loadSprite("health_bars", barType + ".png");
    }
    
    public static BufferedImage getThruster(String thrusterFrame) {
        return loadSprite("thrusters", thrusterFrame + ".png");
    }
    
    public static BufferedImage getBackground(String backgroundType) {
        return loadSprite("background", backgroundType + ".png");
    }
    
    // Get all available player ships
    public static String[] getAvailablePlayerShips() {
        return new String[]{
            "pixel_ship",
            "pixel_ship_blue", 
            "pixel_ship_red",
            "pixel_ship_yellow"
        };
    }
    
    // Get all available enemy ships
    public static String[] getAvailableEnemyShips() {
        return new String[]{
            "pixel_shape3_blue",
            "pixel_ship3_green",
            "pixel_ship3_red", 
            "pixel_ship3_yellow",
            "pixel_ship_blue_small",
            "pixel_ship_green_small_2",
            "pixel_ship_red_small_2"
        };
    }
    
    // Get all available main boss stations
    public static String[] getAvailableMainBossStations() {
        return new String[]{
            "pixel_station_blue",
            "pixel_station_green",
            "pixel_station_red",
            "pixel_station_yellow"
        };
    }
    
    // Get main boss station sprite
    public static BufferedImage getMainBossStation(String stationType) {
        return loadSprite("enemy_ships", stationType + ".png");
    }
    
    // Get all available asteroids
    public static String[] getAvailableAsteroids() {
        return new String[]{
            "asteroid_grey",
            "asteroid_grey_tiny",
            "asteroid_tiny",
            "pixel_asteroid"
        };
    }
}