package com.bow.game.control;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bow.game.BowGame;
import com.bow.game.model.Crosshair;
import com.bow.game.model.Spell;
import com.bow.game.model.mobs.Boss666;

public class EndlessLevelController extends LevelController {
    public EndlessLevelController(BowGame game) {
        super(game);
    }

    @Override
    public void handle() {
        super.handle();

        if (wall.getPercentHealthPoints() <= 0) {
            restartGame();
            game.setScreen(game.menuScreen);
        }
        else if (wall.getPercentHealthPoints() <= 50)
            wall.brake(game.assets.getTexture("wall2"));

        if (bossFIGHT) {
            boss = new Boss666(game.assets.getTexture("boss2"),
                    - 4f, height / 2, 8f, 8f, 600f, (float) Integer.MAX_VALUE);
            boss.targetSpawn(-boss.getWidth() / 2, height / 2, 0f, -1f);
            enemies.add(boss);
            time = 0;
            bossFIGHT = false;
        }
    }

    @Override
    public void restartGame() {
        super.restartGame();
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    protected void initParams() {
        spawnInterval = game.prefs.getFloat("spawnIntervalEndless", 5f);
        time = spawnInterval;
        bossFIGHT = false;
        xp = 0;
        yp = 0;
        jtx = 0;
        jty = 0;
    }

    @Override
    protected void initObjects() {
        super.initObjects();
        Crosshair crosshair = new Crosshair(game.assets.getTexture("crosshair"),
                0, 0, 9f, 9f);
        Crosshair crosshair1 = new Crosshair(game.assets.getTexture("crosshair"),
                0, 0, 4f, 4f);
        spellExplosion = new Spell(game.assets.getTexture("explosionSpellButtonOff"),
                -width / 2, -2f, 3f, 3f, crosshair);
        spellKnight = new Spell(game.assets.getTexture("knightSpellButtonOff"),
                -width / 2, 1.5f, 3f, 3f, crosshair1);
    }
}

