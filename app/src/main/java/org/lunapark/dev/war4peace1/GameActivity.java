package org.lunapark.dev.war4peace1;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.lunapark.dev.war4peace1.managers.ObjectManager;
import org.lunapark.dev.war4peace1.managers.SoundManager;
import org.lunapark.dev.war4peace1.managers.TextureManager;
import org.lunapark.dev.war4peace1.managers.WorldManager;
import org.lunapark.dev.war4peace1.objects.Body2d;
import org.lunapark.dev.war4peace1.objects.Bullet;
import org.lunapark.dev.war4peace1.objects.CharPlayer;

import java.util.ArrayList;

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
import static org.lunapark.dev.war4peace1.utils.Consts.CAMERA_SPEED;
import static org.lunapark.dev.war4peace1.utils.Consts.CAMERA_X_ANGLE;
import static org.lunapark.dev.war4peace1.utils.Consts.CAMERA_Y;
import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_RATE;
import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_SOURCE_ANGLE;
import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_SOURCE_RANGE;
import static org.lunapark.dev.war4peace1.utils.Consts.MAX_BULLETS;
import static org.lunapark.dev.war4peace1.utils.Consts.PLAYER_HEIGHT;
import static org.lunapark.dev.war4peace1.utils.Consts.PLAYER_WIDTH;
import static org.lunapark.dev.war4peace1.utils.Consts.SPEED_LEGS;
import static org.lunapark.dev.war4peace1.utils.Consts.SPEED_PLAYER;

public class GameActivity extends Activity implements SmartGLViewController {

    private static final String TAG = "War4peace";

    // Android
    private SmartGLView mSmartGLView;
    private int screenX, screenXhalf;

    // Engine
    private ObjectManager objectManager;
    private TextureManager textureManager;
    private WorldManager worldManager;
    private SoundManager soundManager;
    private ArrayList<Body2d> solids;

    // Textures
    private Texture txFire;
    private Texture txBody, txLeg, txPlayer;

    // Player
    private Object3D player, legLeft, legRight, body;
    private float legsAngle1, legsAngle2, legsMult = 1;

    private CharPlayer charPlayer;

    //    private float playerX, playerY, playerZ;
    private float dx, dz;

    // Game
    private boolean gameover = false;

    // Camera
    private float cameraTargetX, cameraTargetZ, cameraX, cameraZ;

    // Gun fire
    private Object3D bulletSpawn;
    private boolean fire;
    private long shootTime;
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
        solids = new ArrayList<>();
        worldManager = new WorldManager(this, textureManager, objectManager, solids);
        soundManager = new SoundManager(this);

        worldManager.defineLevelFloor();

        txFire = textureManager.createTexture(Color.YELLOW);

        definePlayer();

//        charPlayer = new CharPlayer(textureManager, objectManager);


        defineWeapon();
        worldManager.defineLevelWalls();
        defineJoystick(renderPassSprite);
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

    private void defPlayer() {
        txBody = textureManager.createTexture(this, R.drawable.survivor);
        txLeg = textureManager.createTexture(this, R.drawable.camo);
        charPlayer.definePlayer(txBody, 2.5f, 1, txLeg, 1.3f, 0.25f);
    }

    private void definePlayer() {
        txLeg = textureManager.createTexture(this, R.drawable.camo);
        legLeft = objectManager.createObject(R.raw.plane_l, txLeg);
        legLeft.setScale(1.3f, 1, 0.25f);

        legRight = objectManager.createObject(R.raw.plane_r, txLeg);
        legRight.setScale(1.3f, 1, 0.25f);

        txPlayer = textureManager.createTexture(Color.TRANSPARENT);
        player = objectManager.createObject(R.raw.plane, txPlayer);
        player.setPos(0, 1.3f, 0);

        txBody = textureManager.createTexture(this, R.drawable.survivor);
        body = objectManager.createObject(R.raw.plane, txBody);
        body.setScale(2.5f, 1, 1);

        bulletSpawn = objectManager.createObject(R.raw.plane, txFire);
        bulletSpawn.setScale(0.2f, 0.2f, 0.2f);
        bulletSpawn.setVisible(false);
    }

    private void defineWeapon() {

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
        float playerX = player.getPosX();
        float playerY = player.getPosY();
        float playerZ = player.getPosZ();

        updateCamera(delta, camera, playerX, playerY, playerZ);
        updatePlayer(delta, dx, dz, playerX, playerY, playerZ);
        updateBullets(delta, bulletSpawn);

        worldManager.updateFloor(player.getPosX(), player.getPosZ());
    }

    private void updatePlayer(float delta, float dx, float dz, float playerX, float playerY, float playerZ) {

        float newX = playerX + dx * delta * SPEED_PLAYER;
        float newZ = playerZ + dz * delta * SPEED_PLAYER;

        if (!checkWallPlayerIntersect(newX, newZ)) {
            playerX = newX;
            playerZ = newZ;
            if (dx != 0 || dz != 0) {
                if ((legsAngle1 >= 140) || (legsAngle1 <= 40)) {
                    legsMult = -legsMult;
                    soundManager.playSoundMono(SoundManager.sfxStep);
                }
                float legsDelta = SPEED_LEGS * legsMult;
                legsAngle1 += legsDelta;
                legsAngle2 -= legsDelta;
            } else {
                legsAngle1 = 90;
                legsAngle2 = 90;
            }
        } else {
            legsAngle1 = 90;
            legsAngle2 = 90;
        }

        float playerRotY = player.getRotY();
        player.setPos(playerX, playerY, playerZ);
        body.setPos(playerX, playerY, playerZ);
        body.setRotation(0, playerRotY, 0);
        body.addRotY((90 - legsAngle1) / 10);

        float bodyRotY = body.getRotY();

        legLeft.setPos(playerX, playerY, playerZ);
        legRight.setPos(playerX, playerY, playerZ);
        legLeft.setRotation(0, playerRotY, legsAngle1);
        legRight.setRotation(0, playerRotY, legsAngle2);

        // update bulletSpawn
        float phi = FIRE_SOURCE_ANGLE + bodyRotY;
        float fireSourceX = playerX - (float) (FIRE_SOURCE_RANGE * Math.cos(Math.toRadians(phi)));
        float fireSourceZ = playerZ + (float) (FIRE_SOURCE_RANGE * Math.sin(Math.toRadians(phi)));
        bulletSpawn.setPos(fireSourceX, playerY, fireSourceZ);
        bulletSpawn.setRotation(0, bodyRotY + 45, 0);
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

    private void updateBullets(float delta, Object3D bulletSpawn) {
        // Create new bullets
        long currentTime = System.currentTimeMillis();
        if ((currentTime - shootTime > FIRE_RATE) && fire) {
            shootTime = currentTime;
            bulletSpawn.setVisible(true);
            soundManager.playSoundMono(SoundManager.sfxShot);
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                if (!bullet.isVisible()) {
                    bullet.create(bulletSpawn.getPosX(), bulletSpawn.getPosY(), bulletSpawn.getPosZ(), bulletSpawn.getRotY() - 45);
                    break;
                }
            }

        } else {
            bulletSpawn.setVisible(false);
        }

        // Update current bullets
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet.isVisible()) {
                bullet.update(delta);
                if (checkWallIntersect(bullet.getBody2d())) {
                    bullet.hide();
                    soundManager.playSoundMono(SoundManager.sfxImpact);
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
        update(frameDuration, renderer.getCamera());
    }


    private boolean checkWallIntersect(Body2d body2d) {

        for (int i = 0; i < solids.size(); i++) {
            Body2d wall = solids.get(i);
            if (intersect(wall, body2d)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkWallPlayerIntersect(float pX, float pZ) {

        for (int i = 0; i < solids.size(); i++) {
            Body2d wall = solids.get(i);
            if (intersectPlayer(wall, pX, pZ)) {
                return true;
            }
        }
        return false;
    }


    private boolean intersect(Body2d o1, Body2d o2) {
        float dx = Math.abs(o1.x - o2.x);
        float dz = Math.abs(o1.z - o2.z);
        float a = o1.width;
        float b = o1.height;
        float c = o2.width;
        float d = o2.height;

        float dMax = (a + c) / 2;
        float eMax = (b + d) / 2;

        return (dx < dMax) && (dz < eMax);
    }

    private boolean intersectPlayer(Body2d o1, float pX, float pZ) {
        float dx = Math.abs(o1.x - pX);
        float dz = Math.abs(o1.z - pZ);
        float a = o1.width;
        float b = o1.height;

        float dMax = (a + PLAYER_WIDTH) / 2;
        float eMax = (b + PLAYER_HEIGHT) / 2;
        return (dx < dMax) && (dz < eMax);
    }

    private void movePlayerUp() {
        dx = 0;
        dz = -1;
        player.setRotation(0, -90, 0);
        cameraTargetX = 0;
        cameraTargetZ = -5;
    }

    private void movePlayerDown() {
        dx = 0;
        dz = 1;
        player.setRotation(0, 90, 0);
        cameraTargetX = 0;
        cameraTargetZ = 5;
    }

    private void movePlayerLeft() {
        cameraTargetX = -5;
        cameraTargetZ = 0;
        dx = -1;
        dz = 0;
        player.setRotation(0, 0, 0);
    }

    private void movePlayerRight() {
        cameraTargetX = 5;
        cameraTargetZ = 0;
        dx = 1;
        dz = 0;
        player.setRotation(0, 180, 0);
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
