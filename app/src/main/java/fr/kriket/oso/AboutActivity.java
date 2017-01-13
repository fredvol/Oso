package fr.kriket.oso;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import fr.kriket.oso.view.activity.DebugActivity;
import fr.kriket.oso.view.activity.MainActivity;
import fr.kriket.oso.view.activity.TrackBookActivity;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";


    TextView txtview_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViewsById();

        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        About();
    }



    private void About() {
        // Update about text
        PackageInfo pInfo ;
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
        txtview_about = (TextView) findViewById(R.id.txtView_About);
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
}
