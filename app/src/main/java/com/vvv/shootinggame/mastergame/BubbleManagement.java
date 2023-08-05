package com.vvv.shootinggame.mastergame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.vvv.shootinggame.MainActivity;
import com.vvv.shootinggame.R;
import com.vvv.shootinggame.logic.GamingMachine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BubbleManagement {

    private final GamingMachine mGamingMachine;
    private final Bubbles[][] mBubblesArray;
    private final int mCol, mRow;
    private final float mBubbleWidth;
    private final Me mMe;


    private final ArrayList<Bubbles> mDeleteList = new ArrayList<>();

    public BubbleManagement(GamingMachine gamingMachine, char[][] charArray) {
        mGamingMachine = gamingMachine;
        mCol = charArray[0].length;
        mRow = charArray.length;
        mBubblesArray = new Bubbles[mRow][mCol];
        mBubbleWidth = 200 * gamingMachine.mPixelFactor;
        mMe = new Me(gamingMachine, this);
        initBubble(charArray);
    }

    private void initBubble(char[][] charArray) {
        float intervalX = mBubbleWidth;
        float intervalY = mBubbleWidth * 0.85f;

        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mCol; j++) {

                Bubbles bubbles = new Bubbles(mGamingMachine, i, j, getBubbleColor(charArray[i][j]));
                bubbles.mX = j * intervalX;
                bubbles.mY = i * intervalY;

                if ((i % 2) != 0) {
                    bubbles.mX += intervalX / 2f;
                }

                mBubblesArray[i][j] = bubbles;
                mGamingMachine.addGameObject(bubbles, 1);
            }
        }
        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mCol; j++) {
                Bubbles bubbles = mBubblesArray[i][j];
                if (i < mRow - 1) {
                    bubbles.mEdges.add(mBubblesArray[i + 1][j]);
                }
                if (i > 0) {
                    bubbles.mEdges.add(mBubblesArray[i - 1][j]);
                }
                if (j < mCol - 1) {
                    bubbles.mEdges.add(mBubblesArray[i][j + 1]);
                }
                if (j > 0) {
                    bubbles.mEdges.add(mBubblesArray[i][j - 1]);
                }

                if ((i % 2) == 0) {
                    if (i < mRow - 1 && j > 0) {
                        bubbles.mEdges.add(mBubblesArray[i + 1][j - 1]);
                    }
                    if (i > 0 && j > 0) {
                        bubbles.mEdges.add(mBubblesArray[i - 1][j - 1]);
                    }
                } else {
                    if (i < mRow - 1 && j < mCol - 1) {
                        bubbles.mEdges.add(mBubblesArray[i + 1][j + 1]);
                    }
                    if (j < mCol - 1) {
                        bubbles.mEdges.add(mBubblesArray[i - 1][j + 1]);
                    }
                }
            }
        }
    }

    private BubbleColors getBubbleColor(char color) {
        switch (color) {
            case 'b':
                return BubbleColors.BLUE;
            case 'r':
                return BubbleColors.RED;
            case 'y':
                return BubbleColors.YELLOW;
            case '0':
                return BubbleColors.BLANK;
        }
        return BubbleColors.BLANK;
    }

    public void addBubble(Me me, Bubbles bubbles) {
        int row = bubbles.mRow;
        int col = bubbles.mCol;
        Bubbles newBubbles = null;
        if (me.mY > bubbles.mY + mBubbleWidth / 2) {
            if (me.mX >= bubbles.mX) {
                if (row < mRow - 1) {
                    newBubbles = (row % 2 == 0) ? mBubblesArray[row + 1][col] : mBubblesArray[row + 1][col + 1];
                }
            } else {
                if (row < mRow - 1) {
                    newBubbles = (row % 2 == 0) ? mBubblesArray[row + 1][col - 1] : mBubblesArray[row + 1][col];
                }
            }
        } else {
            if (me.mX >= bubbles.mX) {
                if (col < mCol - 1) {
                    newBubbles = mBubblesArray[row][col + 1];
                }
            } else {
                if (col > 0) {
                    newBubbles = mBubblesArray[row][col - 1];
                }
            }
        }
        if (newBubbles != null) {
            newBubbles.setBubbleColor(me.mBubbleColors);
            popBubble(newBubbles);
            popFloater();
        } else {
            showGameOverDialog();
        }
    }

    private void showGameOverDialog() {
        Activity activity = mGamingMachine.getMainActivity();

        activity.runOnUiThread(() -> {
            View customDialogView = LayoutInflater.from(activity).inflate(R.layout.game_over_dialog_layout, null);

            TextView titleTextView = customDialogView.findViewById(R.id.dialog_title);
            TextView messageTextView = customDialogView.findViewById(R.id.dialog_message);
            Button okButton = customDialogView.findViewById(R.id.ok_button);

            titleTextView.setText("Game Over");
            messageTextView.setText("You hit a bubble outside the bounds.");

            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setView(customDialogView)
                    .setCancelable(false)
                    .create();

            okButton.setOnClickListener(v -> {
                alertDialog.dismiss();
                activity.finish();
            });

            alertDialog.show();
        });
    }

    private Activity getMainActivity() {
        return mGamingMachine.getMainActivity();
    }

    private void restartGame() {
        mGamingMachine.startGame();
    }

    private void popBubble(Bubbles bubbles) {
        bfs(bubbles, bubbles.mBubbleColors);

        int size = mDeleteList.size();
        for (Bubbles b : mDeleteList) {
            if (size >= 3) {
                b.setBlankBubble();
                mMe.increaseScore(20);
            }

            b.mDepth = -1;
        }
        mDeleteList.clear();
        checkVictory();

        int currentScore = mMe.getScore();
        checkVictory();
    }

    private void checkVictory() {
        if (!checkAnyBubblesNonBlank()) {
            showVictoryDialog(mMe.getScore());
        }
    }

    private boolean checkAnyBubblesNonBlank() {
        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mCol; j++) {
                if (mBubblesArray[i][j].mBubbleColors != BubbleColors.BLANK) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showVictoryDialog(final int score) {
        Activity activity = mGamingMachine.getMainActivity();

        activity.runOnUiThread(() -> {
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_dialog_layout, null);

            TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
            TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
            Button nextButton = dialogView.findViewById(R.id.next_button);
            Button mainMenuButton = dialogView.findViewById(R.id.main_menu_button);

            titleTextView.setText("Congratulations!");
            messageTextView.setText("You have cleared all bubbles in this level!");

            nextButton.setOnClickListener(v -> {
                activity.finish();

                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String scoresString = preferences.getString("SCORES", "");
                if (!scoresString.isEmpty()) {
                    scoresString += ",";
                }
                scoresString += String.valueOf(score);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SCORES", scoresString);
                editor.apply();
                restartGame();
            });

            mainMenuButton.setOnClickListener(v -> {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String scoresString = preferences.getString("SCORES", "");
                if (!scoresString.isEmpty()) {
                    scoresString += ",";
                }
                scoresString += String.valueOf(score);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SCORES", scoresString);
                editor.apply();
                mGamingMachine.getActivity().finish();
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView)
                    .setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    private void bfs(Bubbles root, BubbleColors color) {
        Queue<Bubbles> queue = new LinkedList<>();
        root.mDepth = 0;
        queue.offer(root);

        while (queue.size() > 0) {
            Bubbles currentBubbles = queue.poll();
            mDeleteList.add(currentBubbles);
            for (Bubbles b : currentBubbles.mEdges) {

                if (b.mDepth == -1 && b.mBubbleColors == color) {
                    b.mDepth = currentBubbles.mDepth + 1;
                    queue.offer(b);
                }
            }
        }
    }

    private void popFloater() {
        for (int i = 0; i < mCol; i++) {
            Bubbles bubbles = mBubblesArray[0][i];
            if (bubbles.mBubbleColors != BubbleColors.BLANK) {
                dfs(bubbles);
            }
        }

        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mCol; j++) {
                Bubbles bubbles = mBubblesArray[i][j];
                if (!bubbles.mDiscover) {
                    bubbles.setBlankBubble();
                } else {
                    bubbles.mDiscover = false;
                }
            }
        }
        checkVictory();
    }

    private void dfs(Bubbles bubbles) {
        bubbles.mDiscover = true;
        for (Bubbles b : bubbles.mEdges) {
            if (!b.mDiscover && b.mBubbleColors != BubbleColors.BLANK) {
                dfs(b);
            }
        }
    }
}
