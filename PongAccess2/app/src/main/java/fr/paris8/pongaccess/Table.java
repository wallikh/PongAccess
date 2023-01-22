package fr.paris8.pongaccess;

import static fr.paris8.pongaccess.BlueActivity.ReadInput.reception;
import fr.paris8.pongaccess.BlueActivity.ReadInput;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cette classe définit une vue de type SurfaceView pour le jeu de Pong.
 * Elle implémente l'interface SurfaceHolder.Callback qui permet de gérer les événements liés
 * à la surface de la vue. La classe gère les objets du jeu tels que le joueur, l'adversaire et la balle.
 * Elle contient également des variables pour gérer les dimensions de la table de jeu, les couleurs et
 * les effets de dessin. Elle utilise également des handlers pour gérer les messages d'état et les scores.
 * Enfin, elle contient une méthode initTable() qui est utilisée pour initialiser les objets de jeu
 * et les threads de jeu.
 */
public class Table extends SurfaceView implements SurfaceHolder.Callback {


    public int racketWidth;
    public int racketHeight;
    private JeuThread mJeu;//	mJeu est un objet de la classe JeuThread qui est responsable de la logique de jeu et de la boucle de jeu.
    private TextView mStatus;//	mStatus est un objet TextView qui est utilisé pour afficher des informations de statut sur l'écran.
    private TextView mScoreJoueur;
    private TextView mScoreAdversaire;	//mScoreJoueur et mScoreAdversaire sont des objets TextView utilisés pour afficher les scores des joueurs

    //MediaPlayer mediaPlayer;
    Integer[] integer = {1,-1};//	integer est un tableau d'entiers utilisé pour stocker des valeurs prédéfinies.
    int x = randBetween(0,1);// 	x est un entier utilisé pour stocker un nombre aléatoire généré entre 0 et 1.


    private Joueur mJoueur;
    private Joueur mAdversaire;//	mJoueur et mAdversaire sont des objets de la classe Joueur qui représentent les joueurs.
    private Balle mBalle;//	mBalle est un objet de la classe Balle qui représente la balle utilisée dans le jeu.
    private Paint mNetPaint;
    private Paint mTableBoundPaint;//	mNetPaint et mTableBoundPaint sont des objets Paint utilisés pour dessiner les lignes et les bords de la table.
    private static int mTableWidth;
    private static int mTableHeight;//	mTableWidth et mTableHeight sont des entiers qui stockent les dimensions de la table.
    private Context mContext;//	mContext est un objet Context qui est utilisé pour accéder aux ressources de l'application.


    static SurfaceHolder mHolder;//	mHolder est un objet SurfaceHolder qui est utilisé pour gérer la surface de dessin.
    public static float VIT_RAQUETE = 15.0f;
    public static float VIT_BALLE = 15.0f;//VIT_RAQUETE et VIT_BALLE sont des variables statiques qui définissent respectivement la vitesse de déplacement de la raquette et de la balle.

    private float mIaProbabilite;

    private boolean bouger =false;// bouger est une variable booléenne utilisée pour déterminer si la raquette doit être déplacée ou non.
    //private float mTouchDernierY;



    /**
     * cette méthode est utilisée pour initialiser la table de jeu. Elle est appelée lors de la
     * création de l'objet Table. Elle prend en paramètre un contexte et un ensemble d'attributs.
     * •	Elle initialise un SurfaceHolder (mHolder) qui est utilisé pour gérer la surface de dessin
     *      de la table de jeu.
     * •	Elle crée un objet JeuThread (mJeu) qui est responsable de la boucle de jeu
     *      (c'est-à-dire la mise à jour de l'affichage de la table de jeu). Elle lui passe en
     *      paramètre un contexte, un SurfaceHolder, l'objet Table lui-même, un Handler pour gérer
     *      les messages de statut et un autre Handler pour gérer les scores.
     * •	Elle utilise un objet TypedArray pour récupérer des valeurs définies dans les attributs
     *      de la table de jeu (hauteur de raquette, largeur de raquette, rayon de la balle, etc.).
     * •	Elle crée des objets Paint pour définir les couleurs des différents éléments de la table
     *      de jeu (joueur, adversaire, balle, ligne médiane, bords de la table).
     * •	Elle initialise les objets Joueur (mJoueur et mAdversaire), Balle (mBalle) et Paint
     *      (mNetPaint, mTableBoundPaint) avec les valeurs obtenues à partir des attributs.
     * •	Elle définit une probabilité pour l'IA de l'adversaire (mIaProbabilite).mIaProbabilite est
     *      la probabilité pour l'IA de l'adversaire de réussir à toucher la balle.
     *      •	mHolder est un SurfaceHolder utilisé pour gérer la surface de dessin de la table de jeu.
     * •	mJeu est un objet JeuThread qui est responsable de la boucle de jeu.
     * •	mJoueur et mAdversaire sont des objets Joueur qui représentent les joueurs.
     * •	mBalle est un objet Balle qui représente la balle.
     */
    public void initTable(Context ctx, AttributeSet attr) throws IOException {
        mContext = ctx;
        mHolder = getHolder();
        mHolder.addCallback(this);


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

        // définir le joueur
        Paint joueurPaint = new Paint();
        joueurPaint.setAntiAlias(true);
        joueurPaint.setColor(ContextCompat.getColor(mContext,R.color.player_color));
        mJoueur = new Joueur(racketWidth, racketHeight, joueurPaint);

        //définir opposant ou adversaire
        Paint adversairePaint = new Paint();
        adversairePaint.setAntiAlias(true);
        adversairePaint.setColor(ContextCompat.getColor(mContext,R.color.opponent_color));
        mAdversaire = new Joueur(racketWidth, racketHeight, adversairePaint);

        //définir la balle
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

    }


    /**
     * La fonction "randBetween" génère un nombre aléatoire compris entre deux valeurs données en
     * entrée (start et end). La fonction utilise Math.random() pour générer un nombre aléatoire
     * entre 0 et 1, puis l'ajoute à la valeur de départ "start" et l'arrondit avec Math.round().
     * Le résultat est un entier aléatoire compris entre les valeurs de début et de fin.
     * @param start
     * @param end
     * @return
     */
    private static int randBetween(int start, int end){
        return start+ (int) Math.round(Math.random() * (end-start));
    }

    /**
     * La méthode Table(Context context, AttributeSet attrs) est un constructeur de la classe Table.
     * Il prend en entrée un contexte (généralement une activité) et un ensemble d'attributs
     * (un ensemble de paramètres définis dans un fichier XML). Il utilise ensuite la méthode initTable
     * pour initialiser les différents éléments de la classe Table.
     * @param context
     * @param attrs
     * @throws IOException
     */
    public Table(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        initTable(context,attrs);
    }

    /**
     * Cette méthode est un constructeur de la classe Table qui prend en paramètres un contexte,
     * des attributs et un style par défaut. Il utilise la méthode initTable pour initialiser la table
     * avec les paramètres fournis dans les attributs. Si une exception est lancée, elle doit être gérée
     * par la méthode appelante.
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @throws IOException
     */
    public Table(Context context, AttributeSet attrs, int defStyleAttr) throws IOException {
        super(context, attrs, defStyleAttr);
        initTable(context,attrs);
    }

    /**
     * Cette méthode est utilisée pour dessiner l'interface graphique du jeu.
     * •	super.draw(canvas) : Cette ligne appelle la méthode draw de la classe parente
     *      (ici SurfaceView) pour dessiner l'élément.
     * •	canvas.drawColor(ContextCompat.getColor(mContext, R.color.table_color)) : Cette ligne
     *      remplit l'arrière-plan de la surface de dessin avec la couleur de la table définie dans
     *      les ressources de l'application (R.color.table_color).
     * •	canvas.drawRect(0,0, mTableWidth, mTableHeight, mTableBoundPaint) : Cette ligne dessine un
     *      rectangle qui couvre toute la surface de dessin avec les propriétés de la brosse
     *      mTableBoundPaint (couleur, style, etc.).
     * •	int middle =mTableWidth/2; canvas.drawLine(middle, 1, middle, mTableHeight - 1,mNetPaint) :
     *      Cette ligne dessine une ligne au milieu de la surface de dessin avec les propriétés de
     *      la brosse mNetPaint (couleur, style, etc.).
     * •	mJeu.setScoreText(String.valueOf(mJoueur.score),String.valueOf(mAdversaire.score)) :
     *      Cette ligne met à jour les scores des joueurs dans le thread JeuThread.
     * •	mJoueur.draw(canvas); mAdversaire.draw(canvas); mBalle.draw(canvas) : Ces trois
     *      lignes appellent les méthodes draw des objets Joueur, Adversaire et Balle pour les
     *      dessiner sur la surface de dessin.
     * @param canvas
     */
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

    /**
     * La fonction iA() est une fonction qui définit le comportement de l'IA (Intelligence Artificielle)
     * du joueur-machine. Elle contrôle les mouvements de l'adversaire en utilisant la position de
     * la balle. La première instruction vérifie si la position en haut de la raquette du joueur-machine
     * est supérieure à celle de la balle. Si c'est le cas, la méthode bougerJoueur est appelée pour
     * déplacer la raquette de l'adversaire vers le haut avec une vitesse définie par la variable VIT_RAQUETE.
     * La deuxième instruction vérifie si la position en bas de la raquette du joueur-machine
     * est inférieure à celle de la balle. Si c'est le cas, la méthode bougerJoueur est appelée pour
     * déplacer la raquette de l'adversaire vers le bas avec une vitesse définie par la variable
     * VIT_RAQUETE.
     */
    private void iA(){

        if (mAdversaire.bounds.top > mBalle.cy){
            bougerJoueur(mAdversaire, mAdversaire.bounds.left, mAdversaire.bounds.top - VIT_RAQUETE);
        }else if (mAdversaire.bounds.top + mAdversaire.getRacquetHeight() < mBalle.cy){
            bougerJoueur(mAdversaire, mAdversaire.bounds.left, mAdversaire.bounds.top + VIT_RAQUETE);
        }
    }

    /**
     * appelée lorsque la surface sur laquelle dessiner est créée. Dans cette méthode,
     * le thread de jeu est démarré en appelant setRunning(true) pour indiquer que le thread doit
     * s'exécuter, puis start() pour lancer le thread.
     * @param holder
     */
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mJeu.setRunning(true);
        mJeu.start();

    }

    /**
     * Cette méthode est appelée lorsque la surface de dessin (SurfaceView) a été modifiée,
     * par exemple lorsque l'application est redimensionnée. Elle prend en entrée un objet SurfaceHolder
     * qui contient les informations sur la surface de dessin, le format de la surface,
     * la largeur et la hauteur.
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mTableWidth = width;
        mTableHeight = height;

        mJeu.setUpNewRound();
    }

    /**
     * une surcharge de la méthode surfaceDestroyed de l'interface SurfaceHolder.Callback.
     * Cette méthode est appelée lorsque la surface du jeu est détruite. Elle est utilisée pour
     * arrêter le thread de jeu (mJeu) en changeant sa propriété de running à false, puis en
     * utilisant une boucle while pour attendre que le thread se termine avec la méthode join.
     * Cela permet de s'assurer que le thread ne soit pas interrompu brutalement.
     * @param holder
     */
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

    /**
     * La méthode potentiometreRaquette() utilise un ScheduledExecutorService pour planifier une
     * tâche de mise à jour de la position de la raquette du joueur toutes les 200 millisecondes.
     * Cette tâche consiste à mettre à jour la position de la raquette en utilisant la valeur de
     * la variable reception (qui est récupérée à partir d'un potentiomètre externe).
     * La méthode bougerRaquetJoueur(float potentio, Joueur joueur) est appelée pour mettre à jour
     * la position de la raquette en utilisant la valeur de potentio. La méthode bougerRaquetJoueur
     * est censé être utilisé pour déplacer la raquette selon la valeur de potentio.
     */
    public void potentiometreRaquette() {
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // Cette tâche est planifiée pour être exécutée toutes les 200 millisecondes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // Mise à jour de la position de la raquette en utilisant la valeur de -reception-
                float potentio = Float.parseFloat(reception);
                bougerRaquetJoueur(potentio, mJoueur);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

    }


    /**
     * La méthode onTouchEvent() est un événement déclenché lorsque l'utilisateur touche l'écran.
     * Elle surcharge la méthode onTouchEvent() de la classe SurfaceView.
     * elle vérifie si le jeu est actuellement entre les rounds en utilisant la méthode isBetweenRounds()
     * de l'objet JeuThread. Si c'est le cas, elle met à jour l'état du jeu en utilisant la méthode
     * setState(JeuThread.STAT_RUNNING) pour démarrer le jeu. Sinon, elle appelle la méthode
     * potentiometreRaquette() pour mettre à jour la position de la raquette en utilisant la valeur
     * de "reception".
     * Enfin, elle renvoie true pour indiquer que l'événement a été géré.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String strInput = ReadInput.reception;
        try{
            //float potentio = Float.parseFloat(reception);
                    if (mJeu.isBetweenRounds()){
                        mJeu.setState(JeuThread.STAT_RUNNING);
                    }else {
                        //bougerRaquetJoueur(value, mJoueur);
                        potentiometreRaquette();
                    }

        }catch(NumberFormatException e){
            System.out.println("Impossible de convertir reception en float : "+e.getMessage());
        }
        return true;

    }


    /**
     * La méthode "getGame()" est utilisée pour récupérer l'instance de la classe "JeuThread" qui
     * est utilisée pour gérer le thread de jeu. Elle retourne l'objet "mJeu" qui est une variable
     * de classe de type "JeuThread" déclarée dans la classe "Table". Cela permet aux autres classes
     * d'accéder à l'objet "JeuThread" pour communiquer avec lui ou pour effectuer des actions
     * spécifiques sur celui-ci.
     * @return
     */
    public JeuThread getGame(){
        return mJeu;
    }

    /**
     * La méthode "bougerRaquetJoueur" prend en entrée un déplacement "deltay" et un joueur
     * "player" et utilise la méthode "bougerJoueur" pour déplacer la raquette du joueur en utilisant
     * les coordonnées actuelles de la raquette (left et top) et en y ajoutant le déplacement "deltay".
     * La synchronisation avec l'objet "mHolder" est utilisée pour s'assurer que les accès à la raquette
     * sont thread-safe.
     * @param deltay
     * @param player
     */
    public static void bougerRaquetJoueur(float deltay, Joueur player){

        synchronized (mHolder){
            bougerJoueur(player, player.bounds.left, player.bounds.top + deltay);
        }
    }

    private boolean raquetteTouche(MotionEvent event, Joueur mPlayer){
        return mPlayer.bounds.contains(event.getX(),event.getY());
    }

    /**
     * La dernière méthode "bougerJoueur" prend en paramètre un objet Joueur qui représente le joueur,
     * une valeur de coordonnée en x et une valeur de coordonnée en y pour déplacer la raquette.
     * Elle vérifie si les valeurs de coordonnées envoyées dépassent les limites de la surface de jeu.
     * Si c'est le cas, elle les modifie pour qu'elles restent dans les limites de la surface de jeu.
     * Enfin elle déplace la raquette en utilisant la méthode "offsetTo" de l'objet RectF qui représente
     * les limites de la raquette.
     * @param player
     * @param left
     * @param top
     */
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

    /**
     * •	La méthode misAJour() est utilisée pour mettre à jour les éléments dans le jeu,
     *      comme les positions des raquettes et de la balle.
     * •	La première étape de cette méthode vérifie si la balle entre en collision avec l'une des
     *      raquettes (mJoueur ou mAdversaire) en appelant la méthode collisionJoueurs().
     *      Si la collision est détectée, la méthode handleCollision() est appelée pour gérer
     *      les conséquences de la collision.
     * •	Ensuite, il vérifie si la balle entre en collision avec le haut ou le bas de l'écran en
     *      appelant la méthode collisionHautEtBas(). Si la collision est détectée, la vitesse y de
     *      la balle est inversée pour la faire rebondir.
     * •	Il vérifie également si la balle entre en collision avec le côté gauche ou droit de l'écran
     *      en appelant les méthodes collisionGauche() et collisionDroite(). Si la balle entre en collision
     *      avec le côté gauche, cela signifie que le joueur adverse a marqué un point et le jeu passe
     *      en état de défaite. Si elle entre en collision avec le côté droit, cela signifie que
     *      le joueur a marqué un point et le jeu passe en état de victoire.
     * •	Enfin, il y a une vérification aléatoire pour voir si l'IA doit bouger sa raquette pour
     *      suivre la balle en appelant la méthode iA(). La méthode bougerBalle() est également
     *      appelée pour mettre à jour la position de la balle sur l'écran.
     * @param canvas
     */
    public void misAJour(Canvas canvas){
        // collisions code
        if (collisionJoueurs(mJoueur,mBalle)){
            handleCollision(mJoueur,mBalle);
            //mediaPlayer=MediaPlayer.create(mContext,0);
            //mediaPlayer.setLooping(false);
            //mediaPlayer.start();
        }else if (collisionJoueurs(mAdversaire,mBalle)){
            handleCollision(mAdversaire,mBalle);
            //mediaPlayer=MediaPlayer.create(mContext,0);
            //mediaPlayer.setLooping(false);
            //mediaPlayer.start();
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


    /**
     * Cette méthode vérifie si la balle entre en collision avec le joueur spécifié.
     * Elle utilise la méthode intersects() de la classe Rect pour vérifier si les limites de la balle
     * (définies par ses coordonnées x et y, son rayon et les dimensions de la raquette du joueur)
     * se chevauchent. Si c'est le cas, la méthode retourne true, indiquant qu'il y a eu une collision
     * entre la balle et le joueur. Sinon, elle retourne false.
     * @param player
     * @param balle
     * @return
     */
    private boolean collisionJoueurs(Joueur player, Balle balle){
        return player.bounds.intersects(
                balle.cx - balle.getRadius(),
                balle.cy - balle.getRadius(),
                balle.cx + balle.getRadius(),
                balle.cy + balle.getRadius());

    }

    /**
     *vérifie si la balle entre en collision avec le haut ou le bas du tableau en comparant
     *  les coordonnées y de la balle avec le rayon de la balle et la hauteur du tableau
     * @return
     */
    private boolean collisionHautEtBas(){
        return ((mBalle.cy <= mBalle.getRadius())  || (mBalle.cy +mBalle.getRadius() >= mTableHeight -1) );
    }

    /**
     * () vérifie si la balle entre en collision avec le côté gauche du tableau en comparant
     * la coordonnée x de la balle avec le rayon de la balle.
     * @return
     */
    private boolean collisionGauche(){
        return mBalle.cx <= mBalle.getRadius();
    }

    /**
     * La méthode collisionDroite() vérifie si la balle entre en collision avec le côté droit
     * du tableau en comparant la coordonnée x de la balle avec la largeur du tableau moins
     * le rayon de la balle.
     * @return
     */
    private boolean collisionDroite(){
        return mBalle.cx + mBalle.getRadius() >= mTableWidth - 1;
    }

    /**
     * La méthode collisionJoueurs(Joueur player, Balle balle) vérifie si la balle entre en collision
     * avec un des joueurs en utilisant la méthode intersects() de l'objet Rect qui définit les limites
     * du joueur. Cette méthode prend en entrée les coordonnées x et y des coins supérieur gauche et
     * inférieur droit d'un rectangle et retourne vrai si les rectangles se chevauchent.
     * Dans ce cas, les coordonnées x et y sont calculées en utilisant les propriétés de
     * la balle (cx, cy, getRadius()).
     * @param player
     * @param balle
     */
    private void handleCollision(Joueur player, Balle balle){
        balle.velocity_x = -balle.velocity_x*1.05f;
        if (player == mJoueur){
            balle.cx = mJoueur.bounds.right + balle.getRadius();
        }else if (player == mAdversaire){
            balle.cx = mAdversaire.bounds.left-balle.getRadius();
            VIT_RAQUETE = VIT_RAQUETE * 1.03f;

        }
    }

    /**
     * permet de placer les éléments de jeu (balle et joueurs) à leur position initiale sur le terrain
     * de jeu. La méthode appelle les méthodes placerJoueurs() et placerBalle() pour placer chacun d'eux.
     */
    public void placerTable(){
        placerBalle();
        placerJoueurs();
    }

    /**
     * positionne les joueurs en définissant les coordonnées de leur bords gauche et haut.
     * Le joueur 1 est placé en haut à gauche (bords.offsetTo(2, (mTableHeight - mJoueur.getRacquetHeight())/2))
     * et le joueur 2 est placé en haut à droite
     */
    private void placerJoueurs(){
        mJoueur.bounds.offsetTo(2, (mTableHeight - mJoueur.getRacquetHeight())/2);
        mAdversaire.bounds.offsetTo(mTableWidth - mAdversaire.getRacquetWidth() - 2,
                (mTableHeight - mAdversaire.getRacquetHeight())/2);
    }

    /**
     * La méthode placerBalle() place la balle au milieu du terrain en définissant les coordonnées de
     * son centre (mBalle.cx = mTableWidth/2; mBalle.cy = mTableHeight/2;).
     * Elle définit également la vitesse initiale de la balle en définissant la valeur de ses
     * vecteurs de vitesse (mBalle.velocity_y = integer[x]*((mBalle.velocity_y / Math.abs(mBalle.velocity_y)) * VIT_BALLE);
     * mBalle.velocity_x = -(mBalle.velocity_x / Math.abs(mBalle.velocity_x)) * VIT_BALLE;)
     */
    private void placerBalle(){
        mBalle.cx = mTableWidth/2;
        mBalle.cy = mTableHeight/2;

        mBalle.velocity_y = integer[x]*((mBalle.velocity_y / Math.abs(mBalle.velocity_y)) * VIT_BALLE);
        mBalle.velocity_x = -(mBalle.velocity_x / Math.abs(mBalle.velocity_x)) * VIT_BALLE;
    }

    /**
     * prmet de recuperer l'objet joueur
     * @return
     */
    public Joueur getPlayer(){return mJoueur;}

    /**
     * prmet de recuperer l'objet joueur-adverse-ia
     * @return
     */
    public Joueur getOpponent(){return mAdversaire;}


    /**
     * permet de définir la vue qui affichera le score du joueur
     * @param view
     */
    public void setScorePlayer(TextView view){
        mScoreJoueur =view;}

    /**
     * permet de définir la vue qui affichera le score de l'adversaire
     * @param view
     */
    public void setScoreOpponent(TextView view){
        mScoreAdversaire =view;}

    /**
     * permet de définir la vue qui affichera le statut du jeu, comme par exemple "en cours" ou "terminé".
     * @param view
     */
    public void setStatusView(TextView view){mStatus=view;}







}


