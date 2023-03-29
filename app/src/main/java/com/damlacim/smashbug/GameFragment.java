package com.damlacim.smashbug;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.damlacim.data.local.PreferenceHelper;
import com.damlacim.data.local.PreferenceHelperImp;
import com.damlacim.smashbug.databinding.FragmentSecondBinding;

import java.util.Random;

public class GameFragment extends Fragment implements View.OnClickListener {

    private FragmentSecondBinding binding;
    private ImageView[] imageArray;
    private int score;
    private Handler handler;
    private boolean gameStarted = false;

    private PreferenceHelper preferenceHelper;

    private static final long GAME_DURATION = 50000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceHelper = new PreferenceHelperImp(requireContext());
        getScore();
        binding.btnMenu.setOnClickListener(view12 -> NavHostFragment.findNavController(GameFragment.this)
                .navigate(R.id.action_GameFragment_to_homeFragment));

        handler = new Handler();
        imageArray = new ImageView[]{binding.imageView, binding.imageView2, binding.imageView3,
                binding.imageView4, binding.imageView5, binding.imageView6,
                binding.imageView7, binding.imageView8, binding.imageView9};

        // Set OnClickListener for each ImageView
        for (ImageView imageView : imageArray) {
            imageView.setOnClickListener(this);
        }

        binding.btnRestart.setOnClickListener(view1 -> {
            startGame();
            binding.btnRestart.setVisibility(View.INVISIBLE);
            binding.btnMenu.setVisibility(View.INVISIBLE);
        });
    }

    public void saveScore(int score) {
        preferenceHelper.setScore(score);
    }

    public void getScore() {
        binding.tvLastScore.setText(String.valueOf(preferenceHelper.getScore()));
    }

    private void startGame() {
        gameStarted = true;
        score = 0;
        binding.tvScore.setText("0");

        // Hide all images
        for (ImageView image : imageArray) {
            image.setVisibility(View.INVISIBLE);
        }

        // Start the image rotation
        handler.post(runnable);

        // Start the game timer
        gameTimer.start();
    }

    private final CountDownTimer gameTimer = new CountDownTimer(GAME_DURATION, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            try {
                binding.tvTime.setText(String.valueOf(millisUntilFinished / 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            endGame();
        }
    };

    private void endGame() {
        gameStarted = false;
        binding.btnRestart.setVisibility(View.VISIBLE);
        binding.btnMenu.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
        for (ImageView image : imageArray) {
            image.setVisibility(View.INVISIBLE);
        }
        saveScore(score);

        showAlertDialog();
    }

    private final Runnable runnable = new Runnable() {
        private final Random random = new Random();
        private int previousIndex = -1;

        @Override
        public void run() {
            // Hide all images
            for (ImageView image : imageArray) {
                image.setVisibility(View.INVISIBLE);
            }

            // Show a random image
            int index;
            do {
                index = random.nextInt(imageArray.length);
            } while (index == previousIndex);
            previousIndex = index;
            imageArray[index].setVisibility(View.VISIBLE);

            // Repeat after a delay
            handler.postDelayed(this, 500);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (gameStarted) {
            switch (view.getId()) {
                case R.id.imageView:
                case R.id.imageView2:
                case R.id.imageView3:
                case R.id.imageView4:
                case R.id.imageView5:
                case R.id.imageView6:
                case R.id.imageView7:
                case R.id.imageView8:
                case R.id.imageView9:
                    increaseScore(view);
                    break;
            }
        }
    }

    public void increaseScore(View view) {
        score++;
        binding.tvScore.setText(String.valueOf(score));
        setImageClickedAnimation(view);
        setSound();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());

        alert.setTitle("Restart?");
        alert.setMessage("You score " + score + " " +
                "Are you sure to restart game?");
        alert.setPositiveButton("Yes", (dialog, which) -> startGame());
        alert.setNegativeButton("No", (dialog, which) -> Toast.makeText(requireContext(), "Game Over!", Toast.LENGTH_SHORT).show());
        alert.show();
    }

    private void setSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.click_sound);
        mediaPlayer.start();
    }
    private void setImageClickedAnimation(View view) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.image_click);
        view.startAnimation(anim);
    }
}