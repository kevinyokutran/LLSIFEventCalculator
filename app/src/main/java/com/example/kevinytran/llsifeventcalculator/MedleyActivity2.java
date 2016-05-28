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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


public class MedleyActivity2 extends ActionBarActivity {

    //GUI Declarations
    Button nextButton;
    CheckBox expCB;
    CheckBox pointsCB;
    EditText expertET;
    EditText hardET;
    EditText normalET;
    EditText easyET;
    Spinner difficultySpinner;
    Spinner scoreSpinner;
    Spinner comboSpinner;
    Spinner songsSpinner;

    double exp = 0;
    double natural = 0;
    int playCount = 0;
    int ranksGained = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medley_activity2);

        nextButton = (Button) findViewById(R.id.nextButton);
        expCB = (CheckBox) findViewById(R.id.expCB);
        pointsCB = (CheckBox) findViewById(R.id.pointsCB);
        expertET = (EditText) findViewById(R.id.expertET);
        hardET = (EditText) findViewById(R.id.hardET);
        normalET = (EditText) findViewById(R.id.normalET);
        easyET = (EditText) findViewById(R.id.easyET);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        scoreSpinner = (Spinner) findViewById(R.id.scoreSpinner);
        comboSpinner = (Spinner) findViewById(R.id.comboSpinner);
        songsSpinner = (Spinner) findViewById(R.id.songsSpinner);

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
        getMenuInflater().inflate(R.menu.menu_medley_activity2, menu);
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

    //Gets points gained from loveca refill, Medley
    private double getRefillPoints(String diff, String score, String combo){
        double points = 0;
        int expert = Integer.parseInt(expertET.getText().toString());
        int hard = Integer.parseInt(hardET.getText().toString());
        int normal = Integer.parseInt(normalET.getText().toString());
        int easy = Integer.parseInt(easyET.getText().toString());
        if (expert%3 == 0)

        while (expert >= 3){
            points += getPPP(diff, score, combo, "3");
            expert -=  3;
        }
        if (expert == 2){
            points += getPPP(diff, score, combo, "2");
        }
        if (expert == 1){
            points += getPPP(diff, score, combo, "1");
        }
        while (hard >= 3){
            points += getPPP(diff, score, combo, "3");
            hard -= 3;
        }
        if (hard == 2){
            points += getPPP(diff, score, combo, "2");
        }
        if (hard == 1){
            getPPP(diff, score, combo, "1");
        }
        while (normal >= 3){
            points += getPPP(diff, score, combo, "3");
            normal -=  3;
        }
        if (normal == 2){
            points += getPPP(diff, score, combo, "2");
        }
        if (normal == 1){
            points += getPPP(diff, score, combo, "1");
        }
        while (easy >= 3){
            points += getPPP(diff, score, combo, "3");
            easy -=  3;
        }
        if (easy == 2){
            points += getPPP(diff, score, combo, "2");
        }
        if (easy == 1){
            points += getPPP(diff, score, combo, "1");
        }

        return points;
    }

    //Gets exp gained from loveca refill
    private int getRefillEXP(){
        int expGain = Integer.parseInt(expertET.getText().toString())*83
                + Integer.parseInt(hardET.getText().toString())*46
                + Integer.parseInt(normalET.getText().toString())*26
                + Integer.parseInt(easyET.getText().toString())*12;

        return expGain;
    }

    //Gets the number of plays per loveca refill
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
        int lpUsage = 0;
        int expert = Integer.parseInt(expertET.getText().toString());
        int hard = Integer.parseInt(hardET.getText().toString());
        int normal = Integer.parseInt(normalET.getText().toString());
        int easy = Integer.parseInt(easyET.getText().toString());
        while (expert >= 3){
            lpUsage += 60;
            expert -=  3;
        }
        if (expert == 2) lpUsage += 40;
        else if (expert == 1) lpUsage += 20;
        while (hard >= 3){
            lpUsage += 36;
            hard -= 3;
        }
        if (hard == 2) lpUsage += 24;
        else if (hard == 1) lpUsage += 12;
        while (normal >= 3){
            lpUsage += 24;
            normal -=  3;
        }
        if (normal == 2) lpUsage += 16;
        else if (normal == 1) lpUsage += 8;
        while (easy >= 3){
            lpUsage += 4;
            easy -=  3;
        }
        if (easy == 2) lpUsage += 8;
        else if (easy == 1) lpUsage += 4;

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

    private int getEXP(String userDiff, String songs){
        int exp;
        if (userDiff.equals("Expert")){
            if (songs.equals("3")) exp = 83*3;
            else if (songs.equals("2")) exp = 83*2;
            else exp = 83;
        }
        else if (userDiff.equals("Hard")){
            if (songs.equals("3")) exp = 46*3;
            else if (songs.equals("2")) exp = 46*2;
            else exp = 46;
        }
        else if (userDiff.equals("Normal")){
            if (songs.equals("3")) exp = 26*3;
            else if (songs.equals("2")) exp = 26*3;
            else exp = 26;
        }
        else {
            if (songs.equals("3")) exp = 12*3;
            else if (songs.equals("2")) exp = 12*2;
            else exp = 12;
        }

        return exp;
    }

    private int getLP(String userDiff, String songs){
        int lp;
        if (userDiff.equals("Expert")){
            if (songs.equals("3")) lp = 60;
            else if (songs.equals("2")) lp = 40;
            else lp = 20;
        }
        else if (userDiff.equals("Hard")){
            if (songs.equals("3")) lp = 36;
            else if (songs.equals("2")) lp = 24;
            else lp = 12;
        }
        else if (userDiff.equals("Normal")){
            if (songs.equals("3")) lp = 24;
            else if (songs.equals("2")) lp = 16;
            else lp = 8;
        }
        else {
            if (songs.equals("3")) lp = 12;
            else if (songs.equals("2")) lp = 8;
            else lp = 4;
        }

        return lp;
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
        double minutesLeft = hours*10;
        int expertPlays = Integer.parseInt(expertET.getText().toString());
        int hardPlays = Integer.parseInt(hardET.getText().toString());
        int normalPlays = Integer.parseInt(normalET.getText().toString());
        int easyPlays = Integer.parseInt(easyET.getText().toString());
        String eventDiff = difficultySpinner.getSelectedItem().toString();
        String eventScore = scoreSpinner.getSelectedItem().toString();
        String eventCombo = comboSpinner.getSelectedItem().toString();
        String eventSongs = songsSpinner.getSelectedItem().toString();
        String skill = getSkill(expertPlays, hardPlays, normalPlays, easyPlays);
        while (minutesLeft > 0){
            minutesLeft = minutesLeft - getLP(eventDiff, eventSongs);
            exp = exp + getEXP(eventDiff, eventSongs);
            points = points + getPPP(eventDiff, eventScore, eventCombo, eventSongs);
            playCount++;
        }
        //Adds points from level-ups
        if (expCB.isChecked()) exp = exp * 1.10;
        while (exp > 0){
            if (exp > expToNext) {
                rank++;
                ranksGained++;
                exp = exp - expToNext;
                expToNext = Math.round(34.45 * rank - 551);
            }
            else exp = -1; //exit loop
        }
        if (pointsCB.isChecked()) points = points * 1.10;
        System.out.println("Natural Points is: " + points);

        natural = points;
        return points;
    }

    //gets PPP (points per play) for Medley Festival
    private double getPPP(String eventDiff, String eventScore, String eventCombo, String songs){
        double points = 0;
        //base points
        if (eventDiff.equals("Expert")){
            if (songs.equals("3")) points = 777;
            else if (songs.equals("2")) points = 500;
            else if (songs.equals("1")) points = 241;
        }
        else if (eventDiff.equals("Hard")){
            if (songs.equals("3")) points = 408;
            else if (songs.equals("2")) points = 262;
            else if (songs.equals("1")) points = 126;
        }
        else if (eventDiff.equals("Normal")){
            if (songs.equals("3")) points = 234;
            else if (songs.equals("2")) points = 150;
            else if (songs.equals("1")) points = 72;
        }
        else{
            if (songs.equals("3")) points = 99;
            else if (songs.equals("2")) points = 64;
            else if (songs.equals("1")) points = 31;
        }
        //score multiplier
        if (eventScore.equals("S")) points *= 1.2;
        else if (eventScore.equals("A")) points *= 1.15;
        else if (eventScore.equals("B")) points *= 1.1;
        else if (eventScore.equals("C")) points *= 1.05;
        //combo multiplier
        if (eventCombo.equals("S")) points *= 1.08;
        else if (eventCombo.equals("A")) points *= 1.06;
        else if (eventCombo.equals("B")) points *= 1.04;
        else if (eventCombo.equals("C")) points *= 1.02;

        return points;
    }

    //Gets event points (including tokens) gained from natural LP regen
    private int getLoveca(double target, int rank, double hours, int currentPoints){
        String userDifficulty = difficultySpinner.getSelectedItem().toString();
        String score = scoreSpinner.getSelectedItem().toString();
        String combo = comboSpinner.getSelectedItem().toString();
        String eventSongs = songsSpinner.getSelectedItem().toString();
        int loveca = 0;
        double expToNext = Math.round(34.45 * rank - 551);
        target = target
                - currentPoints
                - getNaturalPoints(rank, hours, expToNext);
        rank = rank + ranksGained;
        ranksGained = 0;
        if (target <= 0) loveca = 0;
        else{
            while (target > 0) {
                loveca++;
                target = target - getRefillPoints(userDifficulty, score, combo);
                playCount = playCount + getRefillPlays();
                exp = exp + getRefillEXP();
            }
        }
        //Takes away the loveca needed from ranks gained
        while (exp > 0){
            if (exp > expToNext) {
                rank++;
                ranksGained++;
                loveca--;
                exp = exp - expToNext;
                expToNext = Math.round(34.45 * rank - 551);
            }
            else exp = -1; //exit loop as there is not enough exp for a level-up
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
