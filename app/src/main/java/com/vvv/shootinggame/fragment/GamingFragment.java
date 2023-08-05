package com.vvv.shootinggame.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvv.shootinggame.InstructionActivity;
import com.vvv.shootinggame.R;
import com.vvv.shootinggame.RankActivity;
import com.vvv.shootinggame.key.TouchTouchController;
import com.vvv.shootinggame.logic.GamingMachine;
import com.vvv.shootinggame.mastergame.BubbleManagement;
import com.vvv.shootinggame.mastergame.Me;

import java.util.Random;

public class GamingFragment extends BaseFragment {

    private final int score = 0;
    private GamingMachine mGamingMachine;

    public GamingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        ImageView rankImageView = view.findViewById(R.id.rank);
        rankImageView.setOnClickListener(v -> openRankActivity());

        ImageView instructionImageView = view.findViewById(R.id.instruction);
        instructionImageView.setOnClickListener(v -> openInstructionActivity());
        return view;
    }

    @Override
    protected void onLayoutCompleted() {
        startGame();
        TextView scoreTextView = getView().findViewById(R.id.score_text);
        scoreTextView.setText("0");
    }

    private void startGame() {

        mGamingMachine = new GamingMachine(getMainActivity(), getView().findViewById(R.id.game_view));
        mGamingMachine.setInputController(new TouchTouchController(mGamingMachine));

        char[][] pattern = new char[][]{
                {'0', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r', '0'},
                {'0', 'y', 'y', 'y', 'y', 'y', 'y', 'y', '0', '0'},
                {'0', '0', 'b', 'b', 'b', 'b', 'b', 'b', '0', '0'},
                {'0', '0', 'r', 'r', 'r', 'r', 'r', '0', '0', '0'},
                {'0', '0', '0', 'y', 'y', 'y', 'y', '0', '0', '0'},
                {'0', '0', '0', 'b', 'b', 'b', '0', '0', '0', '0'},
                {'0', '0', '0', '0', 'r', 'r', '0', '0', '0', '0'},
                {'0', '0', '0', '0', 'y', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0'}
        };

        Random random = new Random();
        for (int row = 0; row < 7; row++) {
            for (int col = 1; col <= 8 - Math.abs(4 - row); col++) {
                if (pattern[row][col] != '0') {
                    char[] colors = {'r', 'y', 'b'};
                    pattern[row][col] = colors[random.nextInt(colors.length)];
                }
            }
        }


        BubbleManagement bubbleManagement = new BubbleManagement(mGamingMachine, pattern);

        mGamingMachine.addGameObject(new Me(mGamingMachine, bubbleManagement), 2);
        mGamingMachine.startGame();
    }

    private void openRankActivity() {
        Intent intent = new Intent(getActivity(), RankActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
    }

    private void openInstructionActivity() {
        Intent intent = new Intent(getActivity(), InstructionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGamingMachine != null && mGamingMachine.isRunning() && mGamingMachine.isPaused()) {
            mGamingMachine.resumeGame();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGamingMachine.isRunning() && !mGamingMachine.isPaused()) {
            mGamingMachine.pauseGame();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGamingMachine.stopGame();
    }

    @Override
    public boolean onBackPressed() {
        getMainActivity().finish();
        return true;
    }

}
