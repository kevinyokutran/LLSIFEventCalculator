package com.example.kevinytran.llsifeventcalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;


public class MedleyActivity extends ActionBarActivity {

    //GUI Declarations
    Button nextButton;
    Button autoButton;
    EditText targetET;
    EditText currentPointsET;
    EditText rankET;
    EditText hoursET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medley);

        //Assigning GUIs
        nextButton = (Button) findViewById(R.id.nextButton);
        autoButton = (Button) findViewById(R.id.hourButton);
        targetET = (EditText) findViewById(R.id.targetET);
        currentPointsET = (EditText) findViewById(R.id.currentPointsET);
        rankET = (EditText) findViewById(R.id.rankET);
        hoursET = (EditText) findViewById(R.id.hoursET);

        //Auto-fill listener
        autoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new getPrediction().execute();
            }
        });

        //nextButton listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNext(view);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medley, menu);
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

    // Get hours left based on http://www.usagi.org/doi-bin/llcutoff.pl
    public class getPrediction extends AsyncTask<Void, Void, Void> {
        ProgressDialog mProgressDialog = new ProgressDialog(MedleyActivity.this);
        String prediction = "UmiLove";
        String hoursLeftStr = "UmiLove";

        protected void onPreExecute() {
            mProgressDialog.setMessage("Accessing usagi.org/doi-bin/llcutoff.pl...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        // @Override
        protected Void doInBackground(Void... voids) {
            final String starting_hour = "16:00"; //JP starting time
            final String ending_hour = "15:00"; //dJP ending time

            Document doc;
            String hours = "UmiLove";
            String maxHours = "UmiLove";
            String startDate = "";
            String endDate = "";
            String hour = "";
            int hoursINT;
            int start_day;
            int end_day;
            String url = "http://www.usagi.org/doi-bin/llcutoff.pl";
            try {
                doc = Jsoup.connect(url).get();
                Element elementsByTag = doc.getElementsByTag("table").get(1);
                Elements rows = elementsByTag.getElementsByTag("tr");
                for (Element row : rows) {
                    //If the score column is empty, it implies that the hours column is most up to date
                    prediction = row.getElementsByTag("td").get(4).text();
                    maxHours = row.getElementsByTag("td").get(1).text();
                }
                elementsByTag = doc.getElementsByTag("table").get(2);
                rows = elementsByTag.getElementsByTag("tr");
                for (Element row : rows) {
                    startDate = row.getElementsByTag("td").get(3).text();
                    endDate = row.getElementsByTag("td").get(4).text();
                }
                DateTimeZone japanTime = DateTimeZone.forID("Asia/Tokyo");
                DateTime nowJapan = DateTime.now(japanTime);
                DateTimeFormatter fmt24 = DateTimeFormat.forPattern("YYYY-MM-dd-HH:mm");
                String jst = fmt24.print(nowJapan);
                String[] splitCurrent = jst.split("-");
                String[] splitStart = startDate.split("-");
                String[] splitEnd = endDate.split("-");
                String[] splitCurrentHour = splitCurrent[3].split(":");
                String[] splitStartH = starting_hour.split(":");
                String[] splitEndH = ending_hour.split(":");
                double hoursLeft = 0;
                int currentDay = Integer.parseInt(splitCurrent[2]);
                int currentHour = Integer.parseInt(splitCurrentHour[0]);
                int currentMinutes = Integer.parseInt(splitCurrentHour[1]);
                int startMonth = Integer.parseInt(splitStart[1]);
                int startDay = Integer.parseInt(splitStart[2]);
                int endDay = Integer.parseInt(splitEnd[2]);
                int endHour = Integer.parseInt(splitEndH[0]);
                int monthLength = nowJapan.dayOfMonth().getMaximumValue();
                int daysLeft = 0;
                int eventLength = 0;

                startDate = startDate + "-" + starting_hour; //add default starting time
                endDate = endDate + "-" + ending_hour;     	//add default ending time

                //event ends this month
                if (startDay < endDay){
                    eventLength = endDay - startDay;
                    daysLeft = endDay - currentDay;
                }
                //event ends next month
                else{
                    eventLength = monthLength - startDay + endDay;
                    if (nowJapan.getMonthOfYear() == startMonth)
                        daysLeft = monthLength - currentDay + endDay;
                    else
                        daysLeft = endDay - currentDay;
                }
                hoursLeft = 24*(daysLeft);
                if (currentHour < endHour) hoursLeft = hoursLeft+endHour-currentHour-1;
                else hoursLeft = hoursLeft - currentHour + endHour;
                hoursLeft = hoursLeft + (1-currentMinutes/60.0);
                DecimalFormat df = new DecimalFormat("0.##");
                hoursLeft = Double.valueOf(df.format(hoursLeft));
                if (hoursLeft <= 0 || hoursLeft > Integer.parseInt(maxHours)){
                    hoursLeft = 0;
                }
                hoursLeftStr = String.valueOf(hoursLeft);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //@Override
        protected void onPostExecute(Void voids) {
            targetET.setText(prediction);
            hoursET.setText(hoursLeftStr);
            if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        }
    }

    //Checks whether there are no EditTexts empty
    private boolean noEmptyFields(){
        boolean empty = true;
        if (targetET.getText().toString().isEmpty() ||
                currentPointsET.getText().toString().isEmpty() ||
                rankET.getText().toString().isEmpty() ||
                hoursET.getText().toString().isEmpty())
            empty = false;

        return empty;
    }

    //Checks whether the player's rank is not zero
    private boolean goodRank(){
        boolean good = true;
        if (rankET.getText().toString().equals("0")) good = false;

        return good;
    }

    //opens the next activity (ScoreActivity2)
    private void openNext(View view) {
        if (noEmptyFields() && goodRank()) {
            int currentPoints = Integer.parseInt(currentPointsET.getText().toString());
            double hours = Double.parseDouble(hoursET.getText().toString());
            int rank = Integer.parseInt(rankET.getText().toString());
            int target = Integer.parseInt(targetET.getText().toString());

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Intent intent;
                if (extras.getString("selection").equals("Score"))
                    intent = new Intent(this, ScoreActivity2.class);
                else
                    intent = new Intent(this, MedleyActivity2.class);
                intent.putExtra("target", target);
                intent.putExtra("currentPoints", currentPoints);
                intent.putExtra("hours", hours);
                intent.putExtra("rank", rank);
                this.startActivity(intent);
            }

        }
        else {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            String blank = "Please enter in blank values";
            String rank = "Rank has to be at least 1";
            if (!noEmptyFields()) dlgAlert.setMessage(blank);
            else if (!goodRank()) dlgAlert.setMessage(rank);
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
