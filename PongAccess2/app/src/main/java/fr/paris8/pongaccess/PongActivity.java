package fr.paris8.pongaccess;




import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

public class PongActivity extends AppCompatActivity {

    private JeuThread mJeuThread;

    /**
     * . Dans cette méthode, le contenu de l'interface utilisateur est défini en utilisant
     * setContentView(R.layout.activity_pong), qui lie le layout XML activity_pong à l'activité PongActivity.
     * Ensuite, une référence à la vue de type Table est obtenue en utilisant findViewById(R.id.PongTable) et
     * stockée dans la variable table. Les TextViews utilisées pour afficher le score du joueur et de l'adversaire,
     * ainsi que le statut du jeu, sont ensuite liées à l'objet Table en utilisant les méthodes setScoreOpponent,
     * setScorePlayer, et setStatusView.
     * Une variable mJeuThread est ensuite déclarée et initialisée en utilisant la méthode getGame() de l'objet Table.
     * Cette variable est utilisée pour stocker une référence à l'objet JeuThread qui gère le déroulement du jeu.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong);


        final Table table = (Table) findViewById(R.id.PongTable);
        table.setScoreOpponent((TextView) findViewById(R.id.scoreAdversaire));
        table.setScorePlayer((TextView) findViewById(R.id.scoreJoueur1));
        table.setStatusView((TextView) findViewById(R.id.statutJeu));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mJeuThread = table.getGame();
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels; System.out.println("width  : "+width);
        int height = displayMetrics.heightPixels;System.out.println("heigth  : "+height);


    }
}