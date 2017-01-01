package fr.kriket.oso.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import fr.kriket.oso.BuildConfig;
import fr.kriket.oso.R;
import fr.kriket.oso.TrackService;

public class MainActivity extends AppCompatActivity {


    TextView txtview_about;
    TextView toggle_track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewsById();
        About();
        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("OSO CORE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Start Mouv Visu


        toggle_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTrackclick(v);
            }
        });
        }

    public void toggleTrackclick(View v){
        if(toggle_track.getText().equals("ON")) {
            Toast.makeText(MainActivity.this, "Start Tracking", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, TrackService.class);
            startService(i);
        }
        else {
            Toast.makeText(MainActivity.this, "End Tracking", Toast.LENGTH_SHORT).show();
        }
    }


    private void About() {
        // Update about text
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            String strabout2="Version app: "+ version + "   Version code: "+ verCode +'\n' +"Build Variant : "+ BuildConfig.BUILD_TYPE;

            txtview_about.setText(strabout2);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    /// Initialisation
    private void findViewsById() {

        //Create Textview
        txtview_about = (TextView) findViewById(R.id.txtView_about);

        //Create Toggle
        toggle_track = (ToggleButton) findViewById(R.id.toggleBtn_track);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case R.id.main_1 :
                Toast.makeText(this, "mainMenu_1", Toast.LENGTH_LONG).show();
                return true;
            case R.id.main_2 :
                Toast.makeText(this, "mainMenu_2 setting", Toast.LENGTH_LONG).show();
                return true;
            case R.id.main_3 :
                Intent Start_Debug_Activite = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(Start_Debug_Activite);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
