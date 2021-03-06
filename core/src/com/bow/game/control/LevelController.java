package com.bow.game.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.bow.game.BowGame;
import com.bow.game.model.Ammo;
import com.bow.game.model.Arrow;
import com.bow.game.model.Background;
import com.bow.game.model.Blood;
import com.bow.game.model.Bow;
import com.bow.game.model.Bullet;
import com.bow.game.model.Button;
import com.bow.game.model.Explosion;
import com.bow.game.model.Spell;
import com.bow.game.model.Turret;
import com.bow.game.model.Wall;
import com.bow.game.model.Weapon;
import com.bow.game.model.mobs.Ally;
import com.bow.game.model.mobs.Boss666;
import com.bow.game.model.mobs.Dog;
import com.bow.game.model.mobs.Enemy;
import com.bow.game.model.mobs.Knight;
import com.bow.game.model.mobs.Zombie;
import com.bow.game.view.GameScreen;

import java.util.ArrayList;
import java.util.Random;

public abstract class LevelController {
    static Random random;
    protected BowGame game;

    public BitmapFont font;

    //Game Objects
    Background background;
    ArrayList<Enemy> enemies;
    ArrayList<Ally> allies;
    Weapon weapon;
    ArrayList<Ammo> ammunition;
    Wall wall;
    Boss666 boss;
    ArrayList<Blood> bloodMap;
    Button pauseButton;
    Spell spellExplosion;
    Spell spellKnight;
    Background wallFloor;

    //Sounds
    Sound swordHitSound;
    Sound swordSound;
    Sound scream;
    Sound explosionSound;
    Sound hitSound;
    Sound bricksSound;
    Sound shootBowSound;
    Sound shootTurretSound;
    Sound reloadedSound;
    Sound buttonSound;

    //Params
    boolean bossFIGHT;
    float spawnInterval;
    float time;
    float xp = 0;
    float yp = 0;
    float jtx = 0;
    float jty = 0;

    float width = GameScreen.cameraWidth;
    float height = width * (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

    LevelController(BowGame game) {
        this.game = game;
        random = new Random();
        font = new BitmapFont(Gdx.files.internal("font123.fnt"), Gdx.files.internal("font123.png"), false);
        font.getData().setScale(0.05f);
        font.setColor(Color.GOLD);
        init();
    }

    private void init() {
        initParams();
        initObjects();
        initSounds();
    }

    public void handle() {
        handleGame();
        for (Ammo ammo : ammunition) ammo.handle();

        weapon.handle();
        wallFloor.handle();
        pauseButton.handle();

        spellExplosion.handle();
        if (!spellExplosion.isOnCD()) spellExplosion.setSprite(game.assets.getTexture("explosionSpellButtonOn"));
        spellKnight.handle();
        if (!spellKnight.isOnCD()) spellKnight.setSprite(game.assets.getTexture("knightSpellButtonOn"));


        for (int i = ammunition.size() - 1; i >= 0; i--) {
            Ammo ammo = ammunition.get(i);
            if (ammo.isReadyToDelete() || ammo.getY() > height / 2)
                ammunition.remove(ammo);
        }

        if (Gdx.input.justTouched()) {
            jtx = (float) Gdx.input.getX() / Gdx.graphics.getWidth() * width - width / 2;
            jty = height - (float) Gdx.input.getY() / Gdx.graphics.getHeight() * height - height / 2;

            pauseButton.setToggled(pauseButton.getBounds().contains(jtx, jty));
            spellExplosion.setToggled(spellExplosion.getBounds().contains(jtx, jty));
            spellKnight.setToggled(spellKnight.getBounds().contains(jtx, jty));
        }


        if (Gdx.input.isTouched()) {
            xp = (float) Gdx.input.getX() / Gdx.graphics.getWidth() * width - width / 2;
            yp = height - (float) Gdx.input.getY() / Gdx.graphics.getHeight() * height - height / 2;

            if (spellExplosion.isToggled() && !spellExplosion.isOnCD() && !spellExplosion.getBounds().contains(xp, yp)) {
                spellExplosion.getCrosshair().setPosition(xp - spellExplosion.getCrosshair().getWidth() / 2, yp - spellExplosion.getCrosshair().getHeight() / 2);
                spellExplosion.getCrosshair().setDrawn(true);
            }
            else spellExplosion.getCrosshair().setDrawn(false);

            if (spellKnight.isToggled() && !spellKnight.isOnCD() && !spellKnight.getBounds().contains(xp, yp)) {
                spellKnight.getCrosshair().setPosition(xp - spellKnight.getCrosshair().getWidth() / 2, yp - spellKnight.getCrosshair().getHeight() / 2);
                spellKnight.getCrosshair().setDrawn(true);
            }
            else spellKnight.getCrosshair().setDrawn(false);

            if (!spellExplosion.isToggled() && !spellKnight.isToggled()) {
                weapon.setPosition(xp - weapon.getWidth() / 2, weapon.getY());
                if (weapon.isLoaded() && weapon.isReadyToShoot()) {
                    weapon.shoot();
                    ammunition.add(Ammo.copy(weapon.getAmmo()));
                    ammunition.get(ammunition.size() - 1).shoot();

                    if (weapon instanceof Turret) game.assets.playSound(shootTurretSound, 0.2f);
                    if (weapon instanceof Bow) game.assets.playSound(shootBowSound, 0.1f);

                }
            }

        }
        else {
            if (pauseButton.isToggled() && pauseButton.getBounds().contains(xp, yp)) {
                game.assets.playSound(buttonSound, 1f);
                pauseButton.setToggled(false);
                game.gameScreen.pause();
                game.setScreen(game.pauseScreen);
            }
            else if (spellExplosion.isToggled() && !spellExplosion.getBounds().contains(xp, yp)) {
                game.assets.playSound(explosionSound, 1f);
                spellExplosion.setToggled(false);
                spellExplosion.setOnCD(true);
                spellExplosion.getCrosshair().setDrawn(false);
                spellExplosion.setSprite(game.assets.getTexture("explosionSpellButtonOff"));
                allies.add(new Explosion(xp -6f, yp -6f, 12f, 12f,50f));
            }
            else if (spellKnight.isToggled() && !spellKnight.getBounds().contains(xp, yp))  {
                game.assets.playSound(swordSound, 1f);
                spellKnight.setToggled(false);
                spellKnight.setOnCD(true);
                spellKnight.getCrosshair().setDrawn(false);
                spellKnight.setSprite(game.assets.getTexture("knightSpellButtonOff"));
                Knight knight = new Knight(game.assets.getTexture("knight"),
                        xp -1.5f ,yp - 1.5f * 0.906f, 3f,3f * 0.906f,800, 25f, 2f);
                knight.targetSpawn(xp -1.5f, yp -1.5f * 0.906f, 0f, 1f);
                knight.setPosition(knight.getX(), Math.max(knight.getY(), wall.getY() + wall.getHeight()));
                allies.add(knight);
            }
        }
    }

    private void handleGame() {
        for (Enemy enemy : enemies) enemy.handle();
        for (Ally ally : allies) ally.handle();

        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            for (Ally ally : allies) {
                if (Intersector.overlapConvexPolygons(enemy.getBounds(), ally.getBounds())) {
                    if (ally instanceof Knight) game.assets.playSound(swordHitSound, 0.2f);
                    else if (ally instanceof Wall) game.assets.playSound(bricksSound, 0.7f);

                    enemy.damaged(ally.getDamage());
                    if (enemy.isRepelable()) enemy.repel(ally.getRepelPower());
                    ally.damaged(enemy.getDamage());
                    if (ally.isRepelable()) ally.repel(enemy.getRepelPower());
                }
            }
            if (enemy.getPercentHealthPoints() <= 0) {
                if (enemy instanceof Boss666) game.assets.playSound(scream, 0.5f);
                enemies.remove(enemy);
                game.prefs.putInteger("cash", 0);
                game.prefs.putInteger("cash", game.prefs.getInteger("cash", 0) + enemy.getValue()).flush();
                bloodMap.add(new Blood(enemy, random));
            }
        }

        for (int i = allies.size() - 1; i >= 0; i--) {
            Ally ally = allies.get(i);
            if (ally instanceof Explosion) ally.setDamage(0f);
            if (ally.getPercentHealthPoints() <= 0 || ally.getY() > height / 2) {
                allies.remove(ally);
            }
        }

        time += GameScreen.deltaCff;
        spawnInterval -= 0.005 * GameScreen.deltaCff;
        if (time > spawnInterval) {
            int param = random.nextInt(6);
            int adjustZombies = 1;
            for (int i = 0; i < param + adjustZombies; i++) {
                enemies.add(new Zombie(-width, height / 2, 3f, 3f * 1.12f, 100f, 50f,-2f));
                enemies.get(enemies.size() - 1).randomSpawn(random, width, 0f, -1.5f);
            }
            for (int i = 0; i < 5 - param; i++) {
                enemies.add(new Dog(-width, height / 2, 2f, 2f * 2.8125f, 50f, 20f,-1f));
                enemies.get(enemies.size() - 1).randomSpawn(random, width, 0f, -4f);
            }
            time = 0;
        }

        for (Ammo ammo : ammunition) {
            for (Enemy enemy : enemies) {
                if (Intersector.overlapConvexPolygons(enemy.getBounds(), ammo.getBounds())) {
                    enemy.damaged(ammo.getDamage(random));
                    enemy.repel(ammo.getRepelDist());
                    game.assets.playSound(hitSound,0.8f);
                    ammo.delete();
                }
            }
        }

        if (bloodMap.size() > 200) {
            bloodMap.remove(0);
        }
    }

    public void restartGame() {
        game.assets.stopMusic("music");
        init();
        game.assets.playMusic("music", 0.15f);
    }

    public void draw(SpriteBatch batch) {
        background.draw(batch);
        wallFloor.draw(batch);
        for (Blood blood : bloodMap) blood.draw(batch);

        for (Enemy enemy : enemies) enemy.draw(batch);
        for (Ally ally : allies) ally.draw(batch);

        weapon.draw(batch);
        for (Ammo ammo : ammunition) ammo.draw(batch);
        pauseButton.draw(batch);
        spellExplosion.draw(batch);
        spellKnight.draw(batch);
        font.draw(game.batch, game.prefs.getInteger("cash", 0) + "$", - width / 2 + 1f, height / 2 - 1f);
    }

    protected abstract void initParams();

    protected void initObjects() {
        this.enemies = new ArrayList<>();
        this.bloodMap = new ArrayList<>();
        this.ammunition = new ArrayList<>();
        this.allies = new ArrayList<>();

        weapon = initWeapon(initAmmo(game.prefs.getString("ammoEquip", "Arrow")), game.prefs.getString("weaponEquip", "Bow"));

        wall = new Wall(game.assets.getTexture("wall1"),
                -width / 2, 4f -height / 2, 2f * 28.96f, 2f, game.prefs.getFloat("WallHP", 300f), width, 3f);
        allies.add(wall);
        wallFloor = new Background(game.assets.getTexture("wallFloor"),
                -width / 2, - height / 2, 5f * 5.875f, 5f);
        background = new Background(game.assets.getTexture("grass"),
                -width / 2, -height / 2, height * 1f, height);
        pauseButton = new Button(game.assets.getTexture("pauseButton"),
                width / 2 - 2f * 1.275f - 0.5f, height / 2 - 2f - 0.5f, 2f * 1.275f, 2f);
    }

    private Ammo initAmmo(String ammoEquip) {
        switch (ammoEquip) {
            case ("Arrow"): {
                return new Arrow(game.assets.getTexture("poisonArrow"),
                        2.25f, -height / 2, 0.5f, 0.5f * 5.6153f, 25f, 0.05f, 100f, 0.3f, 25f);
            }
            case ("Bullet"): {
                return new Bullet(game.assets.getTexture("bullet"),
                        0f, -height / 2, 0.5f, 0.5f * 3.555f,
                        20f, 0.01f, 100f, 0.1f, 40f);
            }
            default: {
                return new Arrow(game.assets.getTexture("poisonArrow"),
                        2.25f, -height / 2, 0.5f, 0.5f * 5.6153f, 25f, 0.05f, 100f, 0.3f, 25f);
            }
        }
    }

    private Weapon initWeapon(Ammo ammoInWeapon, String weaponEquip) {
        switch (weaponEquip) {
            case ("Bow"): {
                return new Bow(game.assets.getTexture("bowLVL2"),
                        0f, -height / 2, 5f, 5f * 0.3255f, ammoInWeapon, 0.5f);
            }
            case ("Turret"): {
                return new Turret(game.assets.getTexture("turret"),
                        0f, -height / 2, 3f, 3f * 1.435f, ammoInWeapon, 30, 2.0f, 0.1f);
            }
            default: {
                return new Bow(game.assets.getTexture("bowLVL2"),
                        0f, -height / 2, 5f, 5f * 0.3255f, ammoInWeapon, 0.0f);
            }
        }
    }

    private void initSounds() {

        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.ogg"));
        shootBowSound = Gdx.audio.newSound(Gdx.files.internal("shootBow.ogg"));
        bricksSound = Gdx.audio.newSound(Gdx.files.internal("bricks.ogg"));
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("soundButton.ogg"));
        scream = Gdx.audio.newSound(Gdx.files.internal("scream.ogg"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        swordSound = Gdx.audio.newSound(Gdx.files.internal("knight.ogg"));
        swordHitSound = Gdx.audio.newSound(Gdx.files.internal("swordHit.ogg"));
        shootTurretSound = Gdx.audio.newSound(Gdx.files.internal("shootTurret.ogg"));
        reloadedSound = Gdx.audio.newSound(Gdx.files.internal("reloaded.ogg"));
    }

    public void dispose() {
        game.dispose();
    }
}


