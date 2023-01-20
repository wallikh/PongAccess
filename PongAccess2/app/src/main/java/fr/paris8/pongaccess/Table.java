package fr.paris8.pongaccess;


import static android.content.Context.SENSOR_SERVICE;
//import static androidx.core.app.AppOpsManagerCompat.Api23Impl.getSystemService;

import static androidx.lifecycle.Transformations.map;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

public class Table extends SurfaceView implements SurfaceHolder.Callback {



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

    private BlueActivity bleuActivity;
    private BlueActivity.ReadInput readInput;
    private float valeur ;



    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private static BluetoothSocket mBTSocket;
    private BlueActivity.ReadInput mReadThread = null;


    private boolean mIsUserInitiatedDisconnect = false;

    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;

    // modif du 07/01
    public static String strInput;
    public static byte[] buffer = new byte[256];

    //Juba
   /* private SensorManager mSensorManager;
    private Sensor mSensor;
    private float mSensorValue;

    private void setupSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_POTENTIOMETER);
        mSensorManager.registerListener((SensorEventListener) this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }*/


    public void initTable(Context ctx, AttributeSet attr) throws IOException {
        mContext = ctx;
        mHolder = getHolder();
        mHolder.addCallback(this);

        // pour deboguer



        afficher_debug();
        gestion();
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

    public Integer getStr() throws InterruptedException {
        readInput = new BlueActivity.ReadInput();
        //new Thread(readInput).start();
        new Thread(readInput).currentThread();

        int i = 0;
        Integer nombre = 0;
        for (i = 0; i < BlueActivity.buffer.length && BlueActivity.buffer[i] != 0; i++) {
            bleuActivity.strInput = new String(bleuActivity.buffer, 0, i);
            //System.out.println("++++++++++++++++valeur strInput dans PongTable :+++:" + BlueActivity.strInput);
            Thread.sleep(5);

            try {
                new Thread(readInput).start();
                nombre = Integer.parseInt(BlueActivity.strInput);
                //float flt = (float) nombre;
               System.out.println("val en int : ---------------- " + nombre);
                //System.out.println("val en flt : ---------------- " + flt);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return nombre;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mJeu.sensorsOn()){
            float u = map(valeur, 0, 1080, 0, 1080);
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if (mJeu.isBetweenRounds()){
                        mJeu.setState(JeuThread.STAT_RUNNING);
                    }else {
                        if (raquetteTouche(event, mJoueur)){
                            bouger = true;

                            mTouchDernierY = u;
                            // c'est là que ça doit se passer !!!!!!!!!!!!!!!!!!!!!!


                        }
                    }
                    break;
                // da i texreb !!!!
                case MotionEvent.ACTION_MOVE:
                    if (bouger){
                        try {

                            //getStr();
                            valeur = (float) getStr();
                            float y = u;


                            System.out.println("MotionEvent.AXIS_VSCROLL :::yyyyyyyyyyy:::"+valeur+"uuuu"+u);
                        float deltay = y - mTouchDernierY;
                       // int z = (int) event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                       // System.out.println("MotionEvent.AXIS_VSCROLL :::getAxisValue:::"+z);



                            System.out.println("la valeur en float : "+valeur+"    la valeur de y : "+y );

                            mTouchDernierY = y;
                            System.out.println("getY getY  mTouchDernierY : "+mTouchDernierY);

                            bougerRaquetJoueur(deltay, mJoueur);
                            System.out.println("deltay+++++++++++++++++++++++++"+deltay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    bouger = false;
                    break;
            }
        }else {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                if (mJeu.isBetweenRounds()){
                    mJeu.setState(JeuThread.STAT_RUNNING);
                }
            }
        }

        return true;

    }

    private float map(float valeur, int i, int i1, int i2, int i3) {
        return valeur;
    }

    public JeuThread getGame(){
        return mJeu;
    }

    public static void bougerRaquetJoueur(float dy, Joueur player){

        synchronized (mHolder){
            bougerJoueur(player, player.bounds.left, player.bounds.top + dy);
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

    public void afficher_debug(){
        //Log.d("info strInput tooth**************","info :***********vide pour le moment"+MonitoringScreen.ReadInput );
        try {
            float x = (float) getStr();
            System.out.println(" debuguer kan moment !!!!!!!!!!!!!!!"+x);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    /**
     * besoin de ce code pour recuperer la valeur de strInput ici
     */
    private void gestion(){
        try {
            while(true){

                int essai = getStr();
                float z = Float.parseFloat(String.valueOf(essai));

                System.out.println(z+"^^^^^^^^^^^");
                Table.bougerRaquetJoueur(z , mJoueur);
            }
        }catch (Exception e){
            System.out.println("erreur");
        }

    }
    public static class ReadInput implements Runnable {

        public boolean bStop = false;
        private Thread t;



        //public String strInput;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }


        public boolean isRunning() {
            return t.isAlive();
        }




        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {

                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {

                        }
                        strInput = new String(buffer, 0, i);
                        //strInputstrInput = mReadThread.strInput;
                        System.out.println("Debogage info strInput tooth**************info :***********"+strInput);

                        // gestion();
                        Log.d("info strInput tooth**************","info :***********"+strInput+"********en byte ");






                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */



                        if (true) {
                            new Runnable() {
                                @Override
                                public void run() {


                                }
                            };
                        }

                    }

                    Thread.sleep(200);
                }

            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

}}


