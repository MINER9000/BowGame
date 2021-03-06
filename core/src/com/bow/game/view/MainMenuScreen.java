package com.bow.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bow.game.BowGame;
import com.bow.game.control.MainMenuController;
import com.bow.game.utils.Assets;

public class MainMenuScreen implements Screen {

    private BowGame game;
    private OrthographicCamera camera;
    private MainMenuController mainMenuController;

    //TODO
    private boolean paused;

    public static final float cameraWidth = 20f;
    public static float deltaCff;

    public MainMenuScreen(BowGame game) {
        this.game = game;
        mainMenuController = new MainMenuController(game);
        game.assets.playMusic("theme", 0.5f);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        paused = false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        deltaCff = delta;

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        if (!paused) mainMenuController.handle();
        mainMenuController.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void show() {
        mainMenuController.sync();
        game.assets.playMusic("theme", 0.5f);
        paused = false;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) height / width;
        camera = new OrthographicCamera(cameraWidth, cameraWidth * aspectRatio);
        camera.update();
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        game.assets.playMusic("theme", 0.5f);
        mainMenuController.sync();
        paused = false;
    }

    @Override
    public void hide() {
        pause();
    }

    @Override
    public void dispose() {
        game.dispose();
        mainMenuController.dispose();
    }
}
