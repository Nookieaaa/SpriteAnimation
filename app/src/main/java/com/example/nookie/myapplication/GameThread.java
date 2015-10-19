package com.example.nookie.myapplication;


import android.graphics.Canvas;

public class GameThread extends Thread {
    private GameView gameView;
    private boolean isRunning = false;

    static final long FPS = 10;

    public GameThread(GameView gameView) {
        this.gameView = gameView;
    }

    public void setIsRunning(boolean isRunning) {

        this.isRunning = isRunning;
    }

    public boolean isRunning(){
        return isRunning;
    }

    @Override
    public void run() {

        long tickPS = 1000/FPS;
        long startTime;
        long sleepTime;


        while (isRunning){
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = gameView.getHolder().lockCanvas();
                synchronized (gameView.getHolder()){
                    gameView.draw(c);
                }
            }
            finally {
                if (c!=null){
                    gameView.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = tickPS - (System.currentTimeMillis() - startTime);
            try{
                if(sleepTime>0)
                    sleep(sleepTime);
                else
                    sleep(10);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
