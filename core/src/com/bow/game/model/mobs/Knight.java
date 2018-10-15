package com.bow.game.model.mobs;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bow.game.model.Enemy;

public class Knight extends Enemy {

    public Knight(TextureRegion texture, TextureAtlas HPtextureAtlas, float x, float y, float width, float height, float maxHealthPoints, float damage) {
        super(texture, HPtextureAtlas, x, y, width, height, maxHealthPoints, damage);
    }
}
