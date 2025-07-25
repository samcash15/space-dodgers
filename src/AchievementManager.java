import java.util.*;
import java.io.*;

public class AchievementManager {
    private Map<String, Achievement> achievements;
    private List<Achievement> recentUnlocks;
    private static final String SAVE_FILE = "achievements.dat";
    
    public AchievementManager() {
        achievements = new HashMap<>();
        recentUnlocks = new ArrayList<>();
        initializeAchievements();
        loadAchievements();
    }
    
    private void initializeAchievements() {
        // Score-based achievements
        addAchievement("game_start", "Space Explorer", "Start your first game", Achievement.AchievementType.SCORE, 10);
        addAchievement("first_blood", "First Blood", "Score your first 100 points", Achievement.AchievementType.SCORE, 100);
        addAchievement("getting_started", "Getting Started", "Score 500 points", Achievement.AchievementType.SCORE, 500);
        addAchievement("ace_pilot", "Ace Pilot", "Score 1,000 points", Achievement.AchievementType.SCORE, 1000);
        addAchievement("space_legend", "Space Legend", "Score 5,000 points", Achievement.AchievementType.SCORE, 5000);
        addAchievement("galaxy_master", "Galaxy Master", "Score 10,000 points", Achievement.AchievementType.SCORE, 10000);
        
        // Enemy ship kill achievements
        addAchievement("first_kill", "First Kill", "Destroy your first enemy ship", Achievement.AchievementType.ENEMY_KILLS, 1);
        addAchievement("ship_hunter", "Ship Hunter", "Destroy 10 enemy ships", Achievement.AchievementType.ENEMY_KILLS, 10);
        addAchievement("fleet_destroyer", "Fleet Destroyer", "Destroy 25 enemy ships", Achievement.AchievementType.ENEMY_KILLS, 25);
        addAchievement("ace_pilot_kills", "Ace Pilot", "Destroy 50 enemy ships", Achievement.AchievementType.ENEMY_KILLS, 50);
        
        // Asteroid destruction achievements
        addAchievement("rock_breaker", "Rock Breaker", "Destroy 10 asteroids", Achievement.AchievementType.ASTEROID_DESTROYED, 10);
        addAchievement("asteroid_hunter", "Asteroid Hunter", "Destroy 50 asteroids", Achievement.AchievementType.ASTEROID_DESTROYED, 50);
        addAchievement("space_cleaner", "Space Cleaner", "Destroy 100 asteroids", Achievement.AchievementType.ASTEROID_DESTROYED, 100);
        addAchievement("annihilator", "Annihilator", "Destroy 500 asteroids", Achievement.AchievementType.ASTEROID_DESTROYED, 500);
        
        // Boss achievements
        addAchievement("boss_slayer", "Boss Slayer", "Destroy your first boss asteroid", Achievement.AchievementType.BOSS_KILLS, 1);
        addAchievement("titan_killer", "Titan Killer", "Destroy 5 boss asteroids", Achievement.AchievementType.BOSS_KILLS, 5);
        addAchievement("boss_master", "Boss Master", "Destroy 25 boss asteroids", Achievement.AchievementType.BOSS_KILLS, 25);
        
        // Main Boss achievements
        addAchievement("station_destroyer", "Station Destroyer", "Destroy your first main boss station", Achievement.AchievementType.MAIN_BOSS_KILLS, 1);
        addAchievement("station_hunter", "Station Hunter", "Destroy 3 main boss stations", Achievement.AchievementType.MAIN_BOSS_KILLS, 3);
        addAchievement("galactic_commander", "Galactic Commander", "Destroy 10 main boss stations", Achievement.AchievementType.MAIN_BOSS_KILLS, 10);
        
        // Survival achievements
        addAchievement("survivor", "Survivor", "Reach level 5", Achievement.AchievementType.SURVIVAL, 5);
        addAchievement("veteran", "Veteran", "Reach level 10", Achievement.AchievementType.SURVIVAL, 10);
        addAchievement("elite_pilot", "Elite Pilot", "Reach level 15", Achievement.AchievementType.SURVIVAL, 15);
        addAchievement("legendary", "Legendary", "Reach level 25", Achievement.AchievementType.SURVIVAL, 25);
        
        // Damage achievements
        addAchievement("iron_hull", "Iron Hull", "Take 500 total damage", Achievement.AchievementType.DAMAGE_TAKEN, 500);
        addAchievement("battle_scarred", "Battle Scarred", "Take 1,000 total damage", Achievement.AchievementType.DAMAGE_TAKEN, 1000);
        
        // Combat achievements
        addAchievement("first_shot", "First Shot", "Fire your first shot", Achievement.AchievementType.SHOTS_FIRED, 1);
        addAchievement("trigger_happy", "Trigger Happy", "Fire 100 shots", Achievement.AchievementType.SHOTS_FIRED, 100);
        addAchievement("gunslinger", "Gunslinger", "Fire 500 shots", Achievement.AchievementType.SHOTS_FIRED, 500);
        addAchievement("weapons_master", "Weapons Master", "Fire 1,000 shots", Achievement.AchievementType.SHOTS_FIRED, 1000);
        
        // Power-up achievements
        addAchievement("first_powerup", "Power Hungry", "Collect your first power-up", Achievement.AchievementType.POWERUPS_COLLECTED, 1);
        addAchievement("collector", "Collector", "Collect 10 power-ups", Achievement.AchievementType.POWERUPS_COLLECTED, 10);
        addAchievement("power_addict", "Power Addict", "Collect 25 power-ups", Achievement.AchievementType.POWERUPS_COLLECTED, 25);
        addAchievement("power_master", "Power Master", "Collect 50 power-ups", Achievement.AchievementType.POWERUPS_COLLECTED, 50);
        
        // Health achievements
        addAchievement("first_heal", "First Aid", "Heal for the first time", Achievement.AchievementType.HEALTH_HEALED, 1);
        addAchievement("medic", "Field Medic", "Heal 500 total health", Achievement.AchievementType.HEALTH_HEALED, 500);
        addAchievement("doctor", "Combat Doctor", "Heal 1,000 total health", Achievement.AchievementType.HEALTH_HEALED, 1000);
        addAchievement("miracle_worker", "Miracle Worker", "Heal 2,500 total health", Achievement.AchievementType.HEALTH_HEALED, 2500);
        
        // Burst mode achievements
        addAchievement("burst_novice", "Burst Novice", "Get 10 kills in burst mode", Achievement.AchievementType.BURST_KILLS, 10);
        addAchievement("burst_expert", "Burst Expert", "Get 50 kills in burst mode", Achievement.AchievementType.BURST_KILLS, 50);
        addAchievement("burst_master", "Burst Master", "Get 100 kills in burst mode", Achievement.AchievementType.BURST_KILLS, 100);
        
        // Survival time achievements
        addAchievement("untouchable_1", "Untouchable", "Survive 30 seconds without damage", Achievement.AchievementType.NO_DAMAGE_TIME, 30);
        addAchievement("untouchable_2", "Evasion Expert", "Survive 60 seconds without damage", Achievement.AchievementType.NO_DAMAGE_TIME, 60);
        addAchievement("untouchable_3", "Ghost Pilot", "Survive 120 seconds without damage", Achievement.AchievementType.NO_DAMAGE_TIME, 120);
        
        // Close call achievements
        addAchievement("lucky_1", "Lucky", "Survive with less than 25 health 5 times", Achievement.AchievementType.CLOSE_CALLS, 5);
        addAchievement("lucky_2", "Cat Lives", "Survive with less than 25 health 10 times", Achievement.AchievementType.CLOSE_CALLS, 10);
        addAchievement("lucky_3", "Death Defier", "Survive with less than 25 health 25 times", Achievement.AchievementType.CLOSE_CALLS, 25);
        
        // Special achievements
        addAchievement("pacifist", "Pacifist Run", "Reach level 3 without firing a single shot", Achievement.AchievementType.PACIFIST_RUN, 3);
        addAchievement("perfect_level", "Perfect Level", "Complete a level without taking damage", Achievement.AchievementType.SURVIVAL, 1);
    }
    
    private void addAchievement(String id, String name, String description, Achievement.AchievementType type, int targetValue) {
        achievements.put(id, new Achievement(id, name, description, type, targetValue));
    }
    
    public void updateProgress(Achievement.AchievementType type, int value) {
        System.out.println("Updating progress for " + type + " by " + value);
        boolean hasNewUnlock = false;
        for (Achievement achievement : achievements.values()) {
            if (achievement.getType() == type && !achievement.isUnlocked()) {
                System.out.println("Checking achievement: " + achievement.getName() + " (" + achievement.getCurrentValue() + "/" + achievement.getTargetValue() + ")");
                achievement.updateProgress(value);
                if (achievement.checkUnlock()) {
                    System.out.println("Achievement unlocked: " + achievement.getName());
                    recentUnlocks.add(achievement);
                    hasNewUnlock = true;
                }
            }
        }
        // Only save when there's a new unlock to reduce file I/O
        if (hasNewUnlock) {
            saveAchievements();
        }
    }
    
    public void setProgress(Achievement.AchievementType type, int value) {
        boolean hasNewUnlock = false;
        for (Achievement achievement : achievements.values()) {
            if (achievement.getType() == type && !achievement.isUnlocked()) {
                achievement.setProgress(value);
                if (achievement.checkUnlock()) {
                    recentUnlocks.add(achievement);
                    hasNewUnlock = true;
                }
            }
        }
        // Only save when there's a new unlock to reduce file I/O
        if (hasNewUnlock) {
            saveAchievements();
        }
    }
    
    public List<Achievement> getRecentUnlocks() {
        List<Achievement> result = new ArrayList<>(recentUnlocks);
        recentUnlocks.clear();
        return result;
    }
    
    public List<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements.values());
    }
    
    public List<Achievement> getUnlockedAchievements() {
        return achievements.values().stream()
                .filter(Achievement::isUnlocked)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public int getUnlockedCount() {
        return (int) achievements.values().stream().filter(Achievement::isUnlocked).count();
    }
    
    public int getTotalCount() {
        return achievements.size();
    }
    
    private void saveAchievements() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            Map<String, Object[]> saveData = new HashMap<>();
            for (Achievement achievement : achievements.values()) {
                saveData.put(achievement.getId(), new Object[]{
                    achievement.isUnlocked(),
                    achievement.getCurrentValue()
                });
            }
            oos.writeObject(saveData);
        } catch (IOException e) {
            System.err.println("Failed to save achievements: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadAchievements() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            Map<String, Object[]> saveData = (Map<String, Object[]>) ois.readObject();
            for (Map.Entry<String, Object[]> entry : saveData.entrySet()) {
                Achievement achievement = achievements.get(entry.getKey());
                if (achievement != null) {
                    Object[] data = entry.getValue();
                    achievement.setUnlocked((Boolean) data[0]);
                    achievement.setCurrentValue((Integer) data[1]);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // File doesn't exist or is corrupted, start fresh
            System.out.println("No existing achievements file found, starting fresh.");
        }
    }
    
    public void resetAllAchievements() {
        for (Achievement achievement : achievements.values()) {
            achievement.setUnlocked(false);
            achievement.setCurrentValue(0);
        }
        saveAchievements();
    }
}