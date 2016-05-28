package com.example.kevinytran.llsifeventcalculator;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    Button tokenButton;
    Button scoreButton;
    Button medleyButton;
    String selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenButton = (Button) findViewById(R.id.tokenButton);
        scoreButton = (Button) findViewById(R.id.scoreButton);
        medleyButton = (Button) findViewById(R.id.medleyButton);

        //tokenButton listener
        tokenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openToken(view);
            }
        });

        //scoreButton listener
        scoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openScore(view);
            }
        });

        //medleyButton listener
        medleyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openMedley(view);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openToken(View view) {
        Intent intent = new Intent(this, TokenActivity.class);
        this.startActivity(intent);
    }

    public void openScore(View view){
        Intent intent = new Intent(this, ScoreActivity.class);
        selection = "score";
        intent.putExtra("selection", selection);
        this.startActivity(intent);
    }

    public void openMedley(View view){
        Intent intent = new Intent(this, ScoreActivity.class);
        selection = "medley";
        intent.putExtra("selection", selection);
        this.startActivity(intent);
    }

}
