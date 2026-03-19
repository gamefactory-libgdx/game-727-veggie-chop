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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.veggiechop619771.app.Constants;
import com.veggiechop619771.app.MainGame;
import com.veggiechop619771.app.UiFactory;

/**
 * Overlay shown mid-game when the player achieves a combo chain.
 *
 * Displays:
 *   "COMBO!" header
 *   "N-CHAIN!" in large gold text
 *   "+NNN PTS" bonus in accent color
 *
 * Dismisses automatically after COMBO_DISPLAY_TIME seconds or when
 * the player taps the "CONTINUE" button. Either way returns to the
 * same GameScreen instance (previousScreen).
 */
public class ComboResultScreen implements Screen {

    private final MainGame game;
    private final Screen   previousScreen;  // resume same GameScreen instance
    private final int      kitchenId;
    private final int      chain;
    private final int      bonusPoints;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    // Background (same kitchen, context for the player)
    private final String bgPath;

    // Auto-dismiss timer
    private float displayTimer = Constants.COMBO_DISPLAY_TIME;

    // Button
    private float continueBtnX, continueBtnY;
    private static final float BTN_W = Constants.BTN_WIDTH_MAIN;
    private static final float BTN_H = Constants.BTN_HEIGHT_MAIN;

    // Panel
    private static final float PANEL_W = 320f;
    private static final float PANEL_H = 360f;

    // Text layouts (pre-computed)
    private GlyphLayout comboTitleLayout;
    private GlyphLayout chainLayout;
    private GlyphLayout bonusLayout;
    private GlyphLayout tapLayout;

    // Particle / sparkle animation
    private float[] sparkX, sparkY, sparkPhase;
    private static final int SPARK_COUNT = 12;
    private float sparkTime = 0f;

    // Colors
    private static final Color COLOR_GOLD   = new Color(1f,   0.84f, 0f,   1f);
    private static final Color OVERLAY      = new Color(0f,   0f,   0f,   0.60f);
    private static final Color PANEL_COLOR  = new Color(0.08f, 0.08f, 0.08f, 0.94f);

    public ComboResultScreen(MainGame game, int chain, int bonusPoints,
                             Screen previousScreen, int kitchenId) {
        this.game           = game;
        this.chain          = chain;
        this.bonusPoints    = bonusPoints;
        this.previousScreen = previousScreen;
        this.kitchenId      = kitchenId;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        bgPath = kitchenId == Constants.KITCHEN_PIZZERIA ? "backgrounds/game/1.png"
               : kitchenId == Constants.KITCHEN_SUSHI    ? "backgrounds/game/2.png"
               :                                           "backgrounds/game/3.png";

        loadAssets();
        buildLayouts();
        initSparkles();
        setupContinueButton();
    }

    private void loadAssets() {
        if (!game.manager.isLoaded(bgPath)) game.manager.load(bgPath, Texture.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.finishLoading();
    }

    private void buildLayouts() {
        comboTitleLayout = new GlyphLayout(game.fontTitle, "COMBO!");
        chainLayout      = new GlyphLayout(game.fontTitle, chain + "-CHAIN!");
        bonusLayout      = new GlyphLayout(game.fontBody,  "+" + bonusPoints + " PTS");
        tapLayout        = new GlyphLayout(game.fontSmall, "TAP TO CONTINUE");
    }

    private void initSparkles() {
        float panelX = (Constants.WORLD_WIDTH  - PANEL_W) / 2f;
        float panelY = (Constants.WORLD_HEIGHT - PANEL_H) / 2f;

        sparkX     = new float[SPARK_COUNT];
        sparkY     = new float[SPARK_COUNT];
        sparkPhase = new float[SPARK_COUNT];

        for (int i = 0; i < SPARK_COUNT; i++) {
            // Distribute sparkles around panel border
            float t = (float) i / SPARK_COUNT;
            sparkX[i]     = panelX + t * PANEL_W;
            sparkY[i]     = panelY + MathUtils.random(-20f, PANEL_H + 20f);
            sparkPhase[i] = MathUtils.random(0f, MathUtils.PI2);
        }
    }

    private void setupContinueButton() {
        float panelY = (Constants.WORLD_HEIGHT - PANEL_H) / 2f;
        continueBtnX = (Constants.WORLD_WIDTH - BTN_W) / 2f;
        continueBtnY = panelY + 20f;

        Actor btn = new Actor();
        btn.setBounds(continueBtnX, continueBtnY, BTN_W, BTN_H);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                dismiss();
            }
        });
        stage.addActor(btn);
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        }));
    }

    private void dismiss() {
        game.setScreen(previousScreen);
    }

    private void playClick() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
    }

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void show() {
        setupInput();
        // Gameplay music continues — same-track guard prevents restart
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    @Override
    public void render(float delta) {
        // Auto-dismiss countdown
        displayTimer -= delta;
        if (displayTimer <= 0f) {
            dismiss();
            return;
        }

        sparkTime += delta;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Kitchen background (frozen frame context)
        game.batch.begin();
        game.batch.draw(game.manager.get(bgPath, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setProjectionMatrix(camera.combined);

        // Full-screen dark overlay
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(OVERLAY);
        sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        sr.end();

        // Panel card
        float panelX = (Constants.WORLD_WIDTH  - PANEL_W) / 2f;
        float panelY = (Constants.WORLD_HEIGHT - PANEL_H) / 2f;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(PANEL_COLOR);
        drawRoundedRect(panelX, panelY, PANEL_W, PANEL_H, 22f);
        sr.end();

        // Sparkles (animated dots around the panel)
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < SPARK_COUNT; i++) {
            float pulse = 0.5f + 0.5f * MathUtils.sin(sparkTime * 4f + sparkPhase[i]);
            sr.setColor(COLOR_GOLD.r, COLOR_GOLD.g, COLOR_GOLD.b, pulse * 0.9f);
            float sz = 5f + 3f * pulse;
            sr.circle(sparkX[i], sparkY[i] + MathUtils.sin(sparkTime * 2f + sparkPhase[i]) * 8f, sz, 8);
        }
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Text inside the panel
        float cx = Constants.WORLD_WIDTH / 2f;

        game.batch.begin();

        // "COMBO!" — top of panel
        game.fontTitle.setColor(UiFactory.COLOR_TEXT);
        game.fontTitle.draw(game.batch, comboTitleLayout,
                cx - comboTitleLayout.width / 2f,
                panelY + PANEL_H - 20f);

        // "N-CHAIN!" — large gold, center of panel
        game.fontTitle.setColor(COLOR_GOLD);
        game.fontTitle.draw(game.batch, chainLayout,
                cx - chainLayout.width / 2f,
                panelY + PANEL_H * 0.60f);

        // "+NNN PTS" — accent, below chain text
        game.fontBody.setColor(UiFactory.COLOR_ACCENT);
        game.fontBody.draw(game.batch, bonusLayout,
                cx - bonusLayout.width / 2f,
                panelY + PANEL_H * 0.40f);

        // Auto-dismiss countdown hint
        game.fontSmall.setColor(new Color(1f, 1f, 1f, 0.6f));
        game.fontSmall.draw(game.batch, tapLayout,
                cx - tapLayout.width / 2f,
                continueBtnY + BTN_H + 14f);

        game.batch.end();

        // "CONTINUE" button
        UiFactory.drawButton(sr, game.batch, game.fontBody, "CONTINUE",
                continueBtnX, continueBtnY, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
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
