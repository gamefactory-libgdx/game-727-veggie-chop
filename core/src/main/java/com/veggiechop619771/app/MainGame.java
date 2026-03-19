package com.veggiechop619771.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.veggiechop619771.app.screens.MainMenuScreen;

public class MainGame extends Game {

    // Shared rendering resources
    public SpriteBatch  batch;
    public AssetManager manager;

    // Shared fonts (generated once, used by all screens)
    public BitmapFont fontTitle;   // Crackman.otf — large titles / scores
    public BitmapFont fontBody;    // Ferrum.otf   — body text / buttons
    public BitmapFont fontSmall;   // Ferrum.otf small — labels / HUD

    // Audio state
    public boolean musicEnabled = true;
    public boolean sfxEnabled   = true;
    public Music   currentMusic = null;

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();

        generateFonts();

        setScreen(new MainMenuScreen(this));
    }

    // -------------------------------------------------------------------------
    // Font generation
    // -------------------------------------------------------------------------

    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Crackman.otf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Ferrum.otf"));

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Title / score font — large
        p.size        = Constants.FONT_SIZE_TITLE;
        p.borderWidth = 3;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        p.color       = Color.WHITE;
        fontTitle = titleGen.generateFont(p);

        // Header font — medium
        p.size        = Constants.FONT_SIZE_HEADER;
        p.borderWidth = 2;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        fontBody = bodyGen.generateFont(p);

        // Small label font
        p.size        = Constants.FONT_SIZE_SMALL;
        p.borderWidth = 1;
        p.borderColor = new Color(0f, 0f, 0f, 0.7f);
        fontSmall = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    // -------------------------------------------------------------------------
    // Music helpers
    // -------------------------------------------------------------------------

    /** Play a looping music track. Does nothing if the same track is already playing. */
    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Play a non-looping music track (game over jingle). */
    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    // -------------------------------------------------------------------------
    // Dispose
    // -------------------------------------------------------------------------

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        fontTitle.dispose();
        fontBody.dispose();
        fontSmall.dispose();
    }
}
