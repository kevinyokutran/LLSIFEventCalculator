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


public class TokenActivity2 extends ActionBarActivity {
    //GUI Declarations
    Button nextButton;
    EditText expertET;
    EditText hardET;
    EditText normalET;
    EditText easyET;
    Spinner difficultySpinner;
    Spinner scoreSpinner;
    Spinner comboSpinner;

    int regCount = 0;
    int eventCount = 0;
    int ranksGained = 0;
    double exp = 0;
    double natural = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_activity2);

        nextButton = (Button) findViewById(R.id.nextButton);
        expertET = (EditText) findViewById(R.id.expertET);
        hardET = (EditText) findViewById(R.id.hardET);
        normalET = (EditText) findViewById(R.id.normalET);
        easyET = (EditText) findViewById(R.id.easyET);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        scoreSpinner = (Spinner) findViewById(R.id.scoreSpinner);
        comboSpinner = (Spinner) findViewById(R.id.comboSpinner);

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
        getMenuInflater().inflate(R.menu.menu_token_activity2, menu);
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

    private String getSkill(int exp, int hard, int norm, int easy){
        String skill;
        if (exp> 0) skill = "Expert";
        else if (hard > 0) skill = "Hard";
        else if (norm > 0) skill = "Normal";
        else skill = "Easy";
        return skill;
    }

    //Gets the amount points without loveca use
    private int getNaturalPoints(int rank, double hours, double expToNext){
        int points = 0;
        int tokens = 0;
        int expGain;
        int eventPlays;
        int expertPlays = Integer.parseInt(expertET.getText().toString());
        int hardPlays = Integer.parseInt(hardET.getText().toString());
        int normalPlays = Integer.parseInt(normalET.getText().toString());
        int easyPlays = Integer.parseInt(easyET.getText().toString());
        double minutesLeft = hours*10;
        String eventDiff = difficultySpinner.getSelectedItem().toString();
        String eventScore = scoreSpinner.getSelectedItem().toString();
        String eventCombo = comboSpinner.getSelectedItem().toString();
        String skill = getSkill(expertPlays, hardPlays, normalPlays, easyPlays);
        System.out.println("Minutes is: " + minutesLeft);
        System.out.println("Skill is: " + skill);
        while (minutesLeft > 0){
            if (skill.equals("Expert")){
                minutesLeft = minutesLeft - 25;
                points = points + 27;
                tokens = tokens + 27;
                exp = exp + 83;
            }
            else if (skill.equals("Hard")){
                minutesLeft = minutesLeft - 15;
                points = points + 16*hardPlays;
                tokens = tokens + 16*hardPlays;
                exp = exp + 46;
            }
            else if (skill.equals("Normal")){
                minutesLeft = minutesLeft - 10;
                points = points + 10*normalPlays;
                tokens = tokens + 10*normalPlays;
                exp = exp + 26;
            }
            else{
                minutesLeft = minutesLeft - 5;
                points = points + 5*normalPlays;
                tokens = tokens + 5*normalPlays;
                exp = exp + 12;
            }
            regCount++;
        }
        if (eventDiff.equals("Expert")) expGain = 83;
        else if (eventDiff.equals("Hard")) expGain = 46;
        else if (eventDiff.equals("Normal")) expGain = 26;
        else expGain = 12;
        eventPlays = tokens/getTokenUsage(eventDiff);
        exp = exp + eventPlays*expGain;
        eventCount = eventCount + eventPlays;
        points = points + eventPlays*getPPP(eventDiff, eventScore, eventCombo);
        System.out.println("Natural Points is: " + points);

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

        natural = points;
        return points;
    }

    //Gets event points (including tokens) gained from natural LP regen
    private int getLoveca(int target, int rank, double hours, int currentPoints, int currentTokens){
        String userDifficulty = difficultySpinner.getSelectedItem().toString();
        String score = scoreSpinner.getSelectedItem().toString();
        String combo = comboSpinner.getSelectedItem().toString();
        int loveca = 0;
        int ranksGained = 0;
        double expToNext = Math.round(34.45 * rank - 551);
        int gainedTokens = 0;
        target = target - currentPoints
                - getNaturalPoints(rank, hours, expToNext)
                - currentTokens/getTokenUsage(userDifficulty)*getPPP(userDifficulty, score, combo);
        rank = rank + ranksGained;
        ranksGained = 0;
        if (target <= 0) loveca = 0;
        else{
            while (target > 0) {
                loveca++;
                regCount = regCount + getRefillPlays();
                exp = exp + getRefillEXP();
                target = target - getTokensPerRefill();
                gainedTokens = gainedTokens + getTokensPerRefill();
                while (gainedTokens >= getTokenUsage(userDifficulty)){
                    eventCount++;
                    exp = exp + getEXP(userDifficulty);
                    target = target - getPPP(userDifficulty, score, combo);
                    gainedTokens = gainedTokens - getTokenUsage(userDifficulty);
                }
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

    // returns number of plays on loveca refill
    private int getRefillPlays(){
        int refill = Integer.parseInt(expertET.getText().toString())
                   + Integer.parseInt(hardET.getText().toString())
                   + Integer.parseInt(normalET.getText().toString())
                   + Integer.parseInt(easyET.getText().toString());

        return refill;
    }

    // return exp from playing one song
    private int getEXP(String userDiff){
        int expGain;
        if (userDiff.equals("Expert")) expGain = 83;
        else if (userDiff.equals("Hard")) expGain = 46;
        else if (userDiff.equals("Normal")) expGain = 26;
        else expGain = 12;

        return expGain;
    }

    // returns exp gained on loveca
    private int getRefillEXP(){
        int expGain = Integer.parseInt(expertET.getText().toString())*83
                    + Integer.parseInt(hardET.getText().toString())*46
                    + Integer.parseInt(normalET.getText().toString())*26
                    + Integer.parseInt(easyET.getText().toString())*12;

        return expGain;
    }

    // returns the number of token used based on the player's difficulty
    private int getTokenUsage(String userDifficulty){
        int tokenUsage = 0;
        if (userDifficulty.equals("Easy")) tokenUsage = 15;
        else if (userDifficulty.equals("Normal")) tokenUsage = 30;
        else if (userDifficulty.equals("Hard")) tokenUsage = 45;
        else if (userDifficulty.equals("Expert")) tokenUsage = 75;
        else if (userDifficulty.equals("Easy4x")) tokenUsage = 60;
        else if (userDifficulty.equals("Normal4x")) tokenUsage = 120;
        else if (userDifficulty.equals("Hard4x")) tokenUsage = 180;

        return tokenUsage;
    }

    // returns the number of tokens gained per loveca refill
    private int getTokensPerRefill(){
        int tokens = Integer.parseInt(expertET.getText().toString())*27
                    + Integer.parseInt(hardET.getText().toString())*16
                    + Integer.parseInt(normalET.getText().toString())*10
                    + Integer.parseInt(easyET.getText().toString())*5;

        return tokens;
    }

    // gets event ppp (ppp= points per play)
    private int getPPP(String userDifficulty, String score, String combo){
        int points = 0;
        //Expert, Score = S
        if (userDifficulty.equals("Expert") && score.equals("S") && combo.equals("S")) points = 404;
        else if (userDifficulty.equals("Expert") && score.equals("S") && combo.equals("A")) points = 393;
        else if (userDifficulty.equals("Expert") && score.equals("S") && combo.equals("B")) points = 371;
        else if (userDifficulty.equals("Expert") && score.equals("S") && combo.equals("C")) points = 363;
        else if (userDifficulty.equals("Expert") && score.equals("S") && combo.equals("-")) points = 356;
            //Expert, Score = A
        else if (userDifficulty.equals("Expert") && score.equals("A") && combo.equals("S")) points = 386;
        else if (userDifficulty.equals("Expert") && score.equals("A") && combo.equals("A")) points = 375;
        else if (userDifficulty.equals("Expert") && score.equals("A") && combo.equals("B")) points = 354;
        else if (userDifficulty.equals("Expert") && score.equals("A") && combo.equals("C")) points = 347;
        else if (userDifficulty.equals("Expert") && score.equals("A") && combo.equals("-")) points = 340;
            //Expert, Score = B
        else if (userDifficulty.equals("Expert") && score.equals("B") && combo.equals("S")) points = 364;
        else if (userDifficulty.equals("Expert") && score.equals("B") && combo.equals("A")) points = 354;
        else if (userDifficulty.equals("Expert") && score.equals("B") && combo.equals("B")) points = 334;
        else if (userDifficulty.equals("Expert") && score.equals("B") && combo.equals("C")) points = 327;
        else if (userDifficulty.equals("Expert") && score.equals("B") && combo.equals("-")) points = 321;
            //Expert, Score = C
        else if (userDifficulty.equals("Expert") && score.equals("C") && combo.equals("S")) points = 346;
        else if (userDifficulty.equals("Expert") && score.equals("C") && combo.equals("A")) points = 336;
        else if (userDifficulty.equals("Expert") && score.equals("C") && combo.equals("B")) points = 317;
        else if (userDifficulty.equals("Expert") && score.equals("C") && combo.equals("C")) points = 311;
        else if (userDifficulty.equals("Expert") && score.equals("C") && combo.equals("-")) points = 305;
            //Expert, Score = No Rank
        else if (userDifficulty.equals("Expert") && score.equals("-") && combo.equals("S")) points = 328;
        else if (userDifficulty.equals("Expert") && score.equals("-") && combo.equals("A")) points = 319;
        else if (userDifficulty.equals("Expert") && score.equals("-") && combo.equals("B")) points = 301;
        else if (userDifficulty.equals("Expert") && score.equals("-") && combo.equals("C")) points = 295;
        else if (userDifficulty.equals("Expert") && score.equals("-") && combo.equals("-")) points = 289;
            //Hard, Score = S
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("S")) points = 233;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("A")) points = 227;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("B")) points = 220;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("C")) points = 216;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("-")) points = 211;
            //Hard, Score = A
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("S")) points = 223;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("A")) points = 216;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("B")) points = 210;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("C")) points = 206;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("-")) points = 202;
            //Hard, Score = B
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("S")) points = 212;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("A")) points = 206;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("B")) points = 200;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("C")) points = 196;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("-")) points = 192;
            //Hard, Score = C
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("S")) points = 201;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("A")) points = 196;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("B")) points = 190;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("C")) points = 186;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("-")) points = 182;
            //Hard, Score = No Rank
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("S")) points = 191;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("A")) points = 185;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("B")) points = 180;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("C")) points = 176;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("-")) points = 173;
            //Hard4x, Score = S
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("S")) points = 233*4;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("A")) points = 227*4;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("B")) points = 220*4;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("C")) points = 216*4;
        else if (userDifficulty.equals("Hard") && score.equals("S") && combo.equals("-")) points = 211*4;
            //Hard4x, Score = A
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("S")) points = 223*4;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("A")) points = 216*4;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("B")) points = 210*4;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("C")) points = 206*4;
        else if (userDifficulty.equals("Hard") && score.equals("A") && combo.equals("-")) points = 202*4;
            //Hard4x, Score = B
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("S")) points = 212*4;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("A")) points = 206*4;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("B")) points = 200*4;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("C")) points = 196*4;
        else if (userDifficulty.equals("Hard") && score.equals("B") && combo.equals("-")) points = 192*4;
            //Hard4x, Score = C
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("S")) points = 201*4;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("A")) points = 196*4;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("B")) points = 190*4;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("C")) points = 186*4;
        else if (userDifficulty.equals("Hard") && score.equals("C") && combo.equals("-")) points = 182*4;
            //Hard4x, Score = No Rank
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("S")) points = 191*4;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("A")) points = 185*4;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("B")) points = 180*4;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("C")) points = 176*4;
        else if (userDifficulty.equals("Hard") && score.equals("-") && combo.equals("-")) points = 173*4;
            //Normal, Score = S
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("S")) points = 140;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("A")) points = 138;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("B")) points = 135;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("C")) points = 132;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("-")) points = 130;
            //Normal, Score = A
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("S")) points = 135;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("A")) points = 133;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("B")) points = 130;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("C")) points = 127;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("-")) points = 125;
            //Normal, Score = B
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("S")) points = 130;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("A")) points = 128;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("B")) points = 125;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("C")) points = 123;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("-")) points = 120;
            //Normal, Score = C
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("S")) points = 124;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("A")) points = 121;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("B")) points = 119;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("C")) points = 116;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("-")) points = 114;
            //Normal, Score = No Rank
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("S")) points = 117;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("A")) points = 115;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("B")) points = 113;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("C")) points = 110;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("-")) points = 108;
            //Normal4x, Score = S
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("S")) points = 140*4;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("A")) points = 138*4;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("B")) points = 135*4;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("C")) points = 132*4;
        else if (userDifficulty.equals("Normal") && score.equals("S") && combo.equals("-")) points = 130*4;
            //Normal4x, Score = A
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("S")) points = 135*4;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("A")) points = 133*4;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("B")) points = 130*4;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("C")) points = 127*4;
        else if (userDifficulty.equals("Normal") && score.equals("A") && combo.equals("-")) points = 125*4;
            //Normal4x, Score = B
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("S")) points = 130*4;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("A")) points = 128*4;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("B")) points = 125*4;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("C")) points = 123*4;
        else if (userDifficulty.equals("Normal") && score.equals("B") && combo.equals("-")) points = 120*4;
            //Normal4x, Score = C
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("S")) points = 124*4;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("A")) points = 121*4;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("B")) points = 119*4;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("C")) points = 116*4;
        else if (userDifficulty.equals("Normal") && score.equals("C") && combo.equals("-")) points = 114*4;
            //Normal4x, Score = No Rank
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("S")) points = 117*4;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("A")) points = 115*4;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("B")) points = 113*4;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("C")) points = 110*4;
        else if (userDifficulty.equals("Normal") && score.equals("-") && combo.equals("-")) points = 108*4;
            //Easy, Score = S
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("S")) points = 65;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("A")) points = 64;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("B")) points = 62;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("C")) points = 61;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("-")) points = 60;
            //Easy, Score = A
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("S")) points = 64;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("A")) points = 62;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("B")) points = 61;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("C")) points = 60;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("-")) points = 59;
            //Easy, Score = B
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("S")) points = 62;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("A")) points = 61;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("B")) points = 60;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("C")) points = 59;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("-")) points = 58;
            //Easy, Score = C
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("S")) points = 59;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("A")) points = 58;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("B")) points = 57;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("C")) points = 56;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("-")) points = 55;
            //Easy, Score = No Rank
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("S")) points = 56;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("A")) points = 55;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("B")) points = 54;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("C")) points = 53;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("-")) points = 52;
            //Easy4x, Score = S
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("S")) points = 65*4;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("A")) points = 64*4;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("B")) points = 62*4;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("C")) points = 61*4;
        else if (userDifficulty.equals("Easy") && score.equals("S") && combo.equals("-")) points = 60*4;
            //Easy4x, Score = A
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("S")) points = 64*4;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("A")) points = 62*4;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("B")) points = 61*4;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("C")) points = 60*4;
        else if (userDifficulty.equals("Easy") && score.equals("A") && combo.equals("-")) points = 59*4;
            //Easy4x, Score = B
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("S")) points = 62*4;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("A")) points = 61*4;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("B")) points = 60*4;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("C")) points = 59*4;
        else if (userDifficulty.equals("Easy") && score.equals("B") && combo.equals("-")) points = 58*4;
            //Easy4x, Score = C
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("S")) points = 59*4;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("A")) points = 58*4;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("B")) points = 57*4;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("C")) points = 56*4;
        else if (userDifficulty.equals("Easy") && score.equals("C") && combo.equals("-")) points = 55*4;
            //Easy4x, Score = No Rank
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("S")) points = 56*4;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("A")) points = 55*4;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("B")) points = 54*4;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("C")) points = 53*4;
        else if (userDifficulty.equals("Easy") && score.equals("-") && combo.equals("-")) points = 52*4;

        return points;
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

    //set empty EditTexts to 0
    private void setZero(View view) {
        if (expertET.getText().toString().isEmpty()) expertET.setText("0");
        if (hardET.getText().toString().isEmpty()) hardET.setText("0");
        if (normalET.getText().toString().isEmpty()) normalET.setText("0");
        if (easyET.getText().toString().isEmpty()) easyET.setText("0");
    }

    //Checks for errors in user input, and returns error message if any is found. If none is found
    //the method opens a new activity with the amount of loveca required
    public void openResult(View view) {
        setZero(view);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int target = extras.getInt("target");
            int rank = extras.getInt("rank")+ranksGained;
            double hours = extras.getDouble("hours");
            int currentPoints = extras.getInt("currentPoints");
            int currentTokens = extras.getInt("currentTokens");
            //int songPlays = regCount + eventCount;
            if (goodLPusage(rank) && actuallyPlays()){
                int loveca = getLoveca(target, rank, hours, currentPoints, currentTokens);
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("loveca", loveca);
                intent.putExtra("natural", (int)natural);
                intent.putExtra("songPlays", regCount+eventCount);
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
