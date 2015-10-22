package com.example.nookie.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
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

    Sprite capturedSprite;

    MotionAction action;



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


        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                action = new MotionAction(event.getX(),event.getY());
                moveCharacter(action);
                int s =1;
                break;
            case MotionEvent.ACTION_MOVE:
                int s2 = 2;
                action.move(event.getX(), event.getY());
                //moveCharacter(action);
                if(capturedSprite!=null){
                    capturedSprite.drag(action);
                }
                Log.d("drag", "x=" + String.valueOf(action.getEndX()) + " y=" + String.valueOf(action.getEndY()));
                Log.d("drag2","x="+String.valueOf(event.getX())+ " y="+String.valueOf(event.getY()));
                break;
            case MotionEvent.ACTION_UP:
                int s3 = 3;
                if (!action.isDragged())
                    deleteCharacter(event);
                else {
                    Log.d("drag", "released at x=" + String.valueOf(action.getEndX()) + ", y=" + String.valueOf(action.getEndY()));
                    //releaseCharacter(action);
                    capturedSprite.endDrag();
                }

                break;
        }

        /*
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
        }*/
        return true;
    }

    private void releaseCharacter(MotionAction action) {
        synchronized (getHolder()) {

            for (int i = sprites.size() - 1; i >= 0; i--) {
                Sprite sprite = sprites.get(i);
                if (sprite.isCollision(action.getEndX(), action.getEndY())) {
                    sprite.endDrag();
                    break;
                }
            }

            for (Sprite sprite : sprites)
                sprite.changeDirection();
        }
    }

    private void deleteCharacter(MotionEvent event) {

        synchronized (getHolder()) {

            long click = System.currentTimeMillis();

            if (click - lastClick > 300)
                lastClick = click;
            else
                return;

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
    }

    public void moveCharacter(MotionAction action) {
        synchronized (getHolder()) {

            for (int i = sprites.size() - 1; i >= 0; i--) {
                Sprite sprite = sprites.get(i);
                if (sprite.isCollision(action.getEndX(), action.getEndY())) {
                    sprite.drag(action);
                    capturedSprite = sprite;
                    Log.d("drag3","locked");
                    break;
                }
            }
        }
    }

        public class MotionAction {

            private int startX;
            private int startY;

            private int endX;
            private int endY;
            private boolean isDragged;

            public MotionAction(float startX, float startY) {
                this.startX = (int)startX;
                this.startY = (int)startY;
                this.endX = this.startX;
                this.endY = this.startY;
            }

            public void move(float x, float y){
                this.endX = (int)x;
                this.endY = (int)y;
                isDragged = true;
            }

            public int getStartX() {
                return startX;
            }

            public int getStartY() {
                return startY;
            }

            public int getEndX() {
                return endX;
            }

            public int getEndY() {
                return endY;
            }

            public boolean isDragged(){
                return isDragged;
            }

        }



}
