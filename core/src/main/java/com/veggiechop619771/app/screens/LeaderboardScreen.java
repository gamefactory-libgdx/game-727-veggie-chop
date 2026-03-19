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

public class LeaderboardScreen implements Screen {

    private final MainGame game;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    private static final String BG = "backgrounds/menu/3.png";

    private static final float BTN_W = Constants.BTN_WIDTH_MAIN;
    private static final float BTN_H = Constants.BTN_HEIGHT_MAIN;

    private float menuBtnX, menuBtnY;

    // Loaded scores
    private final int[] scores = new int[Constants.LEADERBOARD_SIZE];

    // Pre-computed layouts
    private final GlyphLayout headerLayout;
    private final GlyphLayout subLayout;

    // Reusable layout for row text
    private final GlyphLayout rowLayout = new GlyphLayout();

    public LeaderboardScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        loadAssets();
        loadScores();

        headerLayout = new GlyphLayout(game.fontTitle, "LEADERBOARD");
        subLayout    = new GlyphLayout(game.fontSmall, "Top 10 Scores");

        setupButtons();
    }

    // -------------------------------------------------------------------------
    // Static helper — insert score into top-10
    // -------------------------------------------------------------------------

    /**
     * Inserts {@code score} into the persisted top-10 leaderboard (descending order).
     * Call this from GameOverScreen before displaying the leaderboard.
     */
    public static void addScore(int score) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
        int[] list = new int[Constants.LEADERBOARD_SIZE];
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            list[i] = prefs.getInteger(Constants.PREF_SCORE_PREFIX + i, 0);
        }
        // Insert at first position where score beats existing entry
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            if (score > list[i]) {
                // Shift everything down
                for (int j = Constants.LEADERBOARD_SIZE - 1; j > i; j--) {
                    list[j] = list[j - 1];
                }
                list[i] = score;
                break;
            }
        }
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            prefs.putInteger(Constants.PREF_SCORE_PREFIX + i, list[i]);
        }
        prefs.flush();
    }

    // -------------------------------------------------------------------------
    // Assets & data
    // -------------------------------------------------------------------------

    private void loadAssets() {
        if (!game.manager.isLoaded(BG))
            game.manager.load(BG, Texture.class);
        if (!game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg"))
            game.manager.load("sounds/sfx/sfx_button_click.ogg", Sound.class);
        game.manager.finishLoading();
    }

    private void loadScores() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREF_FILE);
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            scores[i] = prefs.getInteger(Constants.PREF_SCORE_PREFIX + i, 0);
        }
    }

    // -------------------------------------------------------------------------
    // Layout & actors
    // -------------------------------------------------------------------------

    private void setupButtons() {
        float cx = Constants.WORLD_WIDTH / 2f;
        menuBtnX = cx - BTN_W / 2f;
        menuBtnY = 30f;

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
        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        float cx        = Constants.WORLD_WIDTH / 2f;
        float listTop   = Constants.WORLD_HEIGHT * 0.76f;
        float rowHeight = 42f;

        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Header
        game.fontTitle.setColor(UiFactory.COLOR_TEXT);
        game.fontTitle.draw(game.batch, headerLayout,
                cx - headerLayout.width / 2f,
                Constants.WORLD_HEIGHT * 0.90f);

        game.fontSmall.setColor(UiFactory.COLOR_TEXT);
        game.fontSmall.draw(game.batch, subLayout,
                cx - subLayout.width / 2f,
                Constants.WORLD_HEIGHT * 0.80f);

        // Leaderboard rows
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            float rowY = listTop - i * rowHeight;
            String rank  = (i + 1) + ".";
            String entry = scores[i] > 0 ? String.valueOf(scores[i]) : "---";

            game.fontSmall.setColor(UiFactory.COLOR_ACCENT);
            game.fontSmall.draw(game.batch, rank, cx - 110f, rowY);

            rowLayout.setText(game.fontSmall, entry);
            game.fontSmall.setColor(UiFactory.COLOR_TEXT);
            game.fontSmall.draw(game.batch, rowLayout, cx + 110f - rowLayout.width, rowY);
        }
        game.batch.end();

        // Main Menu button
        sr.setProjectionMatrix(camera.combined);
        UiFactory.drawButton(sr, game.batch, game.fontBody, "MAIN MENU", menuBtnX, menuBtnY, BTN_W, BTN_H);

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
