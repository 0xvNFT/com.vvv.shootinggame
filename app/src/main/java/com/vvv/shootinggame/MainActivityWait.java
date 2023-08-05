package com.vvv.shootinggame;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;


public class MainActivityWait extends AppCompatActivity {

    private final int[] ballColors = {R.drawable.teal, R.drawable.blue, R.drawable.green,
            R.drawable.purple, R.drawable.red, R.drawable.orange};
    private final Stack<ImageView> bubbleStack = new Stack<>();
    private final int numRows = 5;
    private final int numCols = 5;
    public int bubbleSize;
    public int ballSize;
    public int startX;
    public int topBannerHeight;
    private ImageView cannon, crosshair, ballSelector, ball;
    private int currentBallColorIndex = 0;
    private boolean isAiming = false;
    private ImageView[][] bubbleGrid;

    public MainActivityWait() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wait);

        cannon = findViewById(R.id.cannon);
        crosshair = findViewById(R.id.crosshair);
        ballSelector = findViewById(R.id.ball_selector);
        ball = findViewById(R.id.ball);
        bubbleGrid = new ImageView[numRows][numCols];

        bubbleSize = 100;
        ballSize = 100 / 2;
        topBannerHeight = findViewById(R.id.top_banner).getHeight();

        for (int col = 0; col < numCols; col++) {
            for (int row = 0; row < numRows; row++) {
                bubbleGrid[row][col] = null;
            }
        }

        ViewTreeObserver viewTreeObserver = findViewById(R.id.constraintLayout).getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                findViewById(R.id.constraintLayout).getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int layoutWidth = findViewById(R.id.constraintLayout).getWidth();
                startX = (layoutWidth - numCols * bubbleSize) / 2;

                placeRandomBubblesOnTopRow();
            }
        });

        findViewById(R.id.constraintLayout).setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();
            float centerX = cannon.getX() + (float) cannon.getWidth() / 2;
            float centerY = cannon.getY() + cannon.getHeight();
            float angleRadians = (float) Math.atan2(y - centerY, x - centerX);
            float angleDegrees = (float) Math.toDegrees(angleRadians);

            ball.setImageResource(ballColors[currentBallColorIndex]);
            ball.setVisibility(View.VISIBLE);
            ballSelector.setImageResource(ballColors[currentBallColorIndex]);
            ballSelector.setVisibility(View.VISIBLE);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isAiming = true;

                crosshair.setVisibility(View.VISIBLE);

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                cannon.setRotation(angleDegrees + 90);
                crosshair.setRotation(angleDegrees + 90);
                float crosshairDistance = cannon.getHeight() * 1.5f;
                float crosshairX = centerX + crosshairDistance * (float) Math.cos(angleRadians);
                float crosshairY = centerY + crosshairDistance * (float) Math.sin(angleRadians);
                crosshair.setX(crosshairX - (float) crosshair.getWidth() / 2);
                crosshair.setY(crosshairY - (float) crosshair.getHeight() / 2);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isAiming) {
                    shootBall(angleDegrees);
                }
                isAiming = false;
                crosshair.setVisibility(View.INVISIBLE);
            }

            return true;
        });

        ballSelector.setOnClickListener(v -> {
            currentBallColorIndex = (currentBallColorIndex + 1) % ballColors.length;
            ballSelector.setImageResource(ballColors[currentBallColorIndex]);

            ball.setImageResource(ballColors[currentBallColorIndex]);
        });
    }

    private void shootBall(float angleDegrees) {
        ImageView newBall = new ImageView(this);
        newBall.setImageResource(ballColors[currentBallColorIndex]);
        newBall.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        ((ViewGroup) findViewById(R.id.constraintLayout)).addView(newBall);

        float centerX = cannon.getX() + (float) cannon.getWidth() / 2;
        float centerY = cannon.getY() + cannon.getHeight();

        float velocity = 10.0f;
        final float[] velocityX = {velocity * (float) Math.cos(Math.toRadians(angleDegrees))};
        float velocityY = velocity * (float) Math.sin(Math.toRadians(angleDegrees));

        final float[] ballX = {centerX + (float) cannon.getHeight() * 1.5f * (float) Math.cos(Math.toRadians(angleDegrees))};
        final float[] ballY = {centerY + (float) cannon.getHeight() * 1.5f * (float) Math.sin(Math.toRadians(angleDegrees))};
        newBall.setX(ballX[0]);
        newBall.setY(ballY[0]);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ballX[0] += velocityX[0];
                ballY[0] += velocityY;

                newBall.setX(ballX[0]);
                newBall.setY(ballY[0]);

                if (ballY[0] < topBannerHeight) {
                    int newCol = Math.round((ballX[0] - startX) / bubbleSize);
                    int newRow = Math.round((ballY[0] - topBannerHeight) / bubbleSize);
                    if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
                        bubbleGrid[newRow][newCol] = newBall;
                        newBall.setVisibility(View.VISIBLE);
                    } else {
                        ((ViewGroup) findViewById(R.id.constraintLayout)).removeView(newBall);
                    }
                    return;
                }

                handleCollisions(newBall, bubbleGrid, bubbleSize, ballSize, startX, topBannerHeight);

                if (ballX[0] < 0 || ballX[0] > findViewById(R.id.constraintLayout).getWidth() - newBall.getWidth()) {
                    velocityX[0] = -velocityX[0];
                }

                if (ballY[0] < 0 || ballY[0] > findViewById(R.id.constraintLayout).getHeight() - newBall.getHeight()) {
                    ((ViewGroup) findViewById(R.id.constraintLayout)).removeView(newBall);
                    return;
                }

                new Handler().post(this);
            }
        });
    }

    private void placeRandomBubblesOnTopRow() {

        int topBannerHeight = findViewById(R.id.top_banner).getHeight();
        int bubbleSize = 100;
        int layoutWidth = findViewById(R.id.constraintLayout).getWidth();
        int startX = (layoutWidth - numCols * bubbleSize) / 2;

        for (int col = 0; col < numCols; col++) {
            for (int row = 0; row < numRows; row++) {
                int randomColorIndex = new Random().nextInt(ballColors.length);

                ImageView bubbleView = new ImageView(this);
                bubbleView.setImageResource(ballColors[randomColorIndex]);
                bubbleView.setLayoutParams(new ViewGroup.LayoutParams(bubbleSize, bubbleSize));
                bubbleView.setX(startX + col * bubbleSize);
                bubbleView.setY(topBannerHeight + row * bubbleSize);
                ((ViewGroup) findViewById(R.id.constraintLayout)).addView(bubbleView);
                bubbleGrid[row][col] = bubbleView;
            }
        }
    }

    private boolean isCollision(ImageView ball1, ImageView ball2) {
        float ball1X = ball1.getX() + ball1.getWidth() / 2;
        float ball1Y = ball1.getY() + ball1.getHeight() / 2;

        float ball2X = ball2.getX() + ball2.getWidth() / 2;
        float ball2Y = ball2.getY() + ball2.getHeight() / 2;

        float distanceX = ball1X - ball2X;
        float distanceY = ball1Y - ball2Y;
        float distanceSquared = distanceX * distanceX + distanceY * distanceY;

        int radiusSum = ball1.getWidth() / 2 + ball2.getWidth() / 2;
        int radiusSumSquared = radiusSum * radiusSum;
        return distanceSquared <= radiusSumSquared;
    }

    private void handleCollisions(ImageView ball, ImageView[][] bubbleGrid, int bubbleSize, int ballSize, int startX, int topBannerHeight) {
        int row = -1, col = -1;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (bubbleGrid[i][j] != null && isCollision(ball, bubbleGrid[i][j])) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        if (row != -1 && col != -1) {
            Drawable ballDrawable = ball.getDrawable();
            Drawable bubbleDrawable = bubbleGrid[row][col].getDrawable();

            if (ballDrawable instanceof BitmapDrawable && bubbleDrawable instanceof BitmapDrawable) {
                Bitmap ballBitmap = ((BitmapDrawable) ballDrawable).getBitmap();
                Bitmap bubbleBitmap = ((BitmapDrawable) bubbleDrawable).getBitmap();

                if (areBitmapsSameColor(ballBitmap, bubbleBitmap)) {
                    boolean[][] visited = new boolean[numRows][numCols];
                    for (int i = 0; i < numRows; i++) {
                        Arrays.fill(visited[i], false);
                    }

                    Set<Pair<Integer, Integer>> connectedSet = new HashSet<>();
                    markConnectedBubbles(bubbleGrid, row, col, visited, connectedSet);

                    if (connectedSet.size() >= 2) {
                        for (Pair<Integer, Integer> bubblePair : connectedSet) {
                            int i = bubblePair.first;
                            int j = bubblePair.second;
                            ImageView bubbleView = bubbleGrid[i][j];
                            if (bubbleView != null) {
                                ((ViewGroup) findViewById(R.id.constraintLayout)).removeView(bubbleView);
                                bubbleGrid[i][j] = null;
                            }
                        }
                    } else {
                        int newCol = Math.round((ball.getX() - startX) / bubbleSize);
                        int newRow = Math.round((ball.getY() - topBannerHeight) / bubbleSize);
                        if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
                            bubbleGrid[newRow][newCol] = ball;
                            ball.setX(startX + newCol * bubbleSize);
                            ball.setY(topBannerHeight + newRow * bubbleSize);
                            ball.setVisibility(View.VISIBLE);
                        }
                    }

                    if (ball.getVisibility() == View.INVISIBLE) {
                        ((ViewGroup) findViewById(R.id.constraintLayout)).removeView(ball);
                    }
                }
            }
        }
    }

    private void addToStack(ImageView ball) {
        ball.setVisibility(View.INVISIBLE);
        bubbleStack.push(ball);
    }

    private void markConnectedBubbles(ImageView[][] bubbleGrid, int row, int col, boolean[][] visited, Set<Pair<Integer, Integer>> connectedSet) {
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(row, col));
        visited[row][col] = true;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Pair<Integer, Integer> current = queue.poll();
            int currentRow = current.first;
            int currentCol = current.second;
            connectedSet.add(current);

            for (int i = 0; i < 4; i++) {
                int newRow = currentRow + dx[i];
                int newCol = currentCol + dy[i];

                if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols
                        && !visited[newRow][newCol] && bubbleGrid[newRow][newCol] != null) {

                    Drawable bubbleDrawable = bubbleGrid[newRow][newCol].getDrawable();
                    Drawable targetDrawable = bubbleGrid[row][col].getDrawable();

                    if (bubbleDrawable instanceof BitmapDrawable && targetDrawable instanceof BitmapDrawable) {
                        Bitmap bubbleBitmap = ((BitmapDrawable) bubbleDrawable).getBitmap();
                        Bitmap targetBitmap = ((BitmapDrawable) targetDrawable).getBitmap();

                        if (areBitmapsSameColor(bubbleBitmap, targetBitmap)) {
                            queue.add(new Pair<>(newRow, newCol));
                            visited[newRow][newCol] = true;
                        }
                    }
                }
            }
        }
    }

    private void markConnectedBubbles(ImageView[][] bubbleGrid, int row, int col, boolean[][] visited) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{row, col});
        visited[row][col] = true;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentRow = current[0];
            int currentCol = current[1];

            for (int i = 0; i < 4; i++) {
                int newRow = currentRow + dx[i];
                int newCol = currentCol + dy[i];

                if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols
                        && !visited[newRow][newCol] && bubbleGrid[newRow][newCol] != null) {

                    Drawable bubbleDrawable = bubbleGrid[newRow][newCol].getDrawable();
                    Drawable targetDrawable = bubbleGrid[row][col].getDrawable();

                    if (bubbleDrawable instanceof BitmapDrawable && targetDrawable instanceof BitmapDrawable) {
                        Bitmap bubbleBitmap = ((BitmapDrawable) bubbleDrawable).getBitmap();
                        Bitmap targetBitmap = ((BitmapDrawable) targetDrawable).getBitmap();

                        if (areBitmapsSameColor(bubbleBitmap, targetBitmap)) {
                            queue.add(new int[]{newRow, newCol});
                            visited[newRow][newCol] = true;
                        }
                    }
                }
            }
        }
    }

    private boolean areBitmapsSameColor(Bitmap bitmap1, Bitmap bitmap2) {
        if (bitmap1 == null || bitmap2 == null) {
            return false;
        }

        int width1 = bitmap1.getWidth();
        int width2 = bitmap2.getWidth();
        int height1 = bitmap1.getHeight();
        int height2 = bitmap2.getHeight();

        if (width1 != width2 || height1 != height2) {
            return false;
        }

        int[] pixels1 = new int[width1 * height1];
        int[] pixels2 = new int[width2 * height2];

        bitmap1.getPixels(pixels1, 0, width1, 0, 0, width1, height1);
        bitmap2.getPixels(pixels2, 0, width2, 0, 0, width2, height2);

        int tolerance = 25; // Adjust this value for color similarity tolerance

        for (int i = 0; i < width1; i++) {
            for (int j = 0; j < height1; j++) {
                int pixel1 = pixels1[i + j * width1];
                int pixel2 = pixels2[i + j * width1];

                int alpha1 = Color.alpha(pixel1);
                int red1 = Color.red(pixel1);
                int green1 = Color.green(pixel1);
                int blue1 = Color.blue(pixel1);

                int alpha2 = Color.alpha(pixel2);
                int red2 = Color.red(pixel2);
                int green2 = Color.green(pixel2);
                int blue2 = Color.blue(pixel2);

                int deltaAlpha = Math.abs(alpha1 - alpha2);
                int deltaRed = Math.abs(red1 - red2);
                int deltaGreen = Math.abs(green1 - green2);
                int deltaBlue = Math.abs(blue1 - blue2);

                if (deltaAlpha > tolerance || deltaRed > tolerance || deltaGreen > tolerance || deltaBlue > tolerance) {
                    return false;
                }
            }
        }
        return true;
    }

}