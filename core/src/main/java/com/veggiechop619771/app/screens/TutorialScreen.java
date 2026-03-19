package com.veggiechop619771.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.veggiechop619771.app.Constants;
import com.veggiechop619771.app.MainGame;
import com.veggiechop619771.app.UiFactory;

/**
 * 4-step tutorial screen.
 *
 * Pages:
 *   1 — Swipe to Slice
 *   2 — Build Combos
 *   3 — Avoid Bombs
 *   4 — Score Big
 *
 * Navigation: PREV / NEXT buttons at bottom; SKIP link at top-right.
 */
public class TutorialScreen implements Screen {

    private final MainGame game;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    private static final String BG = "backgrounds/menu/3.png";

    private int currentPage = 0;   // 0-indexed; max = TUTORIAL_STEPS - 1

    // Button geometry
    private static final float NAV_BTN_W = Constants.BTN_WIDTH_SEC;
    private static final float NAV_BTN_H = Constants.BTN_HEIGHT_SEC;
    private static final float NAV_BTN_Y = 40f;
    private static final float GAP       = 20f;

    private float prevBtnX, prevBtnY;
    private float nextBtnX, nextBtnY;
    private float skipBtnX, skipBtnY;

    // Dot indicator
    private static final float DOT_R    = 8f;
    private static final float DOT_GAP  = 24f;
    private static final float DOT_Y    = NAV_BTN_Y + NAV_BTN_H + 20f;

    // Illustration area
    private static final float ILLUS_X  = 80f;
    private static final float ILLUS_Y  = Constants.WORLD_HEIGHT * 0.35f;
    private static final float ILLUS_W  = Constants.WORLD_WIDTH - 160f;
    private static final float ILLUS_H  = 180f;

    // Text area
    private static final float TEXT_Y   = Constants.WORLD_HEIGHT * 0.29f;

    // Animation clock
    private float animTime = 0f;

    // Page content arrays
    private static final String[] STEP_TITLES = {
        "SWIPE TO SLICE",
        "BUILD COMBOS",
        "AVOID BOMBS",
        "SCORE BIG"
    };
    private static final String[] STEP_DESC = {
        "Swipe or tap on falling\nveggies to slice them.\nEvery slice scores points!",
        "Slice 3 or more veggies\nwithout missing to trigger\na COMBO bonus!",
        "Red bombs end the game\ninstantly. Never tap the\nbomb — only veggies!",
        "Miss 3 veggies and it's\nGame Over. Change difficulty\nin Settings for a challenge."
    };

    // Colors for illustrations
    private static final Color COLOR_VEGGIE = new Color(0.18f, 0.80f, 0.44f, 1f);
    private static final Color COLOR_BOMB   = new Color(0.90f, 0.20f, 0.20f, 1f);
    private static final Color COLOR_GOLD   = new Color(1.00f, 0.84f, 0.00f, 1f);
    private static final Color COLOR_SLASH  = new Color(1f,   1f,   0.8f, 0.9f);

    // Reused GlyphLayouts
    private GlyphLayout titleLayout;
    private GlyphLayout descLayout;
    private GlyphLayout skipLayout;

    public TutorialScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        loadAssets();
        calculateButtonPositions();
        buildLayouts();
        setupActors();
    }

    private void loadAssets() {
        if (!game.manager.isLoaded(BG)) game.manager.load(BG, Texture.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_button_back.ogg"))
            game.manager.load("sounds/sfx/sfx_button_back.ogg", Sound.class);
        game.manager.finishLoading();
    }

    private void calculateButtonPositions() {
        float cx = Constants.WORLD_WIDTH / 2f;

        // PREV (left) and NEXT (right) at bottom
        float totalBtnW = 2 * NAV_BTN_W + GAP;
        prevBtnX = cx - totalBtnW / 2f;
        prevBtnY = NAV_BTN_Y;
        nextBtnX = prevBtnX + NAV_BTN_W + GAP;
        nextBtnY = NAV_BTN_Y;

        // SKIP — top right
        skipBtnX = Constants.WORLD_WIDTH - NAV_BTN_W * 0.6f - 12f;
        skipBtnY = Constants.WORLD_HEIGHT - 56f;
    }

    private void buildLayouts() {
        titleLayout = new GlyphLayout(game.fontBody,  STEP_TITLES[currentPage]);
        descLayout  = new GlyphLayout(game.fontSmall, STEP_DESC[currentPage],
                Color.WHITE, ILLUS_W, 1 /* left */, true /* wrap */);
        skipLayout  = new GlyphLayout(game.fontSmall, "SKIP");
    }

    private void setupActors() {
        // PREV button
        Actor prevActor = new Actor();
        prevActor.setBounds(prevBtnX, prevBtnY, NAV_BTN_W, NAV_BTN_H);
        prevActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage > 0) {
                    playClick();
                    currentPage--;
                    buildLayouts();
                }
            }
        });
        stage.addActor(prevActor);

        // NEXT / DONE button
        Actor nextActor = new Actor();
        nextActor.setBounds(nextBtnX, nextBtnY, NAV_BTN_W, NAV_BTN_H);
        nextActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                if (currentPage < Constants.TUTORIAL_STEPS - 1) {
                    currentPage++;
                    buildLayouts();
                } else {
                    // Last page — go to main menu
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        });
        stage.addActor(nextActor);

        // SKIP button (top-right text link)
        Actor skipActor = new Actor();
        skipActor.setBounds(skipBtnX - 10f, skipBtnY - 10f, NAV_BTN_W * 0.6f + 20f, 40f);
        skipActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playBack();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(skipActor);
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void playClick() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
    }

    private void playBack() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_back.ogg"))
            game.manager.get("sounds/sfx/sfx_button_back.ogg", Sound.class).play(1f);
    }

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void show() {
        setupInput();
        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        animTime += delta;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Background
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Illustration area background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.35f);
        drawRoundedRect(ILLUS_X, ILLUS_Y, ILLUS_W, ILLUS_H, 16f);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw page-specific illustration
        drawIllustration(currentPage);

        // Step title
        float cx = Constants.WORLD_WIDTH / 2f;
        game.batch.begin();
        game.fontBody.setColor(UiFactory.COLOR_PRIMARY);
        game.fontBody.draw(game.batch, titleLayout,
                cx - titleLayout.width / 2f,
                Constants.WORLD_HEIGHT * 0.87f);

        // Description text
        game.fontSmall.setColor(UiFactory.COLOR_TEXT);
        game.fontSmall.draw(game.batch, descLayout,
                ILLUS_X, TEXT_Y);

        // SKIP text (top right)
        game.fontSmall.setColor(new Color(1f, 1f, 1f, 0.7f));
        game.fontSmall.draw(game.batch, skipLayout, skipBtnX, skipBtnY);
        game.batch.end();

        // Navigation buttons
        boolean isFirst = currentPage == 0;
        boolean isLast  = currentPage == Constants.TUTORIAL_STEPS - 1;

        // PREV (grayed out on first page)
        if (!isFirst) {
            UiFactory.drawButton(sr, game.batch, game.fontBody, "PREV",
                    prevBtnX, prevBtnY, NAV_BTN_W, NAV_BTN_H);
        } else {
            drawDisabledButton("PREV", prevBtnX, prevBtnY, NAV_BTN_W, NAV_BTN_H);
        }

        // NEXT or DONE
        UiFactory.drawButton(sr, game.batch, game.fontBody, isLast ? "DONE" : "NEXT",
                nextBtnX, nextBtnY, NAV_BTN_W, NAV_BTN_H);

        // Page dots
        drawDots();

        stage.act(delta);
        stage.draw();
    }

    // -------------------------------------------------------------------------
    // Illustrations — drawn with ShapeRenderer to avoid external assets
    // -------------------------------------------------------------------------

    private void drawIllustration(int page) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);

        float cx = Constants.WORLD_WIDTH / 2f;
        float cy = ILLUS_Y + ILLUS_H / 2f;

        switch (page) {
            case 0: drawIllustPage0(cx, cy); break;
            case 1: drawIllustPage1(cx, cy); break;
            case 2: drawIllustPage2(cx, cy); break;
            case 3: drawIllustPage3(cx, cy); break;
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Page 0: Finger swiping through a row of falling veggies */
    private void drawIllustPage0(float cx, float cy) {
        // 3 veggies in a diagonal line (falling effect)
        float[] vx = { cx - 60f, cx, cx + 60f };
        float[] vy = { cy + 40f, cy, cy - 40f };

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(COLOR_VEGGIE);
        for (int i = 0; i < 3; i++) {
            sr.circle(vx[i], vy[i], 24f, 16);
        }

        // Slash line (animated)
        float slashProgress = (animTime % 1.5f) / 1.5f;
        float sx1 = cx - 80f;
        float sx2 = cx - 80f + slashProgress * 160f;
        float syTop = cy + 60f;
        sr.setColor(COLOR_SLASH);
        // Draw as thick line via thin rects approximation
        for (int t = -2; t <= 2; t++) {
            sr.rectLine(sx1, syTop + t, sx2, cy - 60f + t * 0.5f, 2f);
        }
        sr.end();

        // Finger icon (circle at end of slash)
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(UiFactory.COLOR_TEXT);
        sr.circle(sx2, cy - 60f + (syTop - (cy - 60f)) * (1 - slashProgress), 10f, 12);
        sr.end();
    }

    /** Page 1: Chain of 3 veggies with "x3" indicator */
    private void drawIllustPage1(float cx, float cy) {
        float[] vx = { cx - 70f, cx, cx + 70f };

        sr.begin(ShapeRenderer.ShapeType.Filled);
        // Veggies
        sr.setColor(COLOR_VEGGIE);
        for (float x : vx) sr.circle(x, cy, 22f, 16);

        // Connecting lines (chain)
        sr.setColor(COLOR_GOLD);
        sr.rectLine(vx[0], cy, vx[1], cy, 3f);
        sr.rectLine(vx[1], cy, vx[2], cy, 3f);

        // Combo badge (pulsing circle above middle veggie)
        float pulse = 0.8f + 0.2f * (float) Math.sin(animTime * 4f);
        sr.setColor(UiFactory.COLOR_PRIMARY);
        sr.circle(cx, cy + 50f, 26f * pulse, 16);
        sr.end();

        game.batch.begin();
        GlyphLayout x3 = new GlyphLayout(game.fontSmall, "x3");
        game.fontSmall.setColor(UiFactory.COLOR_TEXT);
        game.fontSmall.draw(game.batch, x3,
                cx - x3.width / 2f, cy + 50f + x3.height / 2f);
        game.batch.end();
    }

    /** Page 2: Veggie on left (safe), Bomb on right (danger) */
    private void drawIllustPage2(float cx, float cy) {
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Safe veggie (left)
        sr.setColor(COLOR_VEGGIE);
        sr.circle(cx - 70f, cy, 26f, 16);

        // Bomb (right) — red with fuse
        sr.setColor(COLOR_BOMB);
        float bombPulse = 0.9f + 0.1f * (float) Math.abs(Math.sin(animTime * 5f));
        sr.circle(cx + 70f, cy, 28f * bombPulse, 16);

        // Fuse
        sr.setColor(new Color(0.6f, 0.3f, 0.0f, 1f));
        sr.rectLine(cx + 70f, cy + 28f, cx + 80f, cy + 46f, 3f);

        // X mark on bomb (danger indicator)
        sr.setColor(UiFactory.COLOR_TEXT);
        sr.rectLine(cx + 55f, cy - 14f, cx + 85f, cy + 14f, 3f);
        sr.rectLine(cx + 55f, cy + 14f, cx + 85f, cy - 14f, 3f);

        sr.end();

        // Labels
        game.batch.begin();
        GlyphLayout okL  = new GlyphLayout(game.fontSmall, "SLICE");
        GlyphLayout nokL = new GlyphLayout(game.fontSmall, "AVOID");
        game.fontSmall.setColor(COLOR_VEGGIE);
        game.fontSmall.draw(game.batch, okL,
                cx - 70f - okL.width / 2f, cy - 36f);
        game.fontSmall.setColor(COLOR_BOMB);
        game.fontSmall.draw(game.batch, nokL,
                cx + 70f - nokL.width / 2f, cy - 40f);
        game.batch.end();
    }

    /** Page 3: Score display with difficulty badges */
    private void drawIllustPage3(float cx, float cy) {
        // Score digits animation
        int displayScore = (int) ((animTime * 120f) % 9999);

        game.batch.begin();
        GlyphLayout scoreL = new GlyphLayout(game.fontBody, "SCORE: " + displayScore);
        game.fontBody.setColor(COLOR_GOLD);
        game.fontBody.draw(game.batch, scoreL,
                cx - scoreL.width / 2f, cy + 40f);

        // Difficulty labels
        String[] diffs = { "EASY x0.75", "NORMAL x1.0", "HARD x1.4" };
        Color[]  cols  = {
            new Color(0.2f, 0.8f, 0.2f, 1f),
            new Color(1.0f, 0.7f, 0.0f, 1f),
            new Color(0.9f, 0.2f, 0.2f, 1f)
        };

        float dy = cy - 10f;
        for (int i = 0; i < 3; i++) {
            GlyphLayout dL = new GlyphLayout(game.fontSmall, diffs[i]);
            game.fontSmall.setColor(cols[i]);
            game.fontSmall.draw(game.batch, dL,
                    cx - dL.width / 2f, dy);
            dy -= 26f;
        }
        game.batch.end();
    }

    // -------------------------------------------------------------------------
    // UI helpers
    // -------------------------------------------------------------------------

    private void drawDots() {
        int steps = Constants.TUTORIAL_STEPS;
        float totalW = steps * DOT_R * 2 + (steps - 1) * DOT_GAP;
        float startX = Constants.WORLD_WIDTH / 2f - totalW / 2f + DOT_R;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < steps; i++) {
            if (i == currentPage) {
                sr.setColor(UiFactory.COLOR_PRIMARY);
                sr.circle(startX + i * (DOT_R * 2 + DOT_GAP), DOT_Y, DOT_R + 2, 12);
            } else {
                sr.setColor(new Color(1f, 1f, 1f, 0.4f));
                sr.circle(startX + i * (DOT_R * 2 + DOT_GAP), DOT_Y, DOT_R, 12);
            }
        }

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawDisabledButton(String label, float x, float y, float w, float h) {
        float r = h * 0.35f;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.4f, 0.4f, 0.4f, 0.5f);
        drawRoundedRect(x, y, w, h, r);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        GlyphLayout gl = new GlyphLayout(game.fontBody, label);
        game.batch.begin();
        game.fontBody.setColor(0.7f, 0.7f, 0.7f, 0.6f);
        game.fontBody.draw(game.batch, gl,
                x + (w - gl.width) / 2f, y + (h + gl.height) / 2f);
        game.batch.end();
    }

    private void drawRoundedRect(float x, float y, float w, float h, float r) {
        sr.rect(x + r, y,     w - 2 * r, h);
        sr.rect(x,     y + r, r,         h - 2 * r);
        sr.rect(x + w - r, y + r, r,     h - 2 * r);
        sr.arc(x + r,     y + r,     r, 180, 90, 10);
        sr.arc(x + w - r, y + r,     r, 270, 90, 10);
        sr.arc(x + w - r, y + h - r, r,   0, 90, 10);
        sr.arc(x + r,     y + h - r, r,  90, 90, 10);
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
