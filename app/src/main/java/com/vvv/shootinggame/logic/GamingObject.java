package com.vvv.shootinggame.logic;

import android.graphics.Canvas;

public abstract class GamingObject {
    public int mLayer;

    public abstract void startGame();

    public abstract void onUpdate(long elapsedMillis, GamingMachine gamingMachine);

    public abstract void onDraw(Canvas canvas);

    public void onPostUpdate() {
    }

    public void onGameEvent(GamingEvent gamingEvent) {
    }
}
