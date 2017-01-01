package fr.kriket.oso.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fr.kriket.oso.R;

public class DebugActivity extends AppCompatActivity {

    Button  btn_record_Acc_data;

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

    }



    /// Initialisation
    private void findViewsById() {

        //Create Button
        btn_record_Acc_data = (Button) findViewById(R.id.btn_rec_acc_data);
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
