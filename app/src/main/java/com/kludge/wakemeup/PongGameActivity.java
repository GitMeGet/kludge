package com.kludge.wakemeup;

/*
 * Created by Yu Peng on 25/6/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Random;

public class PongGameActivity extends Activity {

    Canvas canvas;
    SquashCourtView squashCourtView;

    private SoundPool soundPool;
    int sample1 = -1, sample2 = -1, sample3 = -1, sample4 = -1;

    // for getting display details like no. of pixels
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;

    // game objects
    int racketWidth;
    int racketHeight;
    Point racketPosition;
    int ballWidth;
    Point ballPosition;

    // for racket movement
    boolean racketIsMovingRight;
    boolean racketIsMovingLeft;

    // for ball movement
    boolean ballIsMovingLeft;
    boolean ballIsMovingRight;
    boolean ballIsMovingUp;
    boolean ballIsMovingDown;

    // stats
    long lastFrameTime;
    int fps;
    int score;
    int lives;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // new stuff
        squashCourtView = new SquashCourtView(this);
        setContentView(squashCourtView);

        // add sound stuff
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // creates AssetManager instance for your application's package
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("Powerup.wav");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("Powerup2.wav");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("Powerup3.wav");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("Powerup4.wav");
            sample1 = soundPool.load(descriptor, 0);
        } catch (IOException e) {

        }

        // get screen size in pixels
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        // getSize assigns device's available screen size (excluding screen decoration
        // to Point size;
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        // game objects
        racketPosition = new Point();
        racketPosition.x = screenWidth / 2;
        racketPosition.y = screenHeight - 100;
        racketWidth = screenWidth / 8;
        racketHeight = 10;

        ballWidth = screenWidth / 35;
        ballPosition = new Point();
        ballPosition.x = screenWidth / 2;
        ballPosition.y = ballWidth + 1;

        lives = 3;
    }

    class SquashCourtView extends SurfaceView implements Runnable {

        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSquash; // wait what... means can change from outside + inside thread
        Paint paint;

        public SquashCourtView(Context context) {
            super(context);
            ourHolder = getHolder(); // returns SurfaceHolder to access and control SurfaceView's underlying surface
            paint = new Paint();

            initBallMovement();

        }

        // method called when thread created by class implementing Runnable
        // is started
        // ie, when onResume() calls squashCourtView.resume()
        @Override
        public void run() {
            // while playingSquash == true
            while (playingSquash) {
                updateCourt(); // controls movement + collision detection
                drawCourt(); // draws stuff
                controlFPS(); // locks game to consistent FPS
            }
        }

        // I suppose this function is repeatedly called every idk how many ms???
        public void updateCourt() {
            if (racketIsMovingRight && racketPosition.x + racketWidth/2 < screenWidth) {
                racketPosition.x += 10;
            }
            if (racketIsMovingLeft && racketPosition.x - racketWidth/2 > 0) {
                racketPosition.x -= 10;
            }

            // detect collisions

            // ball hits right of screen
            // ballPosition is NOT centre of ball, but top left corner
            if (ballPosition.x + ballWidth > screenWidth) {
                ballIsMovingLeft = true;
                ballIsMovingRight = false;
                soundPool.play(sample1, 1, 1, 0, 0, 1);
            }

            // ball hits left of screen
            if (ballPosition.x < 0) {
                ballIsMovingLeft = false;
                ballIsMovingRight = true;
                soundPool.play(sample1, 1, 1, 0, 0, 1);
            }

            // bottom edge of ball hits bottom of screen
            if (ballPosition.y > screenHeight - ballWidth) {
                lives--;
                if (lives == 0) {
                    lives = 3;
                    score = 0;
                    soundPool.play(sample4, 1, 1, 0, 0, 1);
                }

                // ball goes back  to top of screen
                ballPosition.y = 1 + ballWidth;

                // randomise its restarted horizontal position
                Random r = new Random();
                int startX = r.nextInt(screenWidth - ballWidth) + 1;
                ballPosition.x = startX + ballWidth;

                initBallMovement();
            }

            // ball hits top of screen
            if (ballPosition.y <= 0) {
                ballIsMovingDown = true;
                ballIsMovingUp = false;
                ballPosition.y = 1;
                soundPool.play(sample2, 1, 1, 0, 0, 1);
            }

            if (ballIsMovingDown) {
                ballPosition.y += 6;
            }

            if (ballIsMovingUp) {
                ballPosition.y -= 10;
            }

            if (ballIsMovingRight) {
                ballPosition.x += 12;
            }

            if (ballIsMovingLeft) {
                ballPosition.x -= 12;
            }

            // ball hits racket
            if (ballPosition.y + ballWidth
                    >= racketPosition.y - racketHeight / 2) {

                int halfRacket = racketWidth / 2; // what's this for?
                // if ball lands within horizontal length of racket
                if ((ballPosition.x + ballWidth)
                        > (racketPosition.x - halfRacket) &&
                        ballPosition.x <
                                (racketPosition.x + halfRacket)) {
                    // rebound ball
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    score++;
                    ballIsMovingUp = true;
                    ballIsMovingDown = false;
                    // decide how to rebound ball
                    // if left edge of ball hits right half of racket
                    if (ballPosition.x > racketPosition.x) {
                        // ball moves right
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;
                    }
                    // if left edge of ball hits left half of racket
                    else {
                        // ball moves left
                        ballIsMovingLeft = true;
                        ballIsMovingRight = false;
                    }

                }

                if (score >= 3){
                    setResult(RESULT_OK);
                    finish();
                }

            }
        }


        public void drawCourt() {
            // get a drawing surface and check if it's valid
            if (ourHolder.getSurface().isValid()) {
                // init canvas + paint objects
                canvas = ourHolder.lockCanvas();
                Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.argb(255, 255, 50, 255));
                paint.setTextSize(45);

                canvas.drawText("Score: " + score + " Lives: " + lives + " fps: " + fps, 20, 40, paint);

                // draw squash racket
                int halfRacketX = racketWidth / 2;
                int halfRacketY = racketHeight / 2;
                canvas.drawRect(racketPosition.x - halfRacketX,
                        racketPosition.y - halfRacketY,
                        racketPosition.x + halfRacketX,
                        racketPosition.y + halfRacketY, paint);

                // draw ball
                canvas.drawRect(ballPosition.x,
                        ballPosition.y,
                        ballPosition.x + ballWidth,
                        ballPosition.y + ballWidth, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);

            // 15ms of pause => ~60fps
            long timeToSleep = 15 - timeThisFrame;

            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }

            if (timeToSleep > 0) {
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                }
            }
            lastFrameTime = System.currentTimeMillis();
        }

        // method called by onPause()
        public void pause() {
            playingSquash = false;

            // to clean up threads, lest they run even after activity finished
            try {
                ourThread.join();
            } catch (InterruptedException e) {
            }
        }

        // method called by onResume()
        public void resume() {
            playingSquash = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        // control racket movement using touch events
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                // as long as screen is still being pressed
                case MotionEvent.ACTION_DOWN:
                    // if touch event on right side of screen
                    if (event.getX() >= screenWidth / 2) {
                        racketIsMovingRight = true;
                        racketIsMovingLeft = false;
                    } else {
                        racketIsMovingRight = false;
                        racketIsMovingLeft = true;
                    }
                    break;
                // when user lifts finger up from screen
                case MotionEvent.ACTION_UP:
                    racketIsMovingRight = false;
                    racketIsMovingLeft = false;
                    break;
            }

            return true;
        }


        public void initBallMovement() {
            ballIsMovingDown = true;

            // send ball in random direction
            Random r = new Random();
            int ballDirection = r.nextInt(3);

            switch (ballDirection) {
                case 0:
                    ballIsMovingLeft = true;
                    ballIsMovingRight = false;
                    break;
                case 1:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = true;
                    break;
                case 2:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = false;
                    break;
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        while(true){
            squashCourtView.pause();
            break;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        squashCourtView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        squashCourtView.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if back button pressed
        if (keyCode == KeyEvent.KEYCODE_BACK){
            squashCourtView.pause();
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}












