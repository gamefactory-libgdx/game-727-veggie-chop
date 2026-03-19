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

public class KitchenSelectScreen implements Screen {

    private final MainGame game;

    private OrthographicCamera camera;
    private StretchViewport    viewport;
    private Stage              stage;
    private ShapeRenderer      sr;

    private static final String BG = "backgrounds/menu/2.png";

    // Card layout (3 cards stacked vertically — portrait layout)
    private static final float CARD_W   = Constants.CARD_WIDTH;
    private static final float CARD_H   = Constants.CARD_HEIGHT;
    private static final float CARD_GAP = 24f;

    private float[] cardX = new float[3];
    private float[] cardY = new float[3];

    private float backBtnX, backBtnY;

    private GlyphLayout titleLayout;
    private GlyphLayout tutorialLayout;

    // Card colors
    private static final Color COLOR_PIZZERIA = new Color(0xE53935FF);
    private static final Color COLOR_SUSHI    = new Color(0x1565C0FF);
    private static final Color COLOR_CANTINA  = new Color(0xF57F17FF);
    private static final Color COLOR_CARD_SHD = new Color(0f, 0f, 0f, 0.35f);

    private static final String[] KITCHEN_NAMES = { "PIZZERIA", "SUSHI BAR", "CANTINA" };

    public KitchenSelectScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        loadAssets();

        titleLayout    = new GlyphLayout(game.fontTitle, "CHOOSE KITCHEN");
        tutorialLayout = new GlyphLayout(game.fontSmall, "TAP A KITCHEN TO START");

        setupLayout();
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

    private void setupLayout() {
        float cx = Constants.WORLD_WIDTH / 2f;

        // 3 cards stacked vertically, centered
        float totalH = 3 * CARD_H + 2 * CARD_GAP;
        float startY = (Constants.WORLD_HEIGHT - totalH) / 2f - 20f;

        for (int i = 0; i < 3; i++) {
            cardX[i] = cx - CARD_W / 2f;
            cardY[i] = startY + (2 - i) * (CARD_H + CARD_GAP);
        }

        backBtnX = cx - Constants.BTN_WIDTH_SEC / 2f;
        backBtnY = 30f;
    }

    private void setupActors() {
        // Kitchen cards
        for (int i = 0; i < 3; i++) {
            final int kitchenId = i;
            Actor card = new Actor();
            card.setBounds(cardX[i], cardY[i], CARD_W, CARD_H);
            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    playClick();
                    game.setScreen(new GameScreen(game, kitchenId));
                }
            });
            stage.addActor(card);
        }

        // Back / Tutorial button
        Actor backActor = new Actor();
        backActor.setBounds(backBtnX, backBtnY, Constants.BTN_WIDTH_SEC, Constants.BTN_HEIGHT_SEC);
        backActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playBack();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backActor);
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
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1.0f);
    }

    private void playBack() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_back.ogg"))
            game.manager.get("sounds/sfx/sfx_button_back.ogg", Sound.class).play(1.0f);
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

        // Background
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        game.fontTitle.setColor(UiFactory.COLOR_TEXT);
        game.fontTitle.draw(game.batch, titleLayout,
                (Constants.WORLD_WIDTH - titleLayout.width) / 2f,
                Constants.WORLD_HEIGHT * 0.92f);

        // Subtitle hint
        game.fontSmall.setColor(UiFactory.COLOR_TEXT);
        game.fontSmall.draw(game.batch, tutorialLayout,
                (Constants.WORLD_WIDTH - tutorialLayout.width) / 2f,
                Constants.WORLD_HEIGHT * 0.84f);
        game.batch.end();

        // Draw kitchen cards
        drawCards();

        // Back button
        UiFactory.drawButton(sr, game.batch, game.fontBody, "BACK",
                backBtnX, backBtnY, Constants.BTN_WIDTH_SEC, Constants.BTN_HEIGHT_SEC);

        stage.act(delta);
        stage.draw();
    }

    private void drawCards() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setProjectionMatrix(camera.combined);
        float r = 16f;  // card corner radius

        Color[] colors = { COLOR_PIZZERIA, COLOR_SUSHI, COLOR_CANTINA };

        for (int i = 0; i < 3; i++) {
            float x = cardX[i], y = cardY[i], w = CARD_W, h = CARD_H;

            // Shadow
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(COLOR_CARD_SHD);
            drawRoundedRect(x + 4, y - 4, w, h, r);
            sr.end();

            // Card fill
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(colors[i]);
            drawRoundedRect(x, y, w, h, r);
            sr.end();

            // Kitchen name
            GlyphLayout nameLayout = new GlyphLayout(game.fontBody, KITCHEN_NAMES[i]);
            game.batch.begin();
            game.fontBody.setColor(UiFactory.COLOR_TEXT);
            game.fontBody.draw(game.batch, nameLayout,
                    x + (w - nameLayout.width) / 2f,
                    y + h / 2f + nameLayout.height / 2f);
            game.batch.end();

            // Sub-label with veggie names
            String[] subLabels = {
                "Tomato · Basil · Garlic",
                "Cucumber · Carrot · Avocado",
                "Jalapeño · Lime · Onion"
            };
            GlyphLayout subLayout = new GlyphLayout(game.fontSmall, subLabels[i]);
            game.batch.begin();
            game.fontSmall.setColor(new Color(1f, 1f, 1f, 0.8f));
            game.fontSmall.draw(game.batch, subLayout,
                    x + (w - subLayout.width) / 2f,
                    y + h / 2f - subLayout.height - 6f);
            game.batch.end();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
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
