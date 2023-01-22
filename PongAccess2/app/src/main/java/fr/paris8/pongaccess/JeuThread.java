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

/**
 * •	STAT_READY, STAT_PAUSED, STAT_RUNNING, STAT_WIN et STAT_LOSE sont des constantes qui indiquent
 *      l'état actuel de la partie.
 * •	mCtx est le contexte de l'application utilisée pour accéder aux ressources.
 * •	mSurfaceHolder est un SurfaceHolder qui contient la surface de dessin pour afficher le jeu.
 * •	mPongTable est un objet de la classe Table qui contient les informations de jeu telles que les
 *      joueurs, la balle, etc.
 * •	mGameStatusHandler et mScoreHandler sont des gestionnaires utilisés pour mettre à jour l'interface
 *      utilisateur avec des informations sur le statut de la partie et les scores.
 * •	mRun est un booléen qui indique si la boucle de jeu doit continuer à s'exécuter.
 * •	mGameState est un entier qui contient l'état actuel de la partie.
 * •	mRunLock est un objet utilisé pour synchroniser l'accès à la variable mRun.
 * •	PHYS_FPS est une constante qui indique le nombre de mises à jour de physique par seconde dans le jeu.
 */
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

    /**
     * La méthode run() est la méthode principale du thread JeuThread.
     * Elle est exécutée lorsque le thread est démarré
     * La boucle while(mRun) va s'exécuter tant que mRun est vrai. Elle permet de maintenir le jeu en cours d'exécution
     * La variable mNextGameTic est utilisée pour maintenir une fréquence de mise à jour constante pour le jeu.
     * La variable skipTics contient le temps entre chaque mise à jour.
     * Dans le try, on verrouille le canvas pour dessiner dessus et on vérifie si le jeu est en cours
     * d'exécution. Si c'est le cas, on appelle la méthode misAJour() de la table de jeu pour
     * mettre à jour les positions des éléments du jeu. Ensuite, on vérifie si le thread est toujours
     * en cours d'exécution et si c'est le cas, on appelle la méthode draw() de la table de jeu pour
     * dessiner les éléments à l'écran.
     */
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

    /**
     * La méthode "setState" est utilisée pour définir l'état actuel du jeu. Elle prend un argument
     * entier, qui représente l'état, et met à jour l'état du jeu en conséquence. En fonction de l'état
     * transmis, elle effectue différentes actions. Par exemple, lorsque l'état transmis est STAT_READY,
     * la méthode appelle "setUpNewRound()" pour lancer un nouveau tour de jeu. Lorsque l'état transmis
     * est STAT_WIN, la méthode attribue la valeur "Win" au texte d'état, incrémente le score du joueur
     * et appelle "setUpNewRound()" pour lancer un nouveau tour. De même, elle incrémente le score de
     * l'adversaire lorsque l'état transmis est STAT_LOSE et appelle "setUpNewRound()" pour commencer
     * un nouveau tour. Lorsque l'état transmis est STAT_PAUSED, la méthode attribue au texte d'état
     * la valeur "Paused".
     * @param state
     */
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

    /**
     * Cette méthode, setUpNewRound(), est appelée lorsque l'état du jeu passe à "prêt" (STAT_READY),
     * "gagnant" (STAT_WIN) ou "perdant" (STAT_LOSE). Son but est de placer la table dans son état
     * initial pour un nouveau tour de jeu. Elle est appelée au sein d'un bloc synchronisé sur l'objet
     * mSurfaceHolder, qui est probablement utilisé pour s'assurer que la table est placée dans son
     * état initial avant d'être dessinée à nouveau à l'écran. La méthode appelle la méthode
     * placerTable() sur l'objet mPongTable, qui définit les positions des différents éléments de la
     * table (tels que la balle et les raquettes) à leurs positions de départ.
     */
    public void setUpNewRound(){
        synchronized (mSurfaceHolder){
            mPongTable.placerTable();
        }
    }

    /**
     * permet de définir si le thread est en cours d'exécution ou non. Elle prend en paramètre un
     * booléen "running" qui indique si le thread doit s'exécuter ou pas. La méthode utilise un objet
     * "mRunLock" pour synchroniser l'accès à la variable "mRun" afin d'éviter les problèmes de
     * concurrence
     * @param running
     */
    public void setRunning(boolean running){
        synchronized (mRunLock){
            mRun = running;
        }
    }

    public boolean sensorsOn(){

        return mSensorsOn;
    }

    /**
     * retourne vrai si l'état actuel de la partie n'est pas en cours d'exécution (STAT_RUNNING).
     * Elle retourne faux si l'état actuel de la partie est en cours d'exécution.
     * @return
     */
    public boolean isBetweenRounds(){

        return mGameState != STAT_RUNNING;
    }

    /**
     * permet de définir le texte de statut de l'application. Elle utilise un objet Message pour
     * envoyer un message au gestionnaire de statut de jeu (mGameStatusHandler).
     * Le message contient des données sous forme de Bundle, qui incluent le texte à afficher et
     * la visibilité du texte. Le message est ensuite envoyé au gestionnaire de statut de jeu.
     * @param text
     */
    private void setStatusText(String text){
        Message msg = mGameStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putInt("visibility", View.VISIBLE);
        msg.setData(b);
        mGameStatusHandler.sendMessage(msg);
    }

    /**
     * envoie un message au gestionnaire de statut de jeu qui permet de rendre invisible le texte de
     * statut. Elle utilise un objet de type Bundle pour stocker les données à envoyer avec le message,
     * qui comprend la visibilité (View.INVISIBLE) pour cacher le texte de statut. Enfin, elle envoie
     * le message au gestionnaire de statut de jeu.
     */
    private void hideStatusText(){
        Message msg = mGameStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("visibility",View.INVISIBLE);
        msg.setData(b);
        mGameStatusHandler.sendMessage(msg);
    }

    /**
     * prend en entrée deux chaînes de caractères: "playerScore" et "opponentScore".
     * Il utilise un objet "Message" pour envoyer ces deux chaînes de caractères à un "Handler"
     * spécifié par "mScoreHandler" via un objet "Bundle" nommé "b". Cette méthode permet de mettre
     * à jour les scores affichés à l'écran pour le joueur et l'adversaire.
     * @param playerScore
     * @param opponentScore
     */
    public void setScoreText(String playerScore, String opponentScore){
        Message msg = mScoreHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("player", playerScore);
        b.putString("opponent", opponentScore);
        msg.setData(b);
        mScoreHandler.sendMessage(msg);

    }
}

