# 🐘 This Is The Only Level (Java – OOP Platformer)

A Java remake of **“This Is The Only Level (2009)”** built with an Object-Oriented architecture.  
You play as a blue elephant and complete **five stages** with the **same-looking map**, but each stage introduces a **different gameplay twist** (inverted controls, constant jump, multi-press button, invisibility, etc.).

---

## 🎥 Gameplay Video

- ▶ YouTube: https://youtu.be/HFjE55S1BJ8

---

## 📄 Full Technical Report

📘 **report/ThisIsTheOnlyLevelGameReport.pdf**  
The report explains:
- Stage mechanics (what changes per stage)
- Player physics and gravity handling
- Collision detection + correction logic
- Door/button system
- UI interactions (Help/Restart/Reset)
- OOP class responsibilities (UML-style)

---

## 🎮 Game Goal & Rules

### Objective
Reach the **exit pipe** to finish each stage.

### Door & Button
The exit is blocked by a **door**.  
To open it, you must find and press a **button** (some stages require multiple presses).

### Death / Reset
- Touching **spikes** kills you instantly → stage restarts.
- The game tracks **total time** and **death count**.

---

## 🧩 Stage Mechanics (What Changes)

Even though the map layout is visually “the same”, each stage modifies a rule:

1. **Stage 1 – Normal**
   - Standard controls, standard gravity.
   - Find button → door opens → reach exit.

2. **Stage 2 – Inverted Controls**
   - Left/right controls are swapped.
   - Movement input is intentionally reversed while physics stays consistent.

3. **Stage 3 – Constant Jump + Stronger Physics**
   - The player **jumps constantly** when grounded.
   - Gravity and/or movement pacing are tuned for a more “bouncy” feel.
   - The jump is triggered automatically upon touching the ground.

4. **Stage 4 – Multi-Press Button**
   - You must press the button **five times** to unlock the door.
   - The game counts presses and only then starts opening the door.

5. **Stage 5 – “Do You Remember?” (Invisible World)**
   - Map elements become invisible (rendered white on white),
     except player + pipes.
   - Collisions still exist: platforms/spikes/buttons still work.

---

## ⌨️ Controls & UI

### Movement
- Uses **arrow keys** (actual key codes can vary by stage).

### UI Buttons (Mouse)
- **Help**: shows detailed hint text for the current stage.
- **Restart**: restarts the current stage (keeps you in the same stage).
- **Reset**: resets the entire game back to Stage 1.

### End Screen
- After completing all stages, a completion screen is shown.
- Keyboard options allow restarting or quitting.

---

## 🧠 Architecture (OOP Design)

This project uses **five main classes**, each owning a clear responsibility:

### `Main.java` (Entry Point)
- Creates 5 `Stage` objects with different parameters:
  - gravity
  - movement speed
  - key mappings
  - clue/help texts
- Adds them to `ArrayList<Stage>` and starts the game.

### `Game.java` (Main Loop + UI + Progression)
- Runs the main `play()` loop:
  - time tracking
  - stage transitions
  - reading input (keys + mouse)
  - drawing the screen
- Coordinates with `Map` to apply movement and detect stage completion.

### `Map.java` (World + Collisions + Door/Button Logic)
- Stores the stage environment:
  - obstacles
  - spikes
  - door
  - start & exit pipes
  - button and button-floor
- Core method: `movePlayer(direction)`
  - calculates next position
  - checks collisions
  - corrects location if needed
  - handles spikes (death + respawn)
  - handles door/button logic
  - triggers stage change when the exit pipe is reached

### `Player.java` (Player Physics + State)
- Player position, dimensions, vertical velocity affected by gravity
- `respawn()` resets position to start pipe center
- Direction/image changes depending on movement + stage rules (e.g., inverted stage)

### `Stage.java` (Stage Configuration)
- Encapsulates per-stage parameters:
  - gravity, velocityX, velocityY
  - key codes
  - clue/help text
  - stage color
- Generates randomized colors for stages.

---

## 🧮 Physics & Movement Dynamics (Detailed)

### 1) Next Position Prediction
Movement is computed using a **predict-next-position** approach:
- Horizontal motion:
  - When left/right pressed, `x` changes by `velocityX`.
- Vertical motion:
  - Jump sets an **upward** velocity (stage-configured).
  - While in air, gravity modifies the player's vertical velocity each frame.

Crucially:
- The system often computes a *candidate* next position first,
  then checks collisions, then corrects it before applying.

---

### 2) Gravity & “In Air” Detection
To decide whether gravity should apply:
- The engine checks whether the player is standing on top of any obstacle.
- If not supported, the player is considered airborne and gravity is applied.

This prevents “floating” and ensures consistent platformer behavior.

---

### 3) Collision Detection (Rectangles)
Obstacles are rectangles.
A collision occurs if the player's next bounding box overlaps an obstacle's bounds.

When a collision is detected:
- the game corrects the player's position so the player is pushed
  to the obstacle boundary rather than passing through it.

---

### 4) Collision Correction (Why it Works)
The engine corrects collisions based on which side the player is coming from:
- X-axis correction:
  - pushed to left/right edge of obstacle
- Y-axis correction:
  - pushed to top/bottom edge
  - vertical velocity is set to 0 when landing/hitting from below

This avoids penetration and stabilizes movement.

---

### 5) Corner-Collision Visual Consistency (Important Detail)
Rectangles may be stacked to form platforms.
In some cases, the player might “touch” a corner mathematically
even though visually it should feel like a flat surface.

So the map logic prioritizes “real surface” collisions:
- If two collisions are detected (side + corner),
  the engine ignores the fake corner reaction and resolves the surface collision instead.

Result: smoother, more intuitive platform movement.

---

### 6) Spikes, Death, and Respawn
Spikes are lethal:
- Touch spike → instant death
- The stage restarts:
  - player respawns
  - door resets
  - button resets
  - death counter increments

Spike rendering direction depends on which wall they're attached to
(ceiling/left/floor/right).

---

### 7) Door Opening (Animated)
The door opens **gradually**:
- Each frame reduces its height until fully opened.
This gives visible feedback and prevents instant teleports.

---

## Repository Structure

```bash
this-is-the-only-level/
├── .idea/
├── code/
│   ├── .idea/
│   ├── Main.java
│   ├── Game.java
│   ├── Map.java
│   ├── Player.java
│   └── Stage.java
├── misc/
├── out/
├── report/
│   └── ThisIsTheOnlyLevelGameReport.pdf
├── this-is-the-only-level.iml
└── README.md
```
---

## 🛠 How to Run

> Place all `.java` files in `code/` and keep assets inside `misc/`.

## Compilation and Execution

Compile the project:
```bash
javac *.java
```

Run with:
```bash
java Main
```
---

## 🎨 Assets

All visual assets used in the game are stored inside the `misc/` directory.

These assets include:
- Player sprites (blue elephant facing left and right)
- Spike images used for lethal obstacles
- Start and exit pipe graphics
- Door and button visuals
- UI-related images (icons, indicators, banners)

⚠️ **Important Notes**
- Asset file names are referenced directly inside the code.
- Renaming, removing, or relocating assets requires updating file paths in the source files.
- In the final stage (“Do You Remember?”), most assets are intentionally rendered invisible
  to support the memory-based gameplay mechanic.

---

## 📌 Repository Notice

This repository contains the **full implementation** of the project.  
It is made public **for educational and demonstration purposes only**.

---

## ⚠️ Usage and License

This project is provided **strictly for educational and demonstration purposes**.

- ❌ Reuse of the code for academic submissions is **not permitted**
- ❌ Modification and re-submission for course credit is **not allowed**
- ❌ Direct copying or plagiarism violates academic integrity policies
- ✅ The code may be reviewed to understand:
  - Object-Oriented Programming principles
  - Platformer physics and gravity handling
  - Collision detection and correction strategies
  - Stage-based gameplay design
  - Game loop and UI interaction logic

By accessing this repository, you agree to use the material **only as a learning reference**.

---

## 🏁 Final Remarks

This project demonstrates how a seemingly simple platformer can be turned into a
**robust object-oriented system** through careful design decisions.

Highlights of the implementation:
- Clear separation of responsibilities across classes
- Predict-then-correct movement model for stable physics
- Stage-dependent mechanics without duplicating level layouts
- Careful handling of corner cases in collision logic
- Tight integration between gameplay rules and code structure

For a complete understanding of the system:
- 📘 **Read the technical report**
- 🎥 **Watch the gameplay video**
- 🧠 **Explore the code class-by-class**

This repository is best treated as a **learning reference and design example** rather than
a reusable production-ready codebase.
