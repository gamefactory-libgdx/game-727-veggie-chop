package com.veggiechop619771.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.veggiechop619771.app.Constants;
import com.veggiechop619771.app.MainGame;
import com.veggiechop619771.app.UiFactory;

/**
 * Main gameplay screen for all 3 kitchens (Pizzeria / Sushi / Cantina).
 * Veggies and bombs fall from the top of the gameplay area. The player
 * swipes / taps to slice veggies and avoid bombs.
 */
public class GameScreen implements Screen {

    final MainGame game;
    final int      kitchenId;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;      // pause button only
    private ShapeRenderer      sr;

    // -------------------------------------------------------------------------
    // Asset paths
    // -------------------------------------------------------------------------

    private final String bgPath;

    // Available veggie sprites (round alien assets look like cartoon veggies)
    private static final String[] VEGGIE_SPRITES = {
        "sprites/object/alienGreen_round.png",
        "sprites/object/alienYellow_round.png",
        "sprites/object/alienBlue_round.png",
        "sprites/object/alienPink_round.png"
    };
    private static final String BOMB_SPRITE = "sprites/object/elementExplosive000.png";

    // -------------------------------------------------------------------------
    // Game state
    // -------------------------------------------------------------------------

    private int   score;
    private int   lives;
    private int   consecutiveSlices;
    private int   bestCombo;
    private float speedMultiplier;
    private float spawnTimer;
    private boolean gameOver;

    private final Array<VeggieObj> veggies = new Array<>();

    // -------------------------------------------------------------------------
    // HUD text
    // -------------------------------------------------------------------------

    private GlyphLayout scoreLayout;
    private GlyphLayout livesLayout;
    private GlyphLayout comboLayout;

    // -------------------------------------------------------------------------
    // Pause button geometry (bottom-left of footer)
    // -------------------------------------------------------------------------

    private static final float PAUSE_X = 10f;
    private static final float PAUSE_Y = 8f;
    private static final float PAUSE_W = 80f;
    private static final float PAUSE_H = 36f;

    // -------------------------------------------------------------------------
    // Gameplay area boundaries
    // -------------------------------------------------------------------------

    /** Veggies spawn at this Y and fall down to SPAWN_BOTTOM */
    private static final float SPAWN_TOP    = Constants.WORLD_HEIGHT - Constants.HUD_HEIGHT;
    /** Veggies that reach this Y are considered missed */
    private static final float SPAWN_BOTTOM = Constants.FOOTER_HEIGHT;

    // -------------------------------------------------------------------------
    // Inner class — a single falling object (veggie or bomb)
    // -------------------------------------------------------------------------

    private static class VeggieObj {
        float x, y, speed, size;
        boolean isBomb;
        int     spriteIndex;   // index into VEGGIE_SPRITES; -1 = bomb
        float   alpha = 1f;   // fades to 0 after sliced
        boolean sliced;
        final Rectangle bounds = new Rectangle();

        void updateBounds() {
            bounds.set(x - size * 0.5f, y - size * 0.5f, size, size);
        }
    }

    // =========================================================================
    // Constructor
    // =========================================================================

    public GameScreen(MainGame game, int kitchenId) {
        this.game     = game;
        this.kitchenId = kitchenId;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        bgPath = kitchenId == Constants.KITCHEN_PIZZERIA ? "backgrounds/game/1.png"
               : kitchenId == Constants.KITCHEN_SUSHI    ? "backgrounds/game/2.png"
               :                                           "backgrounds/game/3.png";

        loadAssets();

        // Read saved difficulty
        Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
        int diff = prefs.getInteger(Constants.PREF_DIFFICULTY, Constants.DIFF_NORMAL);
        speedMultiplier = diff == Constants.DIFF_EASY  ? Constants.DIFF_EASY_SPEED
                        : diff == Constants.DIFF_HARD  ? Constants.DIFF_HARD_SPEED
                        :                                Constants.DIFF_NORMAL_SPEED;

        // Init game state
        score             = 0;
        lives             = Constants.LIVES;
        consecutiveSlices = 0;
        bestCombo         = 0;
        gameOver          = false;
        spawnTimer        = MathUtils.random(Constants.SPAWN_INTERVAL_MIN,
                                             Constants.SPAWN_INTERVAL_MAX);

        setupPauseButton();
        rebuildHudLayouts();
    }

    // =========================================================================
    // Assets
    // =========================================================================

    private void loadAssets() {
        loadIfAbsent(bgPath, Texture.class);
        for (String s : VEGGIE_SPRITES) loadIfAbsent(s, Texture.class);
        loadIfAbsent(BOMB_SPRITE, Texture.class);
        loadIfAbsent("sounds/sfx/sfx_hit.ogg",            Sound.class);
        loadIfAbsent("sounds/sfx/sfx_coin.ogg",           Sound.class);
        loadIfAbsent("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        loadIfAbsent("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        loadIfAbsent("sounds/sfx/sfx_level_complete.ogg", Sound.class);
        game.manager.finishLoading();
    }

    private <T> void loadIfAbsent(String path, Class<T> type) {
        if (!game.manager.isLoaded(path)) game.manager.load(path, type);
    }

    // =========================================================================
    // Pause button
    // =========================================================================

    private void setupPauseButton() {
        Actor pauseActor = new Actor();
        pauseActor.setBounds(PAUSE_X, PAUSE_Y, PAUSE_W, PAUSE_H);
        pauseActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
                    game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
                game.setScreen(new PauseScreen(game, GameScreen.this, kitchenId));
            }
        });
        stage.addActor(pauseActor);
    }

    // =========================================================================
    // Input
    // =========================================================================

    private void setupInput() {
        InputAdapter gameInput = new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int ptr, int btn) {
                if (!gameOver) checkSlice(sx, sy);
                return false; // don't consume — Stage pause button still fires
            }

            @Override
            public boolean touchDragged(int sx, int sy, int ptr) {
                if (!gameOver) checkSlice(sx, sy);
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new PauseScreen(game, GameScreen.this, kitchenId));
                    return true;
                }
                return false;
            }
        };
        // Stage first (consumes pause-button touches); gameInput second (slicing)
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, gameInput));
    }

    /**
     * Convert touch screen coordinates to world coordinates and test every
     * active veggie for a hit. Slices the first matching veggie found.
     */
    private void checkSlice(int screenX, int screenY) {
        Vector2 world = viewport.unproject(new Vector2(screenX, screenY));
        for (VeggieObj v : veggies) {
            if (!v.sliced && v.bounds.contains(world.x, world.y)) {
                sliceVeggie(v);
                // break — one slice contact per event to prevent double-count
                break;
            }
        }
    }

    // =========================================================================
    // Game logic
    // =========================================================================

    private void sliceVeggie(VeggieObj v) {
        v.sliced = true;

        if (v.isBomb) {
            // Hit a bomb — instant game over
            playSound("sounds/sfx/sfx_game_over.ogg");
            lives    = 0;
            gameOver = true;
            navigateToGameOver();
        } else {
            // Slice a veggie
            score += Constants.SCORE_PER_VEGGIE;
            consecutiveSlices++;
            if (consecutiveSlices > bestCombo) bestCombo = consecutiveSlices;
            playSound("sounds/sfx/sfx_hit.ogg");

            // Trigger combo result when we hit the threshold
            if (consecutiveSlices >= Constants.COMBO_THRESHOLD) {
                int chain = consecutiveSlices;
                int bonus = Constants.COMBO_BONUS_BASE
                          + (chain - Constants.COMBO_THRESHOLD) * 20;
                score            += bonus;
                consecutiveSlices = 0;   // reset chain before navigating away
                rebuildHudLayouts();
                playSound("sounds/sfx/sfx_level_complete.ogg");
                game.setScreen(new ComboResultScreen(game, chain, bonus, this, kitchenId));
                return; // don't rebuildHudLayouts again below
            }
            rebuildHudLayouts();
        }
    }

    private void update(float delta) {
        // Spawn timer
        spawnTimer -= delta;
        if (spawnTimer <= 0f) {
            spawnVeggie();
            float interval = MathUtils.random(Constants.SPAWN_INTERVAL_MIN,
                                              Constants.SPAWN_INTERVAL_MAX)
                           / speedMultiplier;
            spawnTimer = Math.max(0.3f, interval);
        }

        // Move veggies, handle misses and fade-out of sliced ones
        for (int i = veggies.size - 1; i >= 0; i--) {
            VeggieObj v = veggies.get(i);

            if (v.sliced) {
                // Fade out and remove
                v.alpha -= delta * 4f;
                if (v.alpha <= 0f) veggies.removeIndex(i);
                continue;
            }

            // Fall downward (negative Y in libGDX)
            v.y -= v.speed * delta;
            v.updateBounds();

            // Missed — went below footer
            if (v.y < SPAWN_BOTTOM - v.size && !v.sliced) {
                if (!v.isBomb) {
                    // Missed veggie: lose a life and break the combo
                    lives--;
                    consecutiveSlices = 0;
                    rebuildHudLayouts();
                    if (lives <= 0) {
                        gameOver = true;
                        navigateToGameOver();
                        return;
                    }
                }
                veggies.removeIndex(i);
            }
        }
    }

    private void spawnVeggie() {
        VeggieObj v = new VeggieObj();
        v.size    = Constants.VEGGIE_SIZE;
        float halfSize = v.size * 0.5f;
        v.x       = MathUtils.random(
                Constants.GAMEPLAY_X + halfSize,
                Constants.GAMEPLAY_X + Constants.GAMEPLAY_WIDTH - halfSize);
        v.y       = SPAWN_TOP + halfSize;
        v.isBomb  = MathUtils.random() < Constants.BOMB_CHANCE;
        v.spriteIndex = v.isBomb ? -1 : MathUtils.random(0, VEGGIE_SPRITES.length - 1);
        float baseSpeed = MathUtils.random(Constants.VEGGIE_SPEED_MIN, Constants.VEGGIE_SPEED_MAX);
        v.speed   = baseSpeed * speedMultiplier;
        v.updateBounds();
        veggies.add(v);
    }

    private void navigateToGameOver() {
        game.setScreen(new GameOverScreen(game, score, bestCombo));
    }

    private void playSound(String path) {
        if (game.sfxEnabled && game.manager.isLoaded(path))
            game.manager.get(path, Sound.class).play(1.0f);
    }

    private void rebuildHudLayouts() {
        scoreLayout = new GlyphLayout(game.fontBody,  "SCORE: " + score);
        livesLayout = new GlyphLayout(game.fontBody,  "LIVES: " + lives);
        comboLayout = new GlyphLayout(game.fontSmall, "COMBO: " + consecutiveSlices);
    }

    // =========================================================================
    // Screen lifecycle
    // =========================================================================

    @Override
    public void show() {
        // CRITICAL: re-register input every time this screen becomes active
        // (returning from PauseScreen or ComboResultScreen calls show())
        setupInput();
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    @Override
    public void render(float delta) {
        // Update game logic only when not game-over
        if (!gameOver) update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // --- Background ---
        game.batch.begin();
        game.batch.draw(game.manager.get(bgPath, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // --- HUD & footer overlays ---
        drawHudAndFooterBars();

        // --- Veggies and bombs ---
        game.batch.begin();
        for (VeggieObj v : veggies) {
            String path = v.isBomb ? BOMB_SPRITE : VEGGIE_SPRITES[v.spriteIndex];
            Texture tex = game.manager.get(path, Texture.class);
            game.batch.setColor(1f, 1f, 1f, Math.max(0f, v.alpha));
            game.batch.draw(tex,
                    v.x - v.size * 0.5f, v.y - v.size * 0.5f,
                    v.size, v.size);
        }
        game.batch.setColor(Color.WHITE);
        game.batch.end();

        // --- HUD text ---
        drawHudText();

        // --- Pause button ---
        UiFactory.drawButton(sr, game.batch, game.fontSmall, "II",
                PAUSE_X, PAUSE_Y, PAUSE_W, PAUSE_H);

        // --- Stage (pause button hit area) ---
        // CRITICAL: stage.act() is ALWAYS outside the game-logic guard
        stage.act(delta);
        stage.draw();
    }

    private void drawHudAndFooterBars() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.50f);
        // HUD bar at top
        sr.rect(0, Constants.WORLD_HEIGHT - Constants.HUD_HEIGHT,
                Constants.WORLD_WIDTH, Constants.HUD_HEIGHT);
        // Footer bar at bottom
        sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.FOOTER_HEIGHT);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawHudText() {
        float hudTop    = Constants.WORLD_HEIGHT - Constants.HUD_HEIGHT;
        float hudMidY   = hudTop + Constants.HUD_HEIGHT * 0.5f;
        float textBaseline = hudMidY + scoreLayout.height * 0.5f;

        game.batch.begin();

        // Score — left side of HUD
        game.fontBody.setColor(UiFactory.COLOR_PRIMARY);
        game.fontBody.draw(game.batch, scoreLayout, 16f, textBaseline);

        // Lives — right side of HUD
        game.fontBody.setColor(UiFactory.COLOR_ACCENT);
        game.fontBody.draw(game.batch, livesLayout,
                Constants.WORLD_WIDTH - livesLayout.width - 16f, textBaseline);

        // Combo — center of footer
        game.fontSmall.setColor(UiFactory.COLOR_TEXT);
        float comboX = PAUSE_X + PAUSE_W + 10f;
        game.fontSmall.draw(game.batch, comboLayout,
                comboX + (Constants.WORLD_WIDTH - comboX - comboX * 0.3f - comboLayout.width) * 0.5f,
                Constants.FOOTER_HEIGHT * 0.5f + comboLayout.height * 0.5f);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
