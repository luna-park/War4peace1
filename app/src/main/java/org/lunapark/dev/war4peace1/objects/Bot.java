package org.lunapark.dev.war4peace1.objects;

import org.lunapark.dev.war4peace1.managers.ObjectManager;
import org.lunapark.dev.war4peace1.managers.SoundManager;

import java.util.Stack;

import static org.lunapark.dev.war4peace1.utils.Consts.BOT_BRAIN_TIME;

/**
 * Enemy bot
 * Created by znak on 04.04.2017.
 */

public class Bot extends Character {

    public enum BotState {IDLE, SEEK}

    private BotState currentState = BotState.IDLE;
    private Stack<Point2d> seekPath;
    private long timer;

    public Bot(ObjectManager objectManager, SoundManager soundManager) {
        super(objectManager, soundManager);
        seekPath = new Stack<>();
    }

    public BotState getState() {
        return currentState;
    }

    public void setState(BotState currentState) {
        this.currentState = currentState;
    }

    public boolean busy() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - timer > BOT_BRAIN_TIME) {
            timer = currentTime;
            return false;
        } else {
            return true;
        }
    }

    public Stack<Point2d> getSeekPath() {
        return seekPath;
    }

    public void setSeekPath(Stack<Point2d> seekPath) {
        this.seekPath = seekPath;
    }
}
