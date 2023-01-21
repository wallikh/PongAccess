package fr.paris8.pongaccess;
import static fr.paris8.pongaccess.BlueActivity.ReadInput.strInput;

import fr.paris8.pongaccess.BlueActivity.ReadInput;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Table extends SurfaceView implements SurfaceHolder.Callback {


    public int racketWidth;
    public int racketHeight;
    //public static final String TAG = PongTable.class.getSimpleName();
    private JeuThread mJeu;
    private TextView mStatus;
    private TextView mScoreJoueur;
    private TextView mScoreAdversaire;

    MediaPlayer mediaPlayer;
    Integer[] integer = {1,-1};
    int x = randBetween(0,1);


    private Joueur mJoueur;
    private Joueur mAdversaire;
    private Balle mBalle;
    private Paint mNetPaint;
    private Paint mTableBoundPaint;
    private static int mTableWidth;
    private static int mTableHeight;
    private Context mContext;


    static SurfaceHolder mHolder;
    public static float VIT_RAQUETE = 15.0f;
    public static float VIT_BALLE = 15.0f;

    private float mIaProbabilite;

    private boolean bouger =false;
    private float mTouchDernierY;
    // ajouté le 05/01



    public void initTable(Context ctx, AttributeSet attr) throws IOException {
        mContext = ctx;
        mHolder = getHolder();
        mHolder.addCallback(this);

        // pour deboguer








        // threads et initialisation de la boucle
        mJeu = new JeuThread(this.getContext(), mHolder, this, new Handler(){

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                mStatus.setVisibility(msg.getData().getInt("visibility"));
                mStatus.setText(msg.getData().getString("text"));
            }

        }, new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                mScoreJoueur.setText(msg.getData().getString("player"));
                mScoreAdversaire.setText(msg.getData().getString("opponent"));

            }
        });

        TypedArray a = ctx.obtainStyledAttributes(attr, R.styleable.PongTable);
        int racketHeight = a.getInteger(R.styleable.PongTable_racketHeight,340);
        int racketWidth = a.getInteger(R.styleable.PongTable_racketWidth,100);
        int balleRadius = a.getInteger(R.styleable.PongTable_ballRadius,35);

        // set player
        Paint joueurPaint = new Paint();
        joueurPaint.setAntiAlias(true);
        joueurPaint.setColor(ContextCompat.getColor(mContext,R.color.player_color));
        mJoueur = new Joueur(racketWidth, racketHeight, joueurPaint);

        //set opponent
        Paint adversairePaint = new Paint();
        adversairePaint.setAntiAlias(true);
        adversairePaint.setColor(ContextCompat.getColor(mContext,R.color.opponent_color));
        mAdversaire = new Joueur(racketWidth, racketHeight, adversairePaint);

        //set ball
        Paint ballePaint = new Paint();
        ballePaint.setAntiAlias(true);
        ballePaint.setColor(ContextCompat.getColor(mContext,R.color.ball_color));
        mBalle = new Balle(balleRadius, ballePaint);

        //dessiner la ligne mediane
        mNetPaint = new Paint();
        mNetPaint.setAntiAlias(true);
        mNetPaint.setColor(Color.WHITE);
        mNetPaint.setAlpha(80);
        mNetPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNetPaint.setStrokeWidth(10.f);
        mNetPaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0 ));

        // dessiner les bords
        mTableBoundPaint = new Paint();
        mTableBoundPaint.setAntiAlias(true);
        mTableBoundPaint.setColor(ContextCompat.getColor(mContext,R.color.table_color));
        mTableBoundPaint.setStyle(Paint.Style.STROKE);
        mTableBoundPaint.setStrokeWidth(15.0f);

        mIaProbabilite = 0.7f;

        // ajouté le 05/01





    }



    private static int randBetween(int start, int end){
        return start+ (int) Math.round(Math.random() * (end-start));
    }



    public Table(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        initTable(context,attrs);
    }

    public Table(Context context, AttributeSet attrs, int defStyleAttr) throws IOException {
        super(context, attrs, defStyleAttr);
        initTable(context,attrs);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw( canvas);

        canvas.drawColor(ContextCompat.getColor(mContext, R.color.table_color));
        canvas.drawRect(0,0, mTableWidth, mTableHeight, mTableBoundPaint);

        int middle =mTableWidth/2;
        canvas.drawLine(middle, 1, middle, mTableHeight - 1,mNetPaint);

        mJeu.setScoreText(String.valueOf(mJoueur.score),String.valueOf(mAdversaire.score));


        mJoueur.draw(canvas);
        mAdversaire.draw(canvas);
        mBalle.draw(canvas);

    }

    private void iA(){

        if (mAdversaire.bounds.top > mBalle.cy){
            bougerJoueur(mAdversaire, mAdversaire.bounds.left, mAdversaire.bounds.top - VIT_RAQUETE);
        }else if (mAdversaire.bounds.top + mAdversaire.getRacquetHeight() < mBalle.cy){
            bougerJoueur(mAdversaire, mAdversaire.bounds.left, mAdversaire.bounds.top + VIT_RAQUETE);
        }
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mJeu.setRunning(true);
        mJeu.start();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mTableWidth = width;
        mTableHeight = height;

        mJeu.setUpNewRound();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        mJeu.setRunning(false);
        while (retry){
            try {
                mJeu.join();
                retry = false;

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void potentiometreRaquette() {
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

// This schedule a task to run every 200 milliseconds:
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // Mise à jour de la position de la raquette en utilisant la valeur de strInput
                float value = Float.parseFloat(strInput);
                bougerRaquetJoueur(value, mJoueur);
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String strInput = ReadInput.strInput;
        try{
            float value = Float.parseFloat(strInput);



                    if (mJeu.isBetweenRounds()){
                        mJeu.setState(JeuThread.STAT_RUNNING);
                    }else {
                        //bougerRaquetJoueur(value, mJoueur);
                        potentiometreRaquette();
                    }

        }catch(NumberFormatException e){
            System.out.println("Impossible de convertir strInput en float : "+e.getMessage());
        }
        return true;

    }


    public JeuThread getGame(){
        return mJeu;
    }

    public static void bougerRaquetJoueur(float deltay, Joueur player){

        synchronized (mHolder){
            bougerJoueur(player, player.bounds.left, player.bounds.top + deltay);
        }
    }

    private boolean raquetteTouche(MotionEvent event, Joueur mPlayer){
        return mPlayer.bounds.contains(event.getX(),event.getY());
    }

    public static synchronized void bougerJoueur(Joueur player, float left, float top){

        if (left < 2){
            left = 2;
        }else if (left + player.getRacquetWidth() >= mTableWidth - 2){
            left = mTableWidth - player.getRacquetWidth() - 2;
        }

        if (top < 0){
            top = 0;
        }else if (top + player.getRacquetHeight() >= mTableHeight ){
            top = mTableHeight - player.getRacquetHeight() -1;
        }

        player.bounds.offsetTo(left, top);
    }


    public void misAJour(Canvas canvas){
        // collisions code
        if (collisionJoueurs(mJoueur,mBalle)){
            handleCollision(mJoueur,mBalle);
            mediaPlayer=MediaPlayer.create(mContext,0);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }else if (collisionJoueurs(mAdversaire,mBalle)){
            handleCollision(mAdversaire,mBalle);
            mediaPlayer=MediaPlayer.create(mContext,0);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }else if (collisionHautEtBas()){
            mBalle.velocity_y = - mBalle.velocity_y;
        }else if (collisionGauche()){
            mJeu.setState(JeuThread.STAT_LOSE);
            return;
        }else if (collisionDroite()){
            mJeu.setState(JeuThread.STAT_WIN);
            return;
        }


        if (new Random(System.currentTimeMillis()).nextFloat() < mIaProbabilite){
            iA();
        }
        mBalle.bougerBalle(canvas);
    }


    private boolean collisionJoueurs(Joueur player, Balle balle){
        return player.bounds.intersects(
                balle.cx - balle.getRadius(),
                balle.cy - balle.getRadius(),
                balle.cx + balle.getRadius(),
                balle.cy + balle.getRadius());

    }

    private boolean collisionHautEtBas(){
        return ((mBalle.cy <= mBalle.getRadius())  || (mBalle.cy +mBalle.getRadius() >= mTableHeight -1) );
    }

    private boolean collisionGauche(){
        return mBalle.cx <= mBalle.getRadius();
    }

    private boolean collisionDroite(){
        return mBalle.cx + mBalle.getRadius() >= mTableWidth - 1;
    }

    private void handleCollision(Joueur player, Balle balle){
        balle.velocity_x = -balle.velocity_x*1.05f;
        if (player == mJoueur){
            balle.cx = mJoueur.bounds.right + balle.getRadius();
        }else if (player == mAdversaire){
            balle.cx = mAdversaire.bounds.left-balle.getRadius();
            VIT_RAQUETE = VIT_RAQUETE * 1.03f;

        }
    }

    public void placerTable(){
        placerBalle();
        placerJoueurs();
    }

    private void placerJoueurs(){
        mJoueur.bounds.offsetTo(2, (mTableHeight - mJoueur.getRacquetHeight())/2);
        mAdversaire.bounds.offsetTo(mTableWidth - mAdversaire.getRacquetWidth() - 2,
                (mTableHeight - mAdversaire.getRacquetHeight())/2);
    }

    private void placerBalle(){
        mBalle.cx = mTableWidth/2;
        mBalle.cy = mTableHeight/2;

        mBalle.velocity_y = integer[x]*((mBalle.velocity_y / Math.abs(mBalle.velocity_y)) * VIT_BALLE);
        mBalle.velocity_x = -(mBalle.velocity_x / Math.abs(mBalle.velocity_x)) * VIT_BALLE;
    }

    public Joueur getPlayer(){return mJoueur;}
    public Joueur getOpponent(){return mAdversaire;}



    public void setScorePlayer(TextView view){
        mScoreJoueur =view;}
    public void setScoreOpponent(TextView view){
        mScoreAdversaire =view;}
    public void setStatusView(TextView view){mStatus=view;}







}


