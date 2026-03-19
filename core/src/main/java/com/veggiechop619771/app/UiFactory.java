package com.veggiechop619771.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class UiFactory {

    // Assigned color palette
    public static final Color COLOR_PRIMARY    = new Color(0xF9A825FF);
    public static final Color COLOR_ACCENT     = new Color(0xE53935FF);
    public static final Color COLOR_BACKGROUND = new Color(0xBF360CFF);
    public static final Color COLOR_TEXT       = new Color(0xFFFFFFFF);

    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.4f);

    // Reusable layout — not thread-safe, but LibGDX is single-threaded
    private static final GlyphLayout tmpLayout = new GlyphLayout();

    /**
     * Draws a rounded-rectangle button with drop shadow.
     * Corner radius = h * 0.35. Fill: primary color. Text: white, centered.
     * Neither sr nor batch should be active (begun) when calling this.
     */
    public static void drawButton(ShapeRenderer sr, SpriteBatch batch, BitmapFont font,
                                  String label, float x, float y, float w, float h) {
        float r = h * 0.35f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setProjectionMatrix(batch.getProjectionMatrix());

        // Drop shadow (+3, -3 offset, 40% black)
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(SHADOW_COLOR);
        fillRounded(sr, x + 3, y - 3, w, h, r);
        sr.end();

        // Button fill
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(COLOR_PRIMARY);
        fillRounded(sr, x, y, w, h, r);
        sr.end();

        // Label centered in button
        tmpLayout.setText(font, label);
        batch.begin();
        font.setColor(COLOR_TEXT);
        font.draw(batch, tmpLayout,
                x + (w - tmpLayout.width)  * 0.5f,
                y + (h + tmpLayout.height) * 0.5f);
        batch.end();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static void fillRounded(ShapeRenderer sr, float x, float y, float w, float h, float r) {
        // Center horizontal strip
        sr.rect(x + r, y,     w - 2 * r, h);
        // Left vertical strip
        sr.rect(x,         y + r, r, h - 2 * r);
        // Right vertical strip
        sr.rect(x + w - r, y + r, r, h - 2 * r);
        // Four corner arcs
        sr.arc(x + r,     y + r,     r, 180, 90, 12);
        sr.arc(x + w - r, y + r,     r, 270, 90, 12);
        sr.arc(x + w - r, y + h - r, r,   0, 90, 12);
        sr.arc(x + r,     y + h - r, r,  90, 90, 12);
    }
}
