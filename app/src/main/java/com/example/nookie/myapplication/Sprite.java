package com.example.nookie.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Sprite {
    private GameView gameView;

    private Bitmap bitmap;

    private static final int BMP_ROWS = 4;
    private static final int BMP_COLS = 3;


    private int x = 5;
    private int y = 5;
    private int xSpeed = 5;
    private int ySpeed = 1;

    private int currentFrame = 0;
    private int width;
    private int height;

    int[] DIRECTION_TO_ANIMATION_MAP = {3,1,0,2};

    public Sprite(GameView gameView, Bitmap bitmap) {
        this.gameView = gameView;
        this.bitmap = bitmap;
        width = bitmap.getWidth() / BMP_COLS;
        height = bitmap.getHeight() / BMP_ROWS;

        Random rnd = new Random();
        xSpeed = rnd.nextInt(10)-5;
        ySpeed = rnd.nextInt(10)-5;


        x = rnd.nextInt(gameView.getSize().x);
        y = rnd.nextInt(gameView.getSize().y);
    }

    private void update(){
        if (x > gameView.getWidth() - width - xSpeed || x + xSpeed <= 0){
            xSpeed = -xSpeed;
        }

        x+= xSpeed;

        if(y > gameView.getHeight() - height - ySpeed || y+ySpeed <=0){
            ySpeed = -ySpeed;
        }
        y+=ySpeed;

        currentFrame = ++currentFrame % BMP_COLS;
    }

    public void draw(Canvas canvas){

        update();
        int srcX = currentFrame*width;
        int srcY = getAnimationRow()*height;
        Rect src = new Rect(srcX,srcY,srcX+width,srcY+height);
        Rect dst = new Rect(x,y,x+width,y+height);


        canvas.drawBitmap(bitmap,src,dst,null);
    }

    private int getAnimationRow() {
        double doubleDirection = (Math.atan2(xSpeed,ySpeed))/(Math.PI/2)+2;
        int direction = (int)Math.round(doubleDirection)%BMP_ROWS;

        return DIRECTION_TO_ANIMATION_MAP[direction];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void changeDirection() {
        Random rnd = new Random();
        xSpeed = rnd.nextInt(10)-5;
        ySpeed = rnd.nextInt(10)-5;
    }

    public boolean isCollision(float _x, float _y) {
        boolean result = false;

        result = _x > x && _x<= x+width && _y > y && _y < y+width;

        return result;
    }
}
