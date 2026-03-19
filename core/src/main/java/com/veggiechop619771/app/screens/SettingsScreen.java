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

public class SettingsScreen implements Screen {

    private final MainGame game;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    private static final String BG = "backgrounds/menu/2.png";

    // Toggle button dimensions (secondary size)
    private static final float TOG_W = Constants.BTN_WIDTH_SEC;
    private static final float TOG_H = Constants.BTN_HEIGHT_MAIN;
    private static final float BTN_W = Constants.BTN_WIDTH_MAIN;
    private static final float BTN_H = Constants.BTN_HEIGHT_MAIN;

    // Layout coordinates
    private float musicBtnX, musicBtnY;
    private float sfxBtnX,   sfxBtnY;
    private float menuBtnX,  menuBtnY;

    // State
    private boolean musicOn;
    private boolean sfxOn;

    // Pre-computed layouts
    private final GlyphLayout headerLayout;

    public SettingsScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // Load persisted state
        Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
        musicOn = prefs.getBoolean(Constants.PREF_MUSIC, true);
        sfxOn   = prefs.getBoolean(Constants.PREF_SFX,   true);
        game.musicEnabled = musicOn;
        game.sfxEnabled   = sfxOn;

        loadAssets();

        headerLayout = new GlyphLayout(game.fontTitle, "SETTINGS");

        setupLayout();
    }

    // -------------------------------------------------------------------------
    // Assets
    // -------------------------------------------------------------------------

    private void loadAssets() {
        if (!game.manager.isLoaded(BG))
            game.manager.load(BG, Texture.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_toggle.ogg"))
            game.manager.load("sounds/sfx/sfx_toggle.ogg", Sound.class);
        game.manager.finishLoading();
    }

    // -------------------------------------------------------------------------
    // Layout & actors
    // -------------------------------------------------------------------------

    private void setupLayout() {
        float cx   = Constants.WORLD_WIDTH  / 2f;
        float midY = Constants.WORLD_HEIGHT * 0.52f;
        float gap  = TOG_H + Constants.BTN_SPACING + 10f;

        musicBtnX = cx - TOG_W / 2f;
        musicBtnY = midY + gap * 0.5f;
        sfxBtnX   = cx - TOG_W / 2f;
        sfxBtnY   = midY - gap * 0.5f;
        menuBtnX  = cx - BTN_W / 2f;
        menuBtnY  = midY - gap * 1.8f;

        // Music toggle hit area
        Actor musicActor = new Actor();
        musicActor.setBounds(musicBtnX, musicBtnY, TOG_W, TOG_H);
        musicActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                musicOn = !musicOn;
                Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
                prefs.putBoolean(Constants.PREF_MUSIC, musicOn);
                prefs.flush();
                game.musicEnabled = musicOn;
                if (game.currentMusic != null) {
                    if (musicOn) game.currentMusic.play();
                    else         game.currentMusic.pause();
                }
                playToggle();
            }
        });
        stage.addActor(musicActor);

        // SFX toggle hit area
        Actor sfxActor = new Actor();
        sfxActor.setBounds(sfxBtnX, sfxBtnY, TOG_W, TOG_H);
        sfxActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                sfxOn = !sfxOn;
                Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
                prefs.putBoolean(Constants.PREF_SFX, sfxOn);
                prefs.flush();
                game.sfxEnabled = sfxOn;
                playToggle();
            }
        });
        stage.addActor(sfxActor);

        // Main Menu button hit area
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

    private void playToggle() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_toggle.ogg"))
            game.manager.get("sounds/sfx/sfx_toggle.ogg", Sound.class).play(0.5f);
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

        // Background, header, row labels
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        game.fontTitle.setColor(UiFactory.COLOR_TEXT);
        game.fontTitle.draw(game.batch, headerLayout,
                (Constants.WORLD_WIDTH - headerLayout.width) / 2f,
                Constants.WORLD_HEIGHT * 0.85f);

        float labelX = Constants.WORLD_WIDTH * 0.14f;
        game.fontBody.setColor(UiFactory.COLOR_TEXT);
        game.fontBody.draw(game.batch, "MUSIC",    labelX, musicBtnY + TOG_H * 0.72f);
        game.fontBody.draw(game.batch, "SOUND FX", labelX, sfxBtnY   + TOG_H * 0.72f);
        game.batch.end();

        // Buttons
        sr.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                musicOn ? "ON" : "OFF", musicBtnX, musicBtnY, TOG_W, TOG_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                sfxOn   ? "ON" : "OFF", sfxBtnX,   sfxBtnY,   TOG_W, TOG_H);
        UiFactory.drawButton(sr, game.batch, game.fontBody,
                "MAIN MENU", menuBtnX, menuBtnY, BTN_W, BTN_H);

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
