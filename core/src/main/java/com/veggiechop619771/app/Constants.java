package com.veggiechop619771.app;

public class Constants {

    // World dimensions
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // HUD / layout
    public static final float HUD_HEIGHT        = 80f;
    public static final float FOOTER_HEIGHT      = 50f;
    public static final float GAMEPLAY_X         = 50f;
    public static final float GAMEPLAY_Y         = FOOTER_HEIGHT;
    public static final float GAMEPLAY_WIDTH     = 380f;
    public static final float GAMEPLAY_HEIGHT    = 600f;

    // Button sizes
    public static final float BTN_WIDTH_MAIN     = 240f;
    public static final float BTN_HEIGHT_MAIN    = 70f;
    public static final float BTN_WIDTH_SEC      = 200f;
    public static final float BTN_HEIGHT_SEC     = 60f;
    public static final float BTN_RADIUS         = 18f;
    public static final float BTN_SPACING        = 20f;

    // Kitchen selection cards
    public static final float CARD_WIDTH         = 140f;
    public static final float CARD_HEIGHT        = 180f;

    // Veggie / bomb spawning
    public static final float SPAWN_INTERVAL_MIN = 0.6f;
    public static final float SPAWN_INTERVAL_MAX = 1.4f;
    public static final float VEGGIE_SPEED_MIN   = 200f;
    public static final float VEGGIE_SPEED_MAX   = 450f;
    public static final float BOMB_CHANCE        = 0.15f;  // 15% of spawns are bombs
    public static final float VEGGIE_SIZE        = 64f;
    public static final float BOMB_SIZE          = 64f;

    // Gameplay / scoring
    public static final int   SCORE_PER_VEGGIE   = 10;
    public static final int   COMBO_BONUS_BASE   = 50;
    public static final int   COMBO_THRESHOLD    = 3;   // chain length to trigger combo result
    public static final float COMBO_DISPLAY_TIME = 3f;  // seconds before auto-dismiss
    public static final int   LIVES              = 3;   // missed veggies before game over

    // Difficulty multipliers
    public static final float DIFF_EASY_SPEED    = 0.75f;
    public static final float DIFF_NORMAL_SPEED  = 1.00f;
    public static final float DIFF_HARD_SPEED    = 1.40f;

    // Leaderboard
    public static final int   LEADERBOARD_SIZE   = 10;

    // Font sizes
    public static final int   FONT_SIZE_TITLE    = 56;
    public static final int   FONT_SIZE_HEADER   = 40;
    public static final int   FONT_SIZE_BODY      = 28;
    public static final int   FONT_SIZE_SMALL    = 18;
    public static final int   FONT_SIZE_SCORE    = 48;

    // UI colors (packed RGBA8888 for ShapeRenderer, stored as float[] for convenience)
    // Use Color constants in code; these document the palette.
    // Vibrant Green  #2ECC71
    // Warm Orange    #FF8C42
    // Soft Cream     #FFF8E7
    // Deep Navy      #1A2341
    // Gold           #FFD700
    // Coral Red      #FF6B6B

    // Tutorial
    public static final int   TUTORIAL_STEPS     = 4;

    // SharedPreferences keys
    public static final String PREF_FILE         = "GamePrefs";
    public static final String PREF_MUSIC        = "musicEnabled";
    public static final String PREF_SFX          = "sfxEnabled";
    public static final String PREF_HIGH_SCORE   = "highScore";
    public static final String PREF_DIFFICULTY   = "difficulty";  // 0=Easy 1=Normal 2=Hard
    public static final String PREF_SKIN         = "skin";
    public static final String PREF_COINS        = "coins";

    // Leaderboard score keys  score_0 … score_9
    public static final String PREF_SCORE_PREFIX = "score_";

    // Difficulty values
    public static final int DIFF_EASY   = 0;
    public static final int DIFF_NORMAL = 1;
    public static final int DIFF_HARD   = 2;

    // Kitchen IDs
    public static final int KITCHEN_PIZZERIA = 0;
    public static final int KITCHEN_SUSHI    = 1;
    public static final int KITCHEN_CANTINA  = 2;
}
