package com.bow.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bow.game.model.Blood;
import com.bow.game.model.Explosion;
import com.bow.game.model.HealthBar;
import com.bow.game.model.mobs.Dog;
import com.bow.game.model.mobs.Zombie;
import com.bow.game.utils.Assets;
import com.bow.game.view.GameScreen;
import com.bow.game.view.LevelSelector;
import com.bow.game.view.MainMenuScreen;
import com.bow.game.view.PauseMenuScreen;

public class BowGame extends Game {
	public Preferences prefs;
	public Screen gameScreen;
	public Screen menuScreen;
	public Screen pauseScreen;
	public Screen levelSelector;
	public SpriteBatch batch;
	public Assets assets;

	//TODO settings
	public final int SURVIVAL = 1;
	public final int ENDLESS = 2;
	private int gamemode;

	@Override
	public void create () {
	    prefs = Gdx.app.getPreferences("My Preferences");
		assets = new Assets(this);
		batch = new SpriteBatch();
        initializeStatics();
		gameScreen = new GameScreen(this);
		menuScreen = new MainMenuScreen(this);
		pauseScreen = new PauseMenuScreen(this);
		levelSelector = new LevelSelector(this);

		gamemode = SURVIVAL;
		this.setScreen(menuScreen);
	}

	private void initializeStatics() {
        HealthBar.setTextureRegions(assets.getAtlas("atlasHP.atlas"));
		Explosion.setTextureRegions(assets.getTexture("explosion"));
        Zombie.setTextures(assets.getTexture("zombie"));
        Blood.setTextureRegion(assets.getTexture("blood"));
        Dog.setTextureRegion(assets.getTexture("dog"));
    }

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();
		prefs.flush();
		batch.dispose();
		gameScreen.dispose();
		menuScreen.dispose();
		assets.dispose();
	}

	public int getGamemode() {
		return gamemode;
	}

	public void setGamemode(int gamemode) {
		this.gamemode = gamemode;
	}
}
