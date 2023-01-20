package fr.paris8.pongaccess;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.View;

public class JeuThread extends Thread{

    public static final int STAT_READY = 0;
    public static final int STAT_PAUSED = 1;
    public static final int STAT_RUNNING = 2;
    public static final int STAT_WIN = 3;
    public static final int STAT_LOSE = 4;

    private boolean mSensorsOn;

    private final Context mCtx;
    private final SurfaceHolder mSurfaceHolder;
    private Table mPongTable;
    private final Handler mGameStatusHandler;
    private final Handler mScoreHandler;

    private boolean mRun = false;
    private int mGameState;
    private Object mRunLock;

    public static final int PHYS_FPS = 60;

    public JeuThread(Context mCtx, SurfaceHolder mSurfaceHolder, Table mPongTable, Handler mGameStatusHandler, Handler mScoreHandler) {
        this.mCtx = mCtx;
        this.mSurfaceHolder = mSurfaceHolder;
        this.mPongTable = mPongTable;
        this.mGameStatusHandler = mGameStatusHandler;
        this.mScoreHandler = mScoreHandler;
        mRunLock = new Object();
    }


    @Override
    public void run() {
        long mNextGameTic = SystemClock.uptimeMillis();
        int skipTics = 1000/ PHYS_FPS;

        while (mRun){
            Canvas c =null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                if (c != null){
                    synchronized (mSurfaceHolder){
                        if (mGameState == STAT_RUNNING ){
                            mPongTable.misAJour(c);
                        }
                        synchronized (mRunLock){
                            if (mRun){
                                mPongTable.draw(c);
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (c != null){
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }

            mNextGameTic += skipTics;
            long sleepTime =mNextGameTic - SystemClock.uptimeMillis();
            if (sleepTime > 0){
                try {
                    Thread.sleep(sleepTime);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void setState(int state){

        synchronized (mSurfaceHolder){
            mGameState = state;
            Resources res = mCtx.getResources();
            switch (mGameState){
                case STAT_READY:
                    setUpNewRound();
                    break;

                case STAT_RUNNING:
                    hideStatusText();
                    break;
                case STAT_WIN:
                    setStatusText(res.getString(R.string.mode_win));
                    mPongTable.getPlayer().score++;
                    setUpNewRound();
                    break;
                case STAT_LOSE:
                    setStatusText(res.getString(R.string.mode_lose));
                    mPongTable.getOpponent().score++;
                    setUpNewRound();
                    break;
                case STAT_PAUSED:
                    setStatusText(res.getString(R.string.mode_pause));
                    break;


            }
        }
    }

    public void setUpNewRound(){
        synchronized (mSurfaceHolder){
            mPongTable.placerTable();
        }
    }

    public void setRunning(boolean running){
        synchronized (mRunLock){
            mRun = running;
        }
    }

    public boolean sensorsOn(){

        return mSensorsOn;
    }

    public boolean isBetweenRounds(){

        return mGameState != STAT_RUNNING;
    }

    private void setStatusText(String text){
        Message msg = mGameStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putInt("visibility", View.VISIBLE);
        msg.setData(b);
        mGameStatusHandler.sendMessage(msg);
    }

    private void hideStatusText(){
        Message msg = mGameStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("visibility",View.INVISIBLE);
        msg.setData(b);
        mGameStatusHandler.sendMessage(msg);
    }

    public void setScoreText(String playerScore, String opponentScore){
        Message msg = mScoreHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("player", playerScore);
        b.putString("opponent", opponentScore);
        msg.setData(b);
        mScoreHandler.sendMessage(msg);

    }
}

