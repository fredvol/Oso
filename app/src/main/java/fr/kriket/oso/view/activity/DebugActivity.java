package fr.kriket.oso.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import fr.kriket.oso.R;
import fr.kriket.oso.tools.ExportImportDB;

public class DebugActivity extends AppCompatActivity {

    private static final String TAG = "DebugActivity";

    private static final int REQUEST_WRITE_STORAGE = 112;

    Button  btn_record_Acc_data;
    Button btn_export_DB;
    Button btn_test;


    Context context;

    String value=null;


    private SharedPreferences sharedPref ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        context = getApplicationContext();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Check Permission
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        findViewsById();

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Developer/Debug");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Start Acc record
        btn_record_Acc_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Start_Rec_acc_data_Activite = new Intent(DebugActivity.this, RecAccdata.class);
                startActivity(Start_Rec_acc_data_Activite);
            }
        });

        //Export DB
        btn_export_DB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportDB();

            }
        });




        //Test button
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Log.d("TEST","TEST !");

            value = sharedPref.getString("username", "default_value");
            Log.d(TAG,"shared2 value:"+value );
            Log.d(TAG,"shared2 value:"+sharedPref.getInt("log_interval",3));
            Log.d(TAG,"shared2 value:"+sharedPref.getString("newID",null));
            }
        });

    }

    private void ExportDB() {

        Log.d(TAG,"ExportDB");

        exportDB();
    }

    /// Initialisation
    private void findViewsById() {

        //Create Button
        btn_record_Acc_data = (Button) findViewById(R.id.btn_rec_acc_data);
        btn_export_DB = (Button) findViewById(R.id.bttn_export_DB);
        btn_test=(Button) findViewById(R.id.button_test);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //exporting database
    private void exportDB() {

           String NOM = "TrackPointTable";
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();



            File path = this.getExternalFilesDir(null);



            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "fr.kriket.oso"
                        + "//databases//" + NOM;

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(path, NOM);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getBaseContext(), "Can't write on Sd card", Toast.LENGTH_LONG)
                        .show();

            }

        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

}
