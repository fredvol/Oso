package fr.kriket.oso.view.activity;

import android.content.Intent;
import android.os.Environment;
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

    Button  btn_record_Acc_data;
    Button btn_export_DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);


        findViewsById();

        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Developer/Debug");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Start Acc record
        btn_record_Acc_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent Start_Rec_acc_data_Activite = new Intent(DebugActivity.this, RecAccdata.class);
                // Puis on lance l'intent !
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

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

}
