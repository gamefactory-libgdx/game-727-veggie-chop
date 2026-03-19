package com.veggiechop619771.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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

public class GameOverScreen implements Screen {

    private final MainGame game;
    private final int      score;
    private final int      bestCombo;   // extra: best combo achieved in the run
    private final int      personalBest;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    private static final String BG = "backgrounds/game/1.png";

    private static final float BTN_W = Constants.BTN_WIDTH_MAIN;
    private static final float BTN_H = Constants.BTN_HEIGHT_MAIN;

    private float retryX,  retryY;
    private float menuBtnX, menuBtnY;

    // Pre-computed text layouts
    private final GlyphLayout gameOverLayout;
    private final GlyphLayout scoreLayout;
    private final GlyphLayout bestLayout;
    private final GlyphLayout comboLayout;

    public GameOverScreen(MainGame game, int score, int bestCombo) {
        this.game      = game;
        this.score     = score;
        this.bestCombo = bestCombo;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // Persist score
        LeaderboardScreen.addScore(score);
        Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
        int prev = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);
        if (score > prev) {
            prefs.putInteger(Constants.PREF_HIGH_SCORE, score);
            prefs.flush();
            personalBest = score;
        } else {
            personalBest = prev;
        }

        loadAssets();

        // Pre-compute text layouts
        gameOverLayout = new GlyphLayout(game.fontTitle, "GAME OVER");
        scoreLayout    = new GlyphLayout(game.fontBody,  "SCORE: " + score);
        bestLayout     = new GlyphLayout(game.fontBody,  "BEST: "  + personalBest);
        comboLayout    = new GlyphLayout(game.fontSmall, "BEST COMBO: " + bestCombo);

        setupButtons();
    }

    // -------------------------------------------------------------------------
    // Assets
    // -------------------------------------------------------------------------

    private void loadAssets() {
        if (!game.manager.isLoaded(BG))
            game.manager.load(BG, Texture.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.finishLoading();
    }

    // -------------------------------------------------------------------------
    // Layout & actors
    // -------------------------------------------------------------------------

    private void setupButtons() {
        float cx  = Constants.WORLD_WIDTH / 2f;
        float gap = BTN_H + Constants.BTN_SPACING;

        retryX   = cx - BTN_W / 2f;  retryY   = Constants.WORLD_HEIGHT * 0.28f;
        menuBtnX = cx - BTN_W / 2f;  menuBtnY = retryY - gap;

        Actor retryActor = new Actor();
        retryActor.setBounds(retryX, retryY, BTN_W, BTN_H);
        retryActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                playClick();
                game.setScreen(new KitchenSelectScreen(game));
            }
        });
        stage.addActor(retryActor);

        Actor menuActor = new Actor();
        menuActor.setBounds(menuBtnX, menuBtnY, BTN_W, BTN_H);
        menuActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                playClick();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuActor);
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

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
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1.0f);
    }

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void show() {
        setupInput();
        game.playMusicOnce("sounds/music/music_game_over.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Background, header, score info
        float cx = Constants.WORLD_WIDTH / 2f;

        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        game.fontTitle.setColor(UiFactory.COLOR_TEXT);
        game.fontTitle.draw(game.batch, gameOverLayout,
                cx - gameOverLayout.width / 2f,
                Constants.WORLD_HEIGHT * 0.88f);

        game.fontBody.setColor(UiFactory.COLOR_ACCENT);
        game.fontBody.draw(game.batch, scoreLayout,
                cx - scoreLayout.width / 2f,
                Constants.WORLD_HEIGHT * 0.70f);

        game.fontBody.setColor(UiFactory.COLOR_TEXT);
        game.fontBody.draw(game.batch, bestLayout,
                cx - bestLayout.width / 2f,
                Constants.WORLD_HEIGHT * 0.60f);

        game.fontSmall.setColor(UiFactory.COLOR_TEXT);
        game.fontSmall.draw(game.batch, comboLayout,
                cx - comboLayout.width / 2f,
                Constants.WORLD_HEIGHT * 0.51f);
        game.batch.end();

        // Buttons
        sr.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "PLAY AGAIN", retryX,   retryY,   BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "MAIN MENU",  menuBtnX, menuBtnY, BTN_W, BTN_H);

        stage.act(delta);
        stage.draw();
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
