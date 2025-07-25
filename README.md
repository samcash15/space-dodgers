# 🚀 Space Dodger Game 🌌

A thrilling 2D space dodging game built with Java Swing featuring sprite-based graphics and ship customization! 🎮

## 🎮 How to Play

- **Ship Selection**: Choose your ship from multiple colorful options in the main menu 🚁
- Use **LEFT** ⬅️ and **RIGHT** ➡️ arrow keys to move your spaceship 🛸
- **Health System**: You have 100 health points - survive multiple hits! ❤️
  - Regular asteroids deal 25 damage (1/4 health) ☄️
  - Boss asteroids cause instant death 💀☄️
  - Enemy ships and lasers deal 50 damage (1/2 health) 🚢💥
- Your ship starts with a blaster weapon - hold **SPACE** 🚀 to shoot continuously 🔫
- Dodge or destroy falling asteroids and enemy ships 💥
- **Enemy Ships**: Fight AI-controlled ships that shoot colored lasers matching their hull! 🛸⚔️
- Score points for:
  - Dodging asteroids: 10 points 🏃‍♂️
  - Shooting regular asteroids: 25 points 🎯
  - Destroying enemy ships: 30-75 points (based on type) 🎯
  - Destroying boss asteroids: 100 points 💀
  - Collecting power-ups: 50 points ✨
- The game gets progressively harder as your score increases 📈
- Press **ESC** to pause/resume game ⏸️
- Press **A** in main menu to view achievements 🏆
- Press **SPACE** to restart after game over 🔄

## ⚙️ How to Run

### Option 1: Easy Launch (Recommended) 🚀
1. Make sure you have Java installed (Java 8 or higher) ☕
2. Run the setup (first time only): 📁
   ```bash
   bash setup.sh
   ```
3. Launch the game: 🎮
   ```bash
   ./run.sh
   ```

### Option 2: Alternative Launch 🎮
```bash
bash start.sh
```

### Option 3: Manual Launch 🔧
1. Navigate to the `src` directory 📁
2. Compile the game:
   ```bash
   javac *.java
   ```
3. Run the game:
   ```bash
   java SpaceDodger
   ```

### 🛠️ Troubleshooting
If you get "Permission denied" when running `./run.sh`, run:
```bash
chmod +x run.sh
```

## ✨ Game Features

- **Sprite-Based Graphics**: Professional pixel art assets for all game elements 🎨
- **Ship Selection Menu**: Choose from multiple player ships with unique designs 🚁
- **Achievement System**: Track your progress with unlockable achievements 🏆
  - Visual notifications when achievements are unlocked 📢
  - Comprehensive achievement viewer with progress tracking 📊
  - Persistent progress saved between game sessions 💾
  - Separate tracking for enemy kills vs asteroid destruction 🎯
- **Health System**: Damage-based gameplay with visual health bars ❤️
- **Enemy AI**: Smart enemy ships with varied movement patterns and shooting 🤖
- **Colored Laser System**: Enemy ships shoot lasers matching their ship colors 🌈
- Smooth 60 FPS gameplay 🎯
- Progressive difficulty system 📊
- Score tracking 🏆
- **Boss Asteroids**: Large, multi-hit enemies with health bars - instant death on contact! 👹⚔️💀
- **Explosion Effects**: Particle-based explosions when enemies are destroyed 🎆
- **Dynamic Background**: Moving stars overlay on space background 🌟
- **Pause System**: Pause and resume gameplay anytime ⏸️
- Visual effects (animated thrusters 🔥, rotating asteroids ☄️, hit flash effects ⚡)
- Game over and restart functionality 🔄
- Responsive keyboard controls 🎮

## 🎮 Controls

### In-Game Controls
- **Arrow Keys** ⬅️➡️: Move spaceship left/right 🛸
- **Space** 🚀: Hold to shoot continuously 🔫💥
- **ESC**: Pause/resume game ⏸️

### Menu Controls
- **Left/Right Arrows** ⬅️➡️: Select ship in ship selection menu 🚁
- **Space** 🚀: Confirm ship selection / Restart after game over 🔄
- **A**: View achievements menu 🏆
- **ESC**: Return to main menu (from achievements) 🔙
- **Up/Down Arrows** ⬆️⬇️: Scroll through achievements list 📜

🌟 Enjoy playing Space Dodger! 🌟