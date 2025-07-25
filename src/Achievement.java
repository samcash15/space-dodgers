public class Achievement {
    private String id;
    private String name;
    private String description;
    private boolean unlocked;
    private int targetValue;
    private int currentValue;
    private AchievementType type;
    
    public enum AchievementType {
        SCORE, ENEMY_KILLS, ASTEROID_DESTROYED, SURVIVAL, BOSS_KILLS, DAMAGE_TAKEN, SHOTS_FIRED, ACCURACY, STREAK
    }
    
    public Achievement(String id, String name, String description, AchievementType type, int targetValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetValue = targetValue;
        this.currentValue = 0;
        this.unlocked = false;
    }
    
    public void updateProgress(int value) {
        if (!unlocked) {
            System.out.println("Updating " + name + ": " + currentValue + " + " + value + " = " + (currentValue + value) + "/" + targetValue);
            this.currentValue += value;
            // Don't set unlocked here - let checkUnlock() handle it
        }
    }
    
    public void setProgress(int value) {
        if (!unlocked) {
            this.currentValue = value;
            // Don't set unlocked here - let checkUnlock() handle it
        }
    }
    
    public boolean checkUnlock() {
        if (!unlocked && currentValue >= targetValue) {
            System.out.println("Achievement " + name + " is being unlocked! (" + currentValue + "/" + targetValue + ")");
            unlocked = true;
            return true; // Just unlocked
        }
        return false;
    }
    
    public double getProgressPercent() {
        return Math.min(1.0, (double) currentValue / targetValue);
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return unlocked; }
    public int getTargetValue() { return targetValue; }
    public int getCurrentValue() { return currentValue; }
    public AchievementType getType() { return type; }
    
    // For loading saved achievements
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
}