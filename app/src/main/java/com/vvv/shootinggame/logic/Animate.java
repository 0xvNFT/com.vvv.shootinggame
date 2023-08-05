package com.vvv.shootinggame.logic;

import android.graphics.Bitmap;

public abstract class Animate extends Coke {

    protected Bitmap[] mSpriteBitmaps;
    private long mTimePreFrame;
    private long mCurrentTime;
    private int mIndex;

    protected Animate(GamingMachine gamingMachine, int drawableRes) {
        super(gamingMachine, drawableRes);
    }

    @Override
    public void startGame() {
        mCurrentTime = 0;
        mIndex = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis, GamingMachine gamingMachine) {
        mCurrentTime += elapsedMillis;
        if (mCurrentTime >= mTimePreFrame) {
            mIndex++;
            if (mIndex > mSpriteBitmaps.length - 1) {
                mIndex = 0;
            }
            mBitmap = mSpriteBitmaps[mIndex];
            mCurrentTime = 0;
        }
    }

    public void setAnimatedSpriteBitmaps(Bitmap[] spriteBitmaps) {
        mSpriteBitmaps = spriteBitmaps;
    }

    public void setTimePreFrame(long timePreFrame) {
        mTimePreFrame = timePreFrame;
    }

}
