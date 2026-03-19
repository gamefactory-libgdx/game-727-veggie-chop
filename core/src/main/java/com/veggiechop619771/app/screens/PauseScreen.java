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
 * Pause overlay shown when the player presses the pause button during gameplay.
 *
 * Buttons:
 *   RESUME  → returns to the same GameScreen instance (previousScreen.show() is called)
 *   RESTART → creates a new GameScreen(game, kitchenId) — fresh game state
 *   MAIN MENU → returns to MainMenuScreen
 */
public class PauseScreen implements Screen {

    private final MainGame game;
    private final Screen   previousScreen;   // the GameScreen instance to resume
    private final int      kitchenId;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    // Background path matches the game background of the paused kitchen
    private final String bgPath;

    private static final float BTN_W = Constants.BTN_WIDTH_MAIN;
    private static final float BTN_H = Constants.BTN_HEIGHT_MAIN;

    private float resumeX,  resumeY;
    private float restartX, restartY;
    private float menuX,    menuY;

    private GlyphLayout pausedLayout;

    // Card (panel) dimensions
    private static final float CARD_W  = 340f;
    private static final float CARD_H  = 340f;
    private static final Color OVERLAY = new Color(0f, 0f, 0f, 0.65f);
    private static final Color PANEL   = new Color(0.10f, 0.10f, 0.10f, 0.92f);

    public PauseScreen(MainGame game, Screen previousScreen, int kitchenId) {
        this.game           = game;
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

        pausedLayout = new GlyphLayout(game.fontTitle, "PAUSED");
        setupButtons();
    }

    private void loadAssets() {
        if (!game.manager.isLoaded(bgPath)) game.manager.load(bgPath, Texture.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.finishLoading();
    }

    private void setupButtons() {
        float cx    = Constants.WORLD_WIDTH  / 2f;
        float cardY = (Constants.WORLD_HEIGHT - CARD_H) / 2f;
        float gap   = BTN_H + Constants.BTN_SPACING;

        // Stack 3 buttons centered in the panel
        float buttonsStartY = cardY + (CARD_H - (3 * BTN_H + 2 * Constants.BTN_SPACING)) / 2f;

        resumeX  = cx - BTN_W / 2f;  resumeY  = buttonsStartY + 2 * gap;
        restartX = cx - BTN_W / 2f;  restartY = buttonsStartY + gap;
        menuX    = cx - BTN_W / 2f;  menuY    = buttonsStartY;

        addActor(resumeX,  resumeY,  BTN_W, BTN_H, () -> {
            playClick();
            game.setScreen(previousScreen);
        });
        addActor(restartX, restartY, BTN_W, BTN_H, () -> {
            playClick();
            game.setScreen(new GameScreen(game, kitchenId));
        });
        addActor(menuX, menuY, BTN_W, BTN_H, () -> {
            playClick();
            game.setScreen(new MainMenuScreen(game));
        });
    }

    private void addActor(float x, float y, float w, float h, Runnable action) {
        Actor a = new Actor();
        a.setBounds(x, y, w, h);
        a.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float ex, float ey) {
                action.run();
            }
        });
        stage.addActor(a);
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    // Back key resumes the game
                    game.setScreen(previousScreen);
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

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void show() {
        setupInput();
        // Music keeps playing from GameScreen — playMusic guard prevents restart
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Draw kitchen background (context for the player)
        game.batch.begin();
        game.batch.draw(game.manager.get(bgPath, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Dark overlay over entire screen
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(OVERLAY);
        sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        sr.end();

        // Panel card
        float cardX = (Constants.WORLD_WIDTH  - CARD_W) / 2f;
        float cardY = (Constants.WORLD_HEIGHT - CARD_H) / 2f;
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(PANEL);
        drawRoundedRect(cardX, cardY, CARD_W, CARD_H, 20f);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // "PAUSED" header
        float titleY = cardY + CARD_H - 30f;
        game.batch.begin();
        game.fontTitle.setColor(UiFactory.COLOR_TEXT);
        game.fontTitle.draw(game.batch, pausedLayout,
                (Constants.WORLD_WIDTH - pausedLayout.width) / 2f, titleY);
        game.batch.end();

        // Buttons
        UiFactory.drawButton(sr, game.batch, game.fontBody, "RESUME",    resumeX,  resumeY,  BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "RESTART",   restartX, restartY, BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "MAIN MENU", menuX,    menuY,    BTN_W, BTN_H);

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
