package com.example.infocovid;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION=1;
    private final int PERMISSION_REQUEST_CODE = 200;
    public BackgroundLocationService locationService;
    TextView casesText;
    TextView deathsText;
    TextView dateText;
    TextView cardTitle;

    private TextView textView;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //textView.setText(""+intent.getExtras().get("address"));
                    showStatistics();
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask Location Permissions
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("HELLOLOCATION: Tenemos permisos...");
        } else {
            // no tiene permiso, solicitarlo
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
            // cuando se nos conceda el permiso se llamará a onRequestPermissionsResult()
        }


        // Set Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setTitle("Home Menu");
        setSupportActionBar(myToolbar);

        // Set Coordinates Text & Start Location Tracking
        textView = (TextView) findViewById(R.id.coordinates_text);
        startTracking();

        // Get stats of covid19
        StatisticsClient statisticsClient = new StatisticsClient(this);
        statisticsClient.execute();

        showStatistics();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    finish();
                    startActivity(new Intent(this, this.getClass()));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void startTracking() {
        Intent intent = new Intent(getApplicationContext(), BackgroundLocationService.class);
        startService(intent);
    }

    public void showStatistics(){
        cardTitle = (TextView) findViewById(R.id.card_title);
        casesText = (TextView) findViewById(R.id.cases_text);
        deathsText = (TextView) findViewById(R.id.deaths_text);
        dateText = (TextView) findViewById(R.id.date_text);

        // Cargamos stadistics del disk
        String sharedPrefFile = "com.uc3m.it.infocovid";
        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        String cases = mPreferences.getString("today_new_confirmed", "0");
        String deaths = mPreferences.getString("today_new_deaths", "0");
        String date = mPreferences.getString("last_updated", "0");
        String comunidad = mPreferences.getString("comunidad", "null");
        String pais = mPreferences.getString("pais", "null");

        if(!pais.equals("Spain")) {
            casesText.setText("Unavailable Data");
            casesText.setTextSize(15);
            casesText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            casesText.setTextColor(Color.parseColor("#E06C6C"));
            deathsText.setText("Unavailable Data");
            deathsText.setTextSize(15);
            deathsText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            deathsText.setTextColor(Color.parseColor("#E06C6C"));
        } else {
            casesText.setText(cases);
            casesText.setTextSize(40);
            casesText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            casesText.setTextColor(Color.parseColor("#0003e0"));
            deathsText.setText(deaths);
            deathsText.setTextSize(40);
            deathsText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            deathsText.setTextColor(Color.parseColor("#0003e0"));
        }
        dateText.setText("Actualizado el " + date);
        cardTitle.setText("Hoy en " + comunidad);
    }

    // ---------------------------------------------------------------------------------------------------------------------------------

    public void loadNews(View view){
        String msg = "Clickado en news";
        Log.d("MainActivity",msg);
        Intent intent = new Intent(this, DisplayNewsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void loadRestrictions(View view){
        String msg = "Clickado en restrictions";

        // Creamos coleccion de preferencias
        String sharedPrefFile = "com.uc3m.it.infocovid";
        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String zipCode = mPreferences.getString("zipCode", "0");
        Log.d("MainActivity",msg + " ZIPCODE: " + zipCode);

        Intent intent = new Intent(this, DisplayRestrictionsActivity.class);
        intent.putExtra("zipCode", zipCode);
        intent.putExtra("activity", "MainActivity");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.hold);
    }
    public void loadSearch(View view){
        String msg = "Clickado en search";
        Log.d("MainActivity",msg);
        Intent intent = new Intent(this, DisplaySearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}