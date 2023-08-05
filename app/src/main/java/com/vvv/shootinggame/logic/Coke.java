package com.vvv.shootinggame.logic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public abstract class Coke extends GamingObject {
    private static final boolean DEBUG_MODE = false;
    public final Rect mBoundingRect = new Rect(-1, -1, -1, -1);
    protected final int mScreenWidth;
    protected final int mScreenHeight;
    protected final int mWidth;
    protected final int mHeight;
    protected final float mPixelFactor;
    protected final Resources mResources;
    private final float mRadius;
    private final Matrix mMatrix = new Matrix();
    private final Paint mPaint = new Paint();
    public float mX;
    public float mY;
    public float mRotation;
    public float mScale = 1;
    public int mAlpha = 255;
    protected Bitmap mBitmap;

    protected Coke(GamingMachine gamingMachine, int drawableRes) {
        mResources = gamingMachine.getContext().getResources();
        mBitmap = getDefaultBitmap(mResources.getDrawable(drawableRes));
        mPixelFactor = gamingMachine.mPixelFactor;

        mScreenWidth = gamingMachine.mScreenWidth;
        mScreenHeight = gamingMachine.mScreenHeight;
        mWidth = (int) (mBitmap.getWidth() * mPixelFactor);
        mHeight = (int) (mBitmap.getHeight() * mPixelFactor);
        mRadius = Math.max(mHeight, mWidth) / 2f;

    }

    protected Bitmap getDefaultBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    public boolean checkCollision(Coke otherCoke) {
        double distanceX = (mX + mWidth / 2f) - (otherCoke.mX + otherCoke.mWidth / 2f);
        double distanceY = (mY + mHeight / 2f) - (otherCoke.mY + otherCoke.mHeight / 2);
        double squareDistance = distanceX * distanceX + distanceY * distanceY;
        double collisionDistance = (mRadius + otherCoke.mRadius);
        return squareDistance <= collisionDistance * collisionDistance;
    }

    public void onCollision(GamingMachine gamingMachine, Coke otherObject) {
    }

    @Override
    public void onPostUpdate() {
        mBoundingRect.set(
                (int) mX,
                (int) mY,
                (int) mX + mWidth,
                (int) mY + mHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mX > canvas.getWidth()
                || mY > canvas.getHeight()
                || mX < -mWidth
                || mY < -mHeight) {
            return;
        }
        if (DEBUG_MODE) {
            mPaint.setColor(Color.YELLOW);
            canvas.drawRect(mBoundingRect, mPaint);
        }
        float scaleFactor = mPixelFactor * mScale;
        mMatrix.reset();
        mMatrix.postScale(scaleFactor, scaleFactor);
        mMatrix.postTranslate(mX, mY);
        mMatrix.postRotate(mRotation, mX + mWidth / 2f, mY + mHeight / 2f);
        mPaint.setAlpha(mAlpha);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
    }

}
