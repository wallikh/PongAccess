package fr.paris8.pongaccess;




import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class PongActivity extends AppCompatActivity {

    private JeuThread mJeuThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong);


        final Table table = (Table) findViewById(R.id.PongTable);
        table.setScoreOpponent((TextView) findViewById(R.id.scoreAdversaire));
        table.setScorePlayer((TextView) findViewById(R.id.scoreJoueur1));
        table.setStatusView((TextView) findViewById(R.id.statutJeu));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  // ajoutée du git

        mJeuThread = table.getGame();


    }
}