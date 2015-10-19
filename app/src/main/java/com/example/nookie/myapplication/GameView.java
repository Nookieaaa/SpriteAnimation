package com.example.nookie.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;


public class GameView extends SurfaceView implements View.OnTouchListener {

    private ArrayList<Sprite> sprites;

    private SurfaceHolder holder;

    private GameThread gameThread;

    private int x = 0;

    private int xSpeed = 1;

    private long lastClick;

    private Point size;



    public GameView(Context context) {
        super(context);


        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        size = new Point();
        display.getSize(size);

        lastClick = System.currentTimeMillis();

        gameThread = new GameThread(this);

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                gameThread.setIsRunning(true);
                gameThread.start();


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameThread.setIsRunning(false);
                while (retry) {
                    try {
                        gameThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sprites = new ArrayList<Sprite>();
        for (int i = 1;i<10;i++){
            sprites.add(createSprite());
        }

    }

    public Point getSize(){
        return size;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);
        for (Sprite sprite : sprites) {
            sprite.draw(canvas);
            checkForCollisions(sprite);
        }
    }

    private void checkForCollisions(Sprite currentSprite) {
        for (Sprite sprite:sprites)
            if (currentSprite.isCollision(sprite.getX(),sprite.getY()))
                sprite.changeDirection();
    }

    private Sprite createSprite(){
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.sprite);
        Sprite sprite = new Sprite(this,bmp);
        return sprite;
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {


        synchronized (getHolder()) {

            long click = System.currentTimeMillis();

            if (click-lastClick>300)
                lastClick = click;
            else
                return false;

            for (int i = sprites.size() - 1; i >= 0; i--) {
                Sprite sprite = sprites.get(i);
                if (sprite.isCollision(event.getX(), event.getY())) {
                    sprites.remove(sprite);
                    break;
                }
            }

            for (Sprite sprite : sprites)
                sprite.changeDirection();
        }
        return true;
    }
}
