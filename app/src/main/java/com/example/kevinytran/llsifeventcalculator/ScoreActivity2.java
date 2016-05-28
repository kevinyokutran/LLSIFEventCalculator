package com.example.kevinytran.llsifeventcalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class ScoreActivity2 extends ActionBarActivity {
    //GUI Declarations
    Button nextButton;
    EditText expertET;
    EditText hardET;
    EditText normalET;
    EditText easyET;
    Spinner difficultySpinner;
    Spinner scoreSpinner;
    Spinner placeSpinner;

    double exp = 0;
    double natural = 0;
    int playCount = 0;
    int ranksGained = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_activity2);

        nextButton = (Button) findViewById(R.id.nextButton);
        expertET = (EditText) findViewById(R.id.expertET);
        hardET = (EditText) findViewById(R.id.hardET);
        normalET = (EditText) findViewById(R.id.normalET);
        easyET = (EditText) findViewById(R.id.easyET);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        scoreSpinner = (Spinner) findViewById(R.id.scoreSpinner);
        placeSpinner = (Spinner) findViewById(R.id.comboSpinner);

        //nextButton listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResult(view);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_score_activity2, menu);
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

    //gets points gained from a loveca refill, S score and 2nd place used as an average
    private double getRefillPoints(){
        double points = Integer.parseInt(expertET.getText().toString())*getPPP("Expert", "S", "2nd")
                + Integer.parseInt(hardET.getText().toString())*getPPP("Hard", "S", "2nd")
                + Integer.parseInt(normalET.getText().toString())*getPPP("Normal", "S", "2nd")
                + Integer.parseInt(easyET.getText().toString())*getPPP("Easy", "S", "2nd");

        return points;
    }

    private int getEXP(String userDiff){
        int expGain;
        if (userDiff.equals("Expert")) expGain = 83;
        else if (userDiff.equals("Hard")) expGain = 46;
        else if (userDiff.equals("Normal")) expGain = 26;
        else expGain = 12;

        return expGain;
    }

    private int getRefillEXP(){
        int expGain = Integer.parseInt(expertET.getText().toString())*83
                + Integer.parseInt(hardET.getText().toString())*46
                + Integer.parseInt(normalET.getText().toString())*26
                + Integer.parseInt(easyET.getText().toString())*12;

        return expGain;
    }

    private int getRefillPlays(){
        int refill = Integer.parseInt(expertET.getText().toString())
                + Integer.parseInt(hardET.getText().toString())
                + Integer.parseInt(normalET.getText().toString())
                + Integer.parseInt(easyET.getText().toString());

        return refill;
    }

    //checks whether lp used in regular lives exceeds the player's max LP
    private boolean goodLPusage(int rank){
        boolean exceeds = true;
        double maxLP = 25 + Math.floor(Math.min(rank, 300)/2) + Math.floor(Math.max(rank-300,0)/3);
        int lpUsage;
        int expertPlays = Integer.parseInt(expertET.getText().toString());
        int hardPlays = Integer.parseInt(hardET.getText().toString());
        int normalPlays = Integer.parseInt(normalET.getText().toString());
        int easyPlays = Integer.parseInt(easyET.getText().toString());
        lpUsage = 25*expertPlays + 15*hardPlays + 10*normalPlays + 5*easyPlays;
        if (maxLP < lpUsage) exceeds = false;

        return exceeds;
    }

    //checks whether the distribution is not all zeroes
    private boolean actuallyPlays(){
        boolean result = true;
        if (expertET.getText().toString().equals("0")
                && hardET.getText().toString().equals("0")
                && normalET.getText().toString().equals("0")
                && easyET.getText().toString().equals("0"))
            result = false;

        return result;
    }

    private String getSkill(int exp, int hard, int norm, int easy){
        String skill;
        if (exp> 0) skill = "Expert";
        else if (hard > 0) skill = "Hard";
        else if (norm > 0) skill = "Normal";
        else skill = "Easy";
        return skill;
    }

    //Gets the amount points without loveca use
    private double getNaturalPoints(int rank, double hours, double expToNext){
        double points = 0;
        int expertPlays = Integer.parseInt(expertET.getText().toString());
        int hardPlays = Integer.parseInt(hardET.getText().toString());
        int normalPlays = Integer.parseInt(normalET.getText().toString());
        int easyPlays = Integer.parseInt(easyET.getText().toString());
        double minutesLeft = hours*10;
        String eventDiff = difficultySpinner.getSelectedItem().toString();
        String eventScore = scoreSpinner.getSelectedItem().toString();
        String eventPlace = placeSpinner.getSelectedItem().toString();
        String skill = getSkill(expertPlays, hardPlays, normalPlays, easyPlays);
        while (minutesLeft > 0){
            if (skill.equals("Expert")){
                minutesLeft = minutesLeft - 25;
                exp = exp + 83;
            }
            else if (skill.equals("Hard")){
                minutesLeft = minutesLeft - 15;
                exp = exp + 46;
            }
            else if (skill.equals("Normal")){
                minutesLeft = minutesLeft - 10;
                exp = exp + 26;
            }
            else{
                minutesLeft = minutesLeft - 5;
                exp = exp + 12;
            }
            points = points + getPPP(eventDiff, eventScore, eventPlace);
            playCount++;
        }
        //Adds points from level-ups
        while (exp > 0){
            if (exp > expToNext) {
                rank++;
                ranksGained++;
                exp = exp - expToNext;
                expToNext = Math.round(34.45 * rank - 551);
            }
            else exp = -1; //exit loop
        }
        System.out.println("Natural Points is: " + points);

        natural = points;
        return points;
    }

    //gets PPP (points per play) for Score Match
    private double getPPP(String eventDiff, String eventScore, String eventPlace){
        double points = 0;
        //base
        if (eventDiff.equals("Expert")) points = 272;
        else if (eventDiff.equals("Hard")) points = 163;
        else if (eventDiff.equals("Normal")) points = 89;
        else points = 36;
        //score multiplier
        if (eventScore.equals("S")) points = points * 1.2;
        else if (eventScore.equals("A")) points = points * 1.15;
        else if (eventScore.equals("B")) points = points * 1.1;
        else if (eventScore.equals("C")) points = points * 1.05;
        //rank multiplier
        if (eventPlace.equals("1st")) points = points * 1.25;
        else if (eventPlace.equals("2nd")) points = points * 1.15;
        else if (eventPlace.equals("3rd")) points = points * 1.05;
        if(eventDiff.equals("Fail")){
            if (eventDiff.equals("Expert")) points = 91;
            else if (eventDiff.equals("Hard")) points = 55;
            else if (eventDiff.equals("Normal")) points = 30;
            else points = 12;
        }
        System.out.println("Points is: " + points);
        return points;
    }

    //Gets event points (including tokens) gained from natural LP regen
    private int getLoveca(double target, int rank, double hours, int currentPoints){
        String userDifficulty = difficultySpinner.getSelectedItem().toString();
        String score = scoreSpinner.getSelectedItem().toString();
        String placing = placeSpinner.getSelectedItem().toString();
        int loveca = 0;
        double expToNext = Math.round(34.45 * rank - 551);
        natural = getNaturalPoints(rank, hours, expToNext);
        target = target
                - currentPoints
                - natural;
        rank = rank + ranksGained;
        ranksGained = 0;
        if (target <= 0) loveca = 0;
        else{
            while (target > 0) {
                loveca++;
                target = target - getRefillPoints();
                playCount = playCount + getRefillPlays();
                exp = exp + getRefillEXP();
            }
        }
        //Takes away the loveca needed from ranks gained
        expToNext = Math.round(34.45 * rank - 551);
        while (exp > 0){
            if (exp > expToNext) {
                rank++;
                ranksGained++;
                loveca--;
                exp = exp - expToNext;
                expToNext = Math.round(34.45 * rank - 551);
            }
            else exp = -1; //exit loop
        }

        return loveca;
    }

    //set empty EditTexts to 0
    private void setZero(View view) {
        if (expertET.getText().toString().isEmpty()) expertET.setText("0");
        if (hardET.getText().toString().isEmpty()) hardET.setText("0");
        if (normalET.getText().toString().isEmpty()) normalET.setText("0");
        if (easyET.getText().toString().isEmpty()) easyET.setText("0");
    }

    public void openResult(View view) {
        //int playCount = regCount + eventCount;
        setZero(view);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            double target = extras.getInt("target");
            int rank = extras.getInt("rank")+ranksGained;
            double hours = extras.getDouble("hours");
            int currentPoints = extras.getInt("currentPoints");
            if (goodLPusage(rank) && actuallyPlays()){
                int loveca = getLoveca(target, rank, hours, currentPoints);
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("loveca", loveca);
                intent.putExtra("natural", (int)natural);
                intent.putExtra("songPlays", playCount);
                intent.putExtra("endRank", rank);
                this.startActivity(intent);
            }
            else {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                String exceedLP = "The cumulative LP used by your live distribution exceeds your max LP.";
                String play = "You must play to get points. Do it for your raibu.";

                if (!goodLPusage(rank)) dlgAlert.setMessage(exceedLP);
                else if (!actuallyPlays()) dlgAlert.setMessage(play);
                else dlgAlert.setMessage("Please enter all fields");
                dlgAlert.setTitle("Error Message");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
            }

        }

    }

}

