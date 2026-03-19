# Veggie Chop — Figma AI Design Brief

---

## 1. Art Style & Color Palette

**Art Style:** Bright, cheerful 2D cartoon with rounded, organic shapes and soft edges. Vegetables and kitchen elements feature exaggerated proportions and expressive details (eyes, highlights). All UI follows a playful, hand-drawn aesthetic with slight texture overlays to avoid sterility.

**Primary Color Palette:**
- **Vibrant Green (#2ECC71)** — dominant accent for positive actions, sliced veggie feedback, combo highlights
- **Warm Orange (#FF8C42)** — secondary accent for danger warnings, bombs, energy
- **Soft Cream (#FFF8E7)** — neutral background and card surfaces
- **Deep Navy (#1A2341)** — text, shadows, structural elements

**Accent Colors:**
- **Gold (#FFD700)** — score bonuses, leaderboard ranks, celebration effects
- **Coral Red (#FF6B6B)** — game-over state, bomb hazard indicators

**Typography Mood:** Rounded, friendly sans-serif (weight: semi-bold for headers, regular for body). Convey playfulness and clarity—no serif fonts. Letter spacing slightly generous for legibility on mobile.

---

## 2. App Icon — icon_512.png (512×512px)

**Background:** Radial gradient from warm golden-yellow (#FFD700) at center to vibrant orange (#FF8C42) at edges, creating a sun-like kitchen warmth effect.

**Central Symbol:** A stylized cutting board (cream #FFF8E7) rotated 15° counterclockwise, with a chef's knife (silver with soft shadow) diagonally overlaid. Atop the board, a dynamic arrangement of three cartoon vegetables—a tomato (red, glossy), a cucumber (green, with soft highlight), and a jalapeño (green with orange gradient)—positioned as if mid-slice, with subtle motion lines trailing behind them.

**Effects:** Soft outer glow (orange #FF8C42, 8px blur) around the entire composition; inner shadow on the cutting board for depth; small sparkle particles (gold #FFD700) scattered around the vegetables to suggest energy and excitement.

**Overall Mood:** Energetic, inviting, immediately communicates slashing/chopping action and food preparation. Professional enough for app store, playful enough for casual audiences.

---

## 3. UI Screens (480×854 portrait)

### MainMenuScreen
**Background:** Soft cream (#FFF8E7) with a subtle repeating pattern of small kitchen utensils (forks, spoons) in very light gray (#F0F0F0) scattered across. A wavy horizontal divider (green #2ECC71) sits at 60% height, suggesting a counter edge.

**Header:** Large, bold title "VEGGIE CHOP" centered at top (60px from top), color deep navy (#1A2341), with a subtle drop shadow. Beneath it, a single-line tagline "Slice. Combo. Score." in smaller regular weight, gray (#757575).

**Buttons:** Four primary buttons stacked vertically in the center region:
- "PLAY" (warm orange #FF8C42, center, 25% from top)
- "LEADERBOARD" (vibrant green #2ECC71, center, below Play)
- "TUTORIAL" (gold #FFD700, center, below Leaderboard)
- "SETTINGS" (deep navy #1A2341 text, lighter background, center, below Tutorial)

All buttons are rounded rectangles, horizontally centered, with consistent padding. Bottom-right corner: small version credit text (8pt gray #999999).

### KitchenSelectScreen
**Background:** Cream (#FFF8E7) with three vertical sections, each a distinct kitchen theme color bleeding into the background (Italian: red tint, Japanese: indigo tint, Mexican: warm red tint).

**Header:** "CHOOSE YOUR KITCHEN" centered at top (50px), navy (#1A2341), bold weight.

**Kitchen Selection Cards:** Three equal-width cards stacked vertically (or side-by-side if landscape), each 140×180px, displaying:
- Large, colorful illustration of the kitchen (Pizzeria oven, Sushi counter, Cantina tavern) centered in the card
- Kitchen name below illustration in semi-bold navy (#1A2341)
- Subtle shadow/elevation effect on card borders
- When hovered/tapped, card shows a subtle glow (green #2ECC71)

Bottom center: "BACK" button (navy text, light background) returns to MainMenuScreen.

### PizzeriaScreen
**Background:** Warm rustic terracotta (#C1440E) with a subtle brick texture overlay. Top 80px shows a pale cream (#FFF8E7) header zone with a darker separator line.

**HUD Elements (top-left to top-right):** Score display ("SCORE: 0") in bold gold (#FFD700), Timer/"Combo Meter" display in orange (#FF8C42) on top-right.

**Gameplay Area:** Central 380×600px region (below HUD) where vegetables and bombs spawn and travel downward. Clear boundary with a soft shadow effect marking the "active zone."

**Bottom Bar:** Thin cream (#FFF8E7) footer (50px) displaying current combo count ("COMBO: 0") centered in orange (#FF8C42). Left bottom corner: pause button (standard icon style, navy). Right bottom corner: mute toggle (standard icon style, navy).

**Vegetables:** Tomato (vibrant red, glossy), Basil leaf (bright green), Garlic clove (cream with brown spots), Mozzarella ball (soft white, subtle shadow).

**Bombs:** Frying pan (dark gray with orange handle), Rolling pin (brown wood texture with cream grip).

### SushiScreen
**Background:** Deep navy-blue (#1A2341) with subtle wave patterns in lighter navy (#2E3E5C), evoking water. Top 80px header zone in pale cream (#FFF8E7) with dark separator.

**HUD Elements (top-left to top-right):** Score display in gold (#FFD700), Combo meter in soft teal/cyan accent (complementary to navy).

**Gameplay Area:** Central 380×600px region with a subtle grid pattern (very faint lines, navy #2E3E5C) suggesting a sushi mat or preparation surface.

**Bottom Bar:** Thin cream (#FFF8E7) footer with combo count in teal. Pause and mute buttons (navy) in bottom corners.

**Vegetables:** Cucumber (bright green, segmented), Carrot (orange with gradient), Avocado (pale green, halved), Ginger root (tan with texture). All drawn with Japanese-inspired simple, clean lines.

**Bombs:** Same frying pan and rolling pin, consistent across all kitchens.

### CantinaScreen
**Background:** Warm sandy beige (#E8D4B8) with a woven texture overlay, suggesting a desert tavern. Top 80px header zone in burnt orange (#C1440E) with cream separator.

**HUD Elements (top-left to top-right):** Score in gold (#FFD700), Combo meter in coral red (#FF6B6B).

**Gameplay Area:** Central 380×600px with subtle clay-pot texture background, warm and inviting.

**Bottom Bar:** Thin cream (#FFF8E7) footer with combo count in coral red (#FF6B6B). Pause and mute buttons (deep navy) in bottom corners.

**Vegetables:** Jalapeño (bright green with orange gradient tip), Cilantro leaf (light green, feathery), Lime (pale green, circular), Onion (white with purple layers). Mexican-inspired, earthy aesthetic.

**Bombs:** Same frying pan and rolling pin.

### ComboResultScreen
**Background:** Semi-transparent overlay (black #000000, 60% opacity) covering the active kitchen gameplay screen behind it.

**Popup Card:** Centered white card (320×380px) with rounded corners and soft shadow. Title "COMBO!" in large, bold, vibrant green (#2ECC71) at top.

**Content:** Combo chain number displayed in massive gold (#FFD700) text (e.g., "3-CHAIN", "5-CHAIN"), centered. Below, bonus points text ("+ 500 PTS") in orange (#FF8C42), semi-bold. Small celebratory particle effects (stars, sparkles) animate around the card edges.

**Button:** Single "CONTINUE" button (green #2ECC71, center, bottom of card) dismisses the popup and returns to active gameplay.

**Duration:** Auto-dismisses after 3 seconds if no interaction.

### GameOverScreen
**Background:** Gradient from deep navy (#1A2341) at top to coral red (#FF6B6B) at bottom, creating a sunset/end-game mood.

**Header:** "GAME OVER" centered at top (80px), white text, extra-bold weight.

**Score Display:** "FINAL SCORE: [score]" centered in very large gold (#FFD700) text. Below, smaller text "Best Combo: [combo]" in cream (#FFF8E7).

**Kitchen Indicator:** Small icon or name of the kitchen played (e.g., "Pizzeria") in light gray (#CCCCCC).

**Buttons:** Two primary buttons centered, stacked vertically:
- "PLAY AGAIN" (warm orange #FF8C42, top)
- "BACK TO MENU" (green #2ECC71, bottom)

Both buttons are rounded rectangles with consistent padding and spacing.

### LeaderboardScreen
**Background:** Cream (#FFF8E7) with a subtle vertical stripe pattern in very light gray (#F0F0F0).

**Header:** "LEADERBOARD" centered at top (60px), navy (#1A2341), bold weight. Subtext "Top 10 Scores" in regular gray (#757575).

**Leaderboard List:** Centered list (320×500px) displaying rankings:
- Rank number (gold #FFD700, left-aligned)
- Player name or "Player" (navy #1A2341, semi-bold)
- Score (orange #FF8C42, right-aligned)
- Each row separated by a faint line (light gray #E0E0E0)
- Top 3 rows have subtle gold/silver/bronze background tint (very light, 5% opacity)

**Filter Buttons (optional):** Three small toggle buttons near the top-right to filter by kitchen (Italian, Japanese, Mexican) — green #2ECC71 when active, gray when inactive.

**Button:** Single "BACK" button (navy text on light background) centered at bottom, returns to MainMenuScreen.

### SettingsScreen
**Background:** Cream (#FFF8E7) with subtle horizontal divider lines (light gray #E0E0E0) separating sections.

**Header:** "SETTINGS" centered at top (60px), navy (#1A2341), bold weight.

**Setting Groups:** Vertically stacked sections, each with a label (semi-bold navy #1A2341) and a toggle or selector:

1. **Sound Toggle** — Label "Sound" (left), toggle switch (right, green #2ECC71 when on, gray when off)
2. **Music Toggle** — Label "Music" (left), toggle switch (right, same styling)
3. **Difficulty Selector** — Label "Difficulty" (left), three small buttons ("Easy", "Normal", "Hard") below, active one highlighted in green (#2ECC71)
4. **Data Reset** — Label "Reset Data" (left), small warning text below ("This cannot be undone"), red button "RESET" (coral red #FF6B6B)
5. **Credits** — Label "Credits" (left), small text link "View" (green #2ECC71) that opens a simple popup with game team names

**Button:** "BACK" button (navy text, light background) centered at bottom, returns to MainMenuScreen.

### TutorialScreen
**Background:** Cream (#FFF8E7) with a subtle repeating veggie icon watermark (very light gray #F5F5F5) filling the background.

**Header:** "HOW TO PLAY" centered at top (60px), navy (#1A2341), bold weight.

**Tutorial Sections:** Scrollable or paginated content (depending on implementation), each section containing:
- **Step Title** (semi-bold navy #1A2341, e.g., "Step 1: Swipe to Slice")
- **Illustration or Animation** (centered, 200×150px mockup of the mechanic)
- **Description Text** (regular gray #555555, 2–3 lines, clear and concise)
- **Progress Indicator** (small dots at bottom showing current page, green #2ECC71 for active, gray for inactive)

**Key Sections:**
1. Swipe mechanic with hand illustration
2. Combo explanation with veggie chain illustration
3. Bomb hazards (frying pan and rolling pin) with warning styling
4. Scoring and point multipliers

**Buttons:** 
- "PREV" button (bottom-left, navy text, inactive if on first step)
- "NEXT" button (bottom-right, green #2ECC71, inactive if on last step)
- "SKIP" button (top-right, small text link, gray #999999)

Returns to MainMenuScreen on completion.

---

## 4. Export Checklist

- icon_512.png (512×512)
- ui/mainmenu_bg.png (480×854)
- ui/kitchenselect_screen.png (480×854)
- ui/pizzeria_screen.png (480×854)
- ui/sushi_screen.png (480×854)
- ui/cantina_screen.png (480×854)
- ui/comboresult_popup.png (320×380)
- ui/gameover_screen.png (480×854)
- ui/leaderboard_screen.png (480×854)
- ui/settings_screen.png (480×854)
- ui/tutorial_screen_step1.png (480×854)
- ui/tutorial_screen_step2.png (480×854)
- ui/tutorial_screen_step3.png (480×854)
- ui/tutorial_screen_step4.png (480×854)

---

**END OF BRIEF**
