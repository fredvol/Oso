package fr.kriket.oso.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.kriket.oso.BuildConfig;
import fr.kriket.oso.R;

public class MainActivity extends AppCompatActivity {


    Button  btn_record_Acc_data;
    TextView txtview_about;
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
        btn_record_Acc_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent Start_Rec_acc_data_Activite = new Intent(MainActivity.this, RecAccdata.class);
                // Puis on lance l'intent !
                startActivity(Start_Rec_acc_data_Activite);
            }
        });
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

        //Create Buttun
        btn_record_Acc_data = (Button) findViewById(R.id.btn_rec_acc_data);

        //Create Textview
        txtview_about = (TextView) findViewById(R.id.txtView_about);

    }
}
