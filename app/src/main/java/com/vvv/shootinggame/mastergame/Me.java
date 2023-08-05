package com.vvv.shootinggame.mastergame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.widget.TextView;

import com.vvv.shootinggame.R;
import com.vvv.shootinggame.logic.Coke;
import com.vvv.shootinggame.logic.GamingEvent;
import com.vvv.shootinggame.logic.GamingMachine;
import com.vvv.shootinggame.logic.GamingObject;

import java.util.Random;

public class Me extends Coke {

    private final BubbleManagement mBubbleManagement;
    private final GamingMachine mGamingMachine;
    private final float mStartX, mStartY;
    private final float mMaxX;
    private final float mSpeed;
    private final Random mRandom = new Random();
    public BubbleColors mBubbleColors;
    private float mSpeedX, mSpeedY;
    private boolean mShoot = false;
    private int mScore = 0;

    public Me(GamingMachine gamingMachine, BubbleManagement bubbleManagement) {
        super(gamingMachine, BubbleColors.BLUE.getImageResId());

        mBubbleColors = BubbleColors.BLUE;
        mBubbleManagement = bubbleManagement;
        mGamingMachine = gamingMachine;
        mStartX = mScreenWidth / 2f;
        mStartY = mScreenHeight * 3 / 4f;

        mMaxX = gamingMachine.mScreenWidth - mWidth;

        mSpeed = gamingMachine.mPixelFactor * 3000 / 1000;

        gamingMachine.addGameObject(new BubblePath(), 2);
        SharedPreferences preferences = gamingMachine.getActivity().getPreferences(Context.MODE_PRIVATE);
        mScore = preferences.getInt("PLAYER_SCORE", 0);
    }

    @Override
    public void startGame() {
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
    }

    @Override
    public void onUpdate(long elapsedMillis, GamingMachine gamingMachine) {
        if (mShoot) {
            float sideX = gamingMachine.mInputTouchController.mXUp - mStartX;
            float sideY = gamingMachine.mInputTouchController.mYUp - mStartY;
            float angle = (float) Math.atan2(sideY, sideX);

            mSpeedX = (float) (mSpeed * Math.cos(angle));
            mSpeedY = (float) (mSpeed * Math.sin(angle));

            mShoot = false;
        }

        mX += mSpeedX * elapsedMillis;
        if (mX <= 0) {
            mX = 0;
            mSpeedX = -mSpeedX;
        }
        if (mX >= mMaxX) {
            mX = mMaxX;
            mSpeedX = -mSpeedX;
        }
        mY += mSpeedY * elapsedMillis;
        if (mY <= -mHeight || mY >= mScreenHeight) {
            setNextBubble();
        }
    }

    @Override
    public void onGameEvent(GamingEvent gamingEvent) {
        if (gamingEvent == GamingEvent.SHOOT) {
            mShoot = true;
        }
    }

    @Override
    public void onCollision(GamingMachine gamingMachine, Coke otherObject) {
        if (otherObject instanceof Bubbles) {
            Bubbles bubbles = (Bubbles) otherObject;
            if (bubbles.mBubbleColors != BubbleColors.BLANK && mY >= bubbles.mY) {
                mBubbleManagement.addBubble(this, bubbles);
                setNextBubble();
            }
        }
    }

    public void increaseScore(int points) {
        mScore += points;
        mGamingMachine.getActivity().runOnUiThread(() -> {
            TextView scoreTextView = mGamingMachine.getActivity().findViewById(R.id.score_text);
            scoreTextView.setText(String.valueOf(mScore));
        });
    }

    public int getScore() {
        return mScore;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setNextBubble() {

        BubbleColors color = BubbleColors.values()[mRandom.nextInt(BubbleColors.values().length - 1)];
        mBubbleColors = color;
        mBitmap = getDefaultBitmap(mResources.getDrawable(color.getImageResId()));


        mSpeedX = 0;
        mSpeedY = 0;
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
    }

    class BubblePath extends GamingObject {

        private final float mMaxX, mMinX;
        private final float mRadius;
        private final Paint mPaint = new Paint();
        private final Path mTrianglePath = new Path();
        private float mReflectX, mReflectY;
        private float mEndX, mEndY;
        private boolean mDraw = false;

        public BubblePath() {
            mMaxX = mScreenWidth - mWidth / 2f;
            mMinX = mWidth / 2f;
            mRadius = mScreenWidth / 2f - mWidth / 2f;

            mPaint.setColor(Color.parseColor("#FFF700"));
            mPaint.setStrokeWidth(5);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            float dashLength = 30;
            float gapLength = 15;

            Path triangle = createTrianglePath(dashLength);
            PathEffect effect = new PathDashPathEffect(triangle, dashLength + gapLength, 0, PathDashPathEffect.Style.TRANSLATE);
            mPaint.setPathEffect(effect);
        }

        @Override
        public void startGame() {
        }

        @Override
        public void onUpdate(long elapsedMillis, GamingMachine gamingMachine) {
            if (gamingMachine.mInputTouchController.mAiming) {
                float sideX = gamingMachine.mInputTouchController.mXDown - mStartX;
                float sideY = gamingMachine.mInputTouchController.mYDown - mStartY;
                float ratio = Math.abs(sideY / sideX);
                if (sideY >= 0) {
                    ratio = -ratio;
                }

                mReflectX = sideX > 0 ? mMaxX : mMinX;
                mReflectY = mStartY - mRadius * ratio;

                mEndX = sideX > 0 ? mMinX : mMaxX;
                mEndY = mReflectY - mRadius * ratio * 2;

                mDraw = true;
            } else {
                mDraw = false;
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (!mDraw) {
                return;
            }
            canvas.drawLine(mStartX, mStartY, mReflectX, mReflectY, mPaint);
            canvas.drawLine(mReflectX, mReflectY, mEndX, mEndY, mPaint);
        }

        private Path createTrianglePath(float size) {
            float halfSize = size / 2;
            mTrianglePath.moveTo(-halfSize, 0);
            mTrianglePath.lineTo(halfSize, 0);
            mTrianglePath.lineTo(0, -size);
            mTrianglePath.close();
            return mTrianglePath;
        }
    }
}
