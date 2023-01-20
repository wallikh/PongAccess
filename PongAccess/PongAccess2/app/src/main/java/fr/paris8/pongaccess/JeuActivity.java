package fr.paris8.pongaccess;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JeuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);
    }

    public void startGame(View view) {
        Intent intent = new Intent(JeuActivity.this, PongActivity.class);
        startActivity(intent);
    }
}