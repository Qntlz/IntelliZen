package com.cerenio.pomodoro;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class PomodoroActivity extends AppCompatActivity {
    private static final long WORK_DURATION = 30 * 1000L; // 25 minutes
    private static final long BREAK_DURATION = 10 * 1000L; // 5 minutes
    private static final int MAX_CYCLES = 4;

    private TextView tvTimer;
    private TextView tvSessionLabel;
    private TextView tvCycleCount;
    private ImageView btnStartPause, btnReset;
    private CircularProgressIndicator ring;

    private CountDownTimer timer;
    private boolean isRunning;
    private long timeLeftInMillis = WORK_DURATION;
    private boolean isWorkSession = true;
    private int cycleCount = 0;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            insetsController.setAppearanceLightStatusBars(false); // light icons for dark mode
        } else {
            insetsController.setAppearanceLightStatusBars(true);  // dark icons for light mode
        }

        ring = findViewById(R.id.circularTimer);
        tvTimer = findViewById(R.id.tvTimer);
        tvSessionLabel = findViewById(R.id.tvSessionLabel);
        tvCycleCount = findViewById(R.id.tvCycleCount);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);

        // Gesture: swipe down to skip session
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velX, float velY) {
                if (e2.getY() - e1.getY() > 100) { // downward swipe
                    if (isRunning) {
                        timer.cancel();
                    }
                    finishSession();
                    return true;
                }
                return false;
            }
        });
        ring.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        initRing(WORK_DURATION);
        updateUI();

        btnStartPause.setOnClickListener(v -> {
            if (isRunning) pauseTimer();
            else startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());

        // Go back to Home
        findViewById(R.id.backBtn).setOnClickListener(v ->
                NavUtils.navigateUpFromSameTask(PomodoroActivity.this)
        );
    }

    private void initRing(long duration) {
        int maxSec = (int) (duration / 1000);
        ring.setMax(maxSec);
        ring.setProgressCompat(maxSec, false);
    }

    private void startTimer() {
        // color & label for this session
        applySessionStyle();

        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
                int secLeft = (int) (timeLeftInMillis / 1000);
                ring.setProgressCompat(secLeft, true);
            }

            @Override
            public void onFinish() {
                finishSession();
            }
        }.start();

        isRunning = true;
        btnStartPause.setImageResource(R.drawable.pause);
    }

    private void finishSession() {
        // Stop current running state
        isRunning = false;

        // count cycle when a work session completes
        if (isWorkSession) {
            cycleCount = Math.min(cycleCount + 1, MAX_CYCLES);
        }
        isWorkSession = !isWorkSession;

        long nextDuration = isWorkSession ? WORK_DURATION : BREAK_DURATION;
        timeLeftInMillis = nextDuration;
        initRing(nextDuration);
        updateUI();
    }

    private void pauseTimer() {
        timer.cancel();
        isRunning = false;
        btnStartPause.setImageResource(R.drawable.play);
    }

    private void resetTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
        isWorkSession = true;
        cycleCount = 0;
        timeLeftInMillis = WORK_DURATION;
        initRing(WORK_DURATION);
        updateUI();
    }

    private void updateUI() {
        updateTimerText();
        applySessionStyle();
        tvCycleCount.setText(getCycleText());
        btnStartPause.setImageResource(isRunning ? R.drawable.pause : R.drawable.play);
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String time = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(time);
    }

    private void applySessionStyle() {
        if (isWorkSession) {
            ring.setIndicatorColor(Color.RED);
            tvSessionLabel.setText("Work");
            tvSessionLabel.setTextColor(Color.RED);
        } else {
            ring.setIndicatorColor(Color.GREEN);
            tvSessionLabel.setText("Break");
            tvSessionLabel.setTextColor(Color.GREEN);
        }
    }

    private String getCycleText() {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < cycleCount; i++) sb.append("✔️");
//        for (int i = cycleCount; i < MAX_CYCLES; i++) sb.append("○");
        return cycleCount + "/" + MAX_CYCLES;
    }
}
