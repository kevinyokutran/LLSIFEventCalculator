package com.example.kevinytran.llsifeventcalculator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ResultActivity extends ActionBarActivity {

    TextView lovecaTV2;
    TextView songPlaysTV2;
    TextView naturalTV2;
    TextView timeTV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        lovecaTV2 = (TextView) findViewById(R.id.lovecaTV2);
        songPlaysTV2 = (TextView) findViewById(R.id.songPlaysTV2);
        naturalTV2 = (TextView) findViewById(R.id.naturalTV2);
        timeTV2 = (TextView) findViewById(R.id.timeTV2);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            lovecaTV2.setText(String.valueOf(extras.getInt("loveca")));
            songPlaysTV2.setText(String.valueOf(extras.getInt("songPlays")));
            naturalTV2.setText(String.valueOf(extras.getInt("natural")));
            timeTV2.setText(String.valueOf(getTimeNeeded(extras.getInt("songPlays"))));

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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

    //converts the amount of loveca into money -> 1 loveca at approx. 58 cents (50loveca:$86)
    private double getMoney(int loveca){
        double money = loveca * (50/86);

        return money;
    }

    private String getTimeNeeded(int playCount){
        String time = "";
        int minutes = playCount*3;
        if (minutes >= 1440){
            int days = minutes/1440;
            minutes = minutes - days*1440;
            time = String.valueOf(days) + ":";
        }
        if (minutes >= 60){
            int hours = minutes/60;
            minutes = minutes - hours*60;
            time = time + String.valueOf(hours) + ":";
        }
        time = time + String.valueOf(minutes);

        return time;
    }

}
