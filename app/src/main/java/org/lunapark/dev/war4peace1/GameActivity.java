package org.lunapark.dev.war4peace1;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.lunapark.dev.war4peace1.managers.BotFather;
import org.lunapark.dev.war4peace1.managers.ObjectManager;
import org.lunapark.dev.war4peace1.managers.SoundManager;
import org.lunapark.dev.war4peace1.managers.TextureManager;
import org.lunapark.dev.war4peace1.managers.WorldManager;
import org.lunapark.dev.war4peace1.objects.Body2d;
import org.lunapark.dev.war4peace1.objects.Bot;
import org.lunapark.dev.war4peace1.objects.Bullet;
import org.lunapark.dev.war4peace1.objects.Character;
import org.lunapark.dev.war4peace1.objects.CharacterData;

import java.util.ArrayList;
import java.util.Locale;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.OpenGLCamera;
import fr.arnaudguyon.smartgl.opengl.RenderPassObject3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassSprite;
import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;
import fr.arnaudguyon.smartgl.opengl.SmartGLViewController;
import fr.arnaudguyon.smartgl.opengl.Sprite;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static org.lunapark.dev.war4peace1.utils.Consts.BOT_SPEED;
import static org.lunapark.dev.war4peace1.utils.Consts.CAMERA_SPEED;
import static org.lunapark.dev.war4peace1.utils.Consts.CAMERA_X_ANGLE;
import static org.lunapark.dev.war4peace1.utils.Consts.CAMERA_Y;
import static org.lunapark.dev.war4peace1.utils.Consts.MAX_BULLETS;
import static org.lunapark.dev.war4peace1.utils.Consts.SPEED_PLAYER;

public class GameActivity extends Activity implements SmartGLViewController {

    private static final String TAG = "War4peace";

    // Android
    private SmartGLView mSmartGLView;
    private int screenX, screenXhalf;
    private TextView tvInfo;
    private float globalDelta;
    private Handler handler;
    private Runnable updateTextView;

    // Engine
    private ObjectManager objectManager;
    private TextureManager textureManager;
    private WorldManager worldManager;
    private SoundManager soundManager;
    private BotFather botFather;

    // Textures
    private Texture txFire;
    private Texture txBody, txLeg, txPlayer, txTransparent, txDead, txEnemy;

    // Player
    private int dx, dz;
    private Character player;
    private ArrayList<Bot> enemies;

    // Game
    private boolean gameover = false;

    // Camera
    private float cameraTargetX, cameraTargetZ, cameraX, cameraZ;

    // Gun fire
    private boolean fire;
    private ArrayList<Bullet> bullets;

    // Joystick
    private boolean joyVisible = false;
    private float joyDeltaX, joyDeltaY;
    private int knobLimitDistance, joyTolerance;
    private Sprite joyBaseSprite, joyKnobSprite;
    private float joyX, joyY;
    private Texture joyTexture, knobTexture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Get screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenX = size.x;
        screenXhalf = screenX / 2;

        //
        mSmartGLView = (SmartGLView) findViewById(R.id.smartGLView);
        mSmartGLView.setDefaultRenderer(this);
        mSmartGLView.setController(this);

        tvInfo = (TextView) findViewById(R.id.tvInfo);


        handler = new Handler();
        updateTextView = new Runnable() {
            @Override
            public void run() {
                tvInfo.setText(String.format(Locale.US, "FPS: %.1f", 1 / globalDelta));
            }
        };


        Button btnFire = (Button) findViewById(R.id.btnFire);
        btnFire.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        fire = true;
                        break;
                    case ACTION_UP:
                        fire = false;
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void onPrepareView(SmartGLView smartGLView) {
        SmartGLRenderer renderer = smartGLView.getSmartGLRenderer();

        RenderPassObject3D.ShaderType shaderType = RenderPassObject3D.ShaderType.SHADER_TEXTURE;
        RenderPassObject3D renderPassObject3D = new RenderPassObject3D(shaderType, true, true);

        RenderPassSprite renderPassSprite = new RenderPassSprite();

        renderer.addRenderPass(renderPassObject3D);
        renderer.addRenderPass(renderPassSprite);

        // Camera settings
        renderer.setClearColor(0, 0, 0, 1);
        renderer.getCamera().setRotation(CAMERA_X_ANGLE, 0, 0);

//        renderer.getCamera().setFOV(100);

        objectManager = new ObjectManager(this, renderPassObject3D);
        textureManager = new TextureManager();

        worldManager = new WorldManager(this, textureManager, objectManager);

        soundManager = new SoundManager(this);

        worldManager.defineLevelFloor();
        worldManager.defineLevelWalls();

        txFire = textureManager.createTexture(Color.YELLOW);
        txTransparent = textureManager.createTexture(Color.TRANSPARENT);
        txLeg = textureManager.createTexture(this, R.drawable.camo);
        txPlayer = textureManager.createTexture(this, R.drawable.survivor);
        txDead = textureManager.createTexture(Color.RED);
        txEnemy = textureManager.createTexture(this, R.drawable.enemy);

        int a = 10;
        int b = 10;

        // Define enemies and their dead bodies
        enemies = new ArrayList<>();
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                Bot enemy = new Bot(objectManager, soundManager);
                enemy.defineDeadBody(txDead, 2);
                enemies.add(enemy);
            }
        }

        // Define player and his dead body
        player = new Character(objectManager, soundManager);
        player.defineDeadBody(txDead, 2);

        // Define enemies textures
        int c = 0;
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                Character enemy = enemies.get(c);
                enemy.define(txEnemy, txLeg, txFire, 2.5f, 1);
                enemy.setBulletSpawn(1.4f, 0.26f);
                enemy.getBase().setPos(10 * i + 10, 1, 30 - 20 * j);
                c++;
            }
        }

        // Define player textures
        player.define(txPlayer, txLeg, txFire, 2.5f, 1);
        player.setBulletSpawn(1.4f, 0.26f);

        defineBullets();
        defineJoystick(renderPassSprite);
        botFather = new BotFather(worldManager);
    }

    private void defineJoystick(RenderPassSprite renderPassSprite) {
        // Create joystick
        joyTexture = textureManager.createTexture(this, R.drawable.gamepad);
        knobTexture = textureManager.createTexture(this, R.drawable.knob);

        int joySize = screenX / 4;
        knobLimitDistance = joySize / 3;
        joyTolerance = knobLimitDistance / 5;

        joyBaseSprite = new Sprite(joySize, joySize);
        joyBaseSprite.setPivot(0.5f, 0.5f);
        joyBaseSprite.setTexture(joyTexture);
        joyBaseSprite.setVisible(joyVisible);

        joyKnobSprite = new Sprite(joySize / 2, joySize / 2);
        joyKnobSprite.setPivot(0.5f, 0.5f);
        joyKnobSprite.setTexture(knobTexture);
        joyKnobSprite.setVisible(joyVisible);

        renderPassSprite.addSprite(joyBaseSprite);
        renderPassSprite.addSprite(joyKnobSprite);
    }

    private void defineBullets() {
        bullets = new ArrayList<>();
        for (int i = 0; i < MAX_BULLETS; i++) {
            Object3D bulletObj = objectManager.createObject(R.raw.plane, txFire);
            bulletObj.setScale(0.2f, 0.05f, 0.05f);
            Body2d body2d = new Body2d();
            body2d.width = 0.2f;
            body2d.height = 0.2f;
            Bullet bullet = new Bullet(bulletObj, body2d);
            bullets.add(bullet);
        }
    }

    @Override
    public void onReleaseView(final SmartGLView smartGLView) {
        textureManager.dispose();
        if (smartGLView != null) {
            smartGLView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    smartGLView.destroyDrawingCache();
                }
            });
        }

        soundManager.dispose();
    }

    private void update(float delta, OpenGLCamera camera) {

        float playerX = player.getBase().getPosX();
        float playerY = player.getBase().getPosY();
        float playerZ = player.getBase().getPosZ();

        float newX = playerX + dx * delta * SPEED_PLAYER;
        float newZ = playerZ + dz * delta * SPEED_PLAYER;

        boolean collision = worldManager.checkWallwithCharIntersect(newX, newZ);

        if (!collision) {
            playerX = newX;
            playerZ = newZ;
        }
        player.update(delta, dx, dz, playerX, playerY, playerZ, !collision);
        if (fire) gunfire(player);

        updateEnemy(delta);

        updateCamera(delta, camera, playerX, playerY, playerZ);
        updateBullets(delta);

        worldManager.updateFloor(playerX, playerZ);
    }

    private void updateEnemy(float delta) {
        for (int i = 0; i < enemies.size(); i++) {
            Bot enemy = enemies.get(i);

            float x = enemy.getBase().getPosX();
            float y = enemy.getBase().getPosY();
            float z = enemy.getBase().getPosZ();

            if (enemy.getHealth() > 0) {
                CharacterData data = botFather.getData(enemy, player);

                int dx = data.deltaX;
                int dz = data.deltaZ;
                if (data.canShoot) gunfire(enemy);

                float newX = x + dx * delta * BOT_SPEED;
                float newZ = z + dz * delta * BOT_SPEED;

                boolean collision = worldManager.checkWallwithCharIntersect(newX, newZ);

                if (!collision) {
                    x = newX;
                    z = newZ;
                }
                enemy.update(delta, dx, dz, x, y, z, !collision);
            } else {
                enemy.update(delta, dx, dz, x, y, z, false);
            }

        }
    }

    private void updateCamera(float delta, OpenGLCamera camera, float playerX, float playerY, float playerZ) {
        float camX = playerX + cameraX;
        float camZ = playerZ + cameraZ;
        if (cameraZ > cameraTargetZ) {
            cameraZ -= delta * CAMERA_SPEED;
        }
        if (cameraZ < cameraTargetZ) {
            cameraZ += delta * CAMERA_SPEED;
        }
        if (cameraX > cameraTargetX) {
            cameraX -= delta * CAMERA_SPEED;
        }
        if (cameraX < cameraTargetX) {
            cameraX += delta * CAMERA_SPEED;
        }
        camera.setPosition(camX, playerY + CAMERA_Y, camZ);
    }

    // Create new bullets
    private void gunfire(Character character) {
        Object3D bulletSpawn = character.getBulletSpawn();
        if (character.fire()) {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                if (!bullet.isVisible()) {
                    bullet.create(bulletSpawn.getPosX(), bulletSpawn.getPosY(), bulletSpawn.getPosZ(), bulletSpawn.getRotY() - 45);
                    break;
                }
            }
        }
    }

    private void updateBullets(float delta) {
        // Update current bullets
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet.isVisible()) {
                bullet.update(delta);
                if (worldManager.checkWallIntersect(bullet.getBody2d())) {
                    bullet.hide();
                    soundManager.playSoundMono(SoundManager.sfxImpact);
                }
                if (worldManager.intersectCharacter(bullet.getBody2d(), player.getBase().getPosX(), player.getBase().getPosZ())) {
//                    player.damage();
                    bullet.hide();
                    soundManager.playSoundMono(SoundManager.sfxImpact);
                }
                for (int j = 0; j < enemies.size(); j++) {
                    Character enemy = enemies.get(j);
                    if (enemy.getHealth() > 0 && worldManager.intersectCharacter(bullet.getBody2d(), enemy.getBase().getPosX(), enemy.getBase().getPosZ())) {
                        enemy.damage();
                        bullet.hide();
                        soundManager.playSoundMono(SoundManager.sfxImpact);
                    }
                }
            }
        }
    }

    @Override
    public void onResizeView(SmartGLView smartGLView) {

    }

    @Override
    public void onTick(SmartGLView smartGLView) {
        SmartGLRenderer renderer = smartGLView.getSmartGLRenderer();
        float frameDuration = renderer.getFrameDuration();
        if (frameDuration < 0.02f) frameDuration = 0.02f;
        if (frameDuration > 0.04f) frameDuration = 0.04f;
        globalDelta = frameDuration;
        handler.post(updateTextView);
        update(frameDuration, renderer.getCamera());
    }

    private void movePlayerUp() {
        dx = 0;
        dz = -1;
        cameraTargetX = 0;
        cameraTargetZ = -5;
    }

    private void movePlayerDown() {
        dx = 0;
        dz = 1;
        cameraTargetX = 0;
        cameraTargetZ = 5;
    }

    private void movePlayerLeft() {
        dx = -1;
        dz = 0;
        cameraTargetX = -5;
        cameraTargetZ = 0;
    }

    private void movePlayerRight() {
        dx = 1;
        dz = 0;
        cameraTargetX = 5;
        cameraTargetZ = 0;
    }

    @Override
    public void onTouchEvent(SmartGLView smartGLView, TouchHelperEvent touchHelperEvent) {
        TouchHelperEvent.TouchEventType type = touchHelperEvent.getType();

        int fingers = touchHelperEvent.getNbFingers();
        for (int i = 0; i < fingers; i++) {
            float touchX = touchHelperEvent.getX(i);
            float touchY = touchHelperEvent.getY(i);
            if (touchX < screenXhalf) {
                switch (type) {
                    case SINGLETOUCH:
                        joyVisible = true;
                        joyX = touchX;
                        joyY = touchY;
                        break;
                    case SINGLEMOVE:
                        if (joyVisible) {
                            float x = touchX - joyX;
                            float y = touchY - joyY;
                            float modX = Math.abs(x);
                            float modY = Math.abs(y);

                            if (modX > joyTolerance || modY > joyTolerance) {
                                if (modX > modY) {
                                    if (x > 0) {
                                        // move right
                                        joyDeltaX = knobLimitDistance;
                                        joyDeltaY = 0;
                                        movePlayerRight();
                                    } else {
                                        // move left
                                        joyDeltaX = -knobLimitDistance;
                                        joyDeltaY = 0;
                                        movePlayerLeft();
                                    }
                                } else {
                                    if (y > 0) {
                                        // move down
                                        joyDeltaX = 0;
                                        joyDeltaY = knobLimitDistance;
                                        movePlayerDown();
                                    } else {
                                        // move up
                                        joyDeltaX = 0;
                                        joyDeltaY = -knobLimitDistance;
                                        movePlayerUp();
                                    }
                                }
                            } else {
                                joyDeltaX = 0;
                                joyDeltaY = 0;
                                dz = 0;
                                dx = 0;
                            }
                        }
                        break;
                }
            }
        }

        if (type == TouchHelperEvent.TouchEventType.SINGLEUNTOUCH) {
            joyVisible = false;
            // Stop player
            dz = 0;
            dx = 0;
        }
        updateJoystick();
    }

    private void updateJoystick() {
        joyBaseSprite.setVisible(joyVisible);
        joyKnobSprite.setVisible(joyVisible);

        joyBaseSprite.setPos(joyX, joyY);
        joyKnobSprite.setPos(joyX + joyDeltaX, joyY + joyDeltaY);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                movePlayerUp();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_S:
                movePlayerDown();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                movePlayerLeft();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                movePlayerRight();
                return true;
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_SPACE:
                fire = true;
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                dz = 0;
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_S:
                dz = 0;
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                dx = 0;
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                dx = 0;
                return true;
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_SPACE:
                fire = false;
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    @Override
    protected void onPause() {
        if (mSmartGLView != null) {
            mSmartGLView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSmartGLView != null) {
            mSmartGLView.onResume();
        }
    }
}
