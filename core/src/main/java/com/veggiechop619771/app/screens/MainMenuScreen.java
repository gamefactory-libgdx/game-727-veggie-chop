package com.veggiechop619771.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

public class MainMenuScreen implements Screen {

    private final MainGame game;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    private static final String BG = "backgrounds/menu/1.png";

    // Button layout
    private static final float BTN_W = Constants.BTN_WIDTH_MAIN;
    private static final float BTN_H = Constants.BTN_HEIGHT_MAIN;

    private float playX,  playY;
    private float lbX,    lbY;
    private float settX,  settY;

    // Pre-computed text layout for title
    private GlyphLayout titleLayout;

    public MainMenuScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        loadAssets();

        // Restore persisted settings
        Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
        game.musicEnabled = prefs.getBoolean(Constants.PREF_MUSIC, true);
        game.sfxEnabled   = prefs.getBoolean(Constants.PREF_SFX,   true);

        titleLayout = new GlyphLayout(game.fontTitle, "VEGGIE CHOP");

        setupButtons();
    }

    // -------------------------------------------------------------------------
    // Asset loading
    // -------------------------------------------------------------------------

    private void loadAssets() {
        loadIfAbsent(BG,                                    Texture.class);
        loadIfAbsent("sounds/music/music_menu.ogg",         Music.class);
        loadIfAbsent("sounds/music/music_gameplay.ogg",     Music.class);
        loadIfAbsent("sounds/music/music_game_over.ogg",    Music.class);
        loadIfAbsent("sounds/sfx/sfx_button_click.ogg",     Sound.class);
        loadIfAbsent("sounds/sfx/sfx_button_back.ogg",      Sound.class);
        loadIfAbsent("sounds/sfx/sfx_toggle.ogg",           Sound.class);
        loadIfAbsent("sounds/sfx/sfx_game_over.ogg",        Sound.class);
        game.manager.finishLoading();
    }

    private <T> void loadIfAbsent(String path, Class<T> type) {
        if (!game.manager.isLoaded(path)) game.manager.load(path, type);
    }

    // -------------------------------------------------------------------------
    // Button setup
    // -------------------------------------------------------------------------

    private void setupButtons() {
        float cx  = Constants.WORLD_WIDTH  / 2f;
        float gap = BTN_H + Constants.BTN_SPACING;

        float startY = Constants.WORLD_HEIGHT * 0.52f;
        playX = cx - BTN_W / 2f;  playY = startY;
        lbX   = cx - BTN_W / 2f;  lbY   = startY - gap;
        settX = cx - BTN_W / 2f;  settY = startY - gap * 2f;

        addHitActor(playX,  playY,  BTN_W, BTN_H, () -> {
            playClick();
            game.setScreen(new KitchenSelectScreen(game));
        });
        addHitActor(lbX,    lbY,    BTN_W, BTN_H, () -> {
            playClick();
            game.setScreen(new LeaderboardScreen(game));
        });
        addHitActor(settX,  settY,  BTN_W, BTN_H, () -> {
            playClick();
            game.setScreen(new SettingsScreen(game));
        });
    }

    private void addHitActor(float x, float y, float w, float h, Runnable action) {
        Actor actor = new Actor();
        actor.setBounds(x, y, w, h);
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float ex, float ey) {
                action.run();
            }
        });
        stage.addActor(actor);
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.exit();
                    return true;
                }
                return false;
            }
        }));
    }

    private void playClick() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1.0f);
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
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Background + title
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.fontTitle.setColor(UiFactory.COLOR_TEXT);
        game.fontTitle.draw(game.batch, titleLayout,
                (Constants.WORLD_WIDTH - titleLayout.width) / 2f,
                Constants.WORLD_HEIGHT * 0.87f);
        game.batch.end();

        // Buttons
        sr.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "PLAY",        playX, playY, BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "LEADERBOARD", lbX,   lbY,   BTN_W, BTN_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "SETTINGS",    settX, settY, BTN_W, BTN_H);

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
