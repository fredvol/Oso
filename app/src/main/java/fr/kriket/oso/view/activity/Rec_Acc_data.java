package fr.kriket.oso.view.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.kriket.oso.R;
import fr.kriket.oso.tools.GenerateCsv;
import fr.kriket.oso.model.SensorPosition;

import static fr.kriket.oso.tools.GenerateCsv.generateCsvFile;

public class Rec_Acc_data extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    TextView txtv_accX;
    TextView txtv_accY;
    TextView txtv_accZ;
    CheckBox chkbx_record;
    Date date = new Date();

    ArrayList<SensorPosition> TableSenPos = new ArrayList<SensorPosition>();

    //private final float[] AcclVector = new float[4]();



    Button  btn_start_record_Acc_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec__acc_data);

        findViewsById();

        //Format title bar
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Rec Acc data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAcc , SensorManager.SENSOR_DELAY_NORMAL);

        //Start Mouv Visu
        btn_start_record_Acc_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordAcc2File();
            }
        });


    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        // If sensor is unreliable, then just return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        // Update Label

        txtv_accX.setText(Float.toString(event.values[0]));
        txtv_accY.setText(Float.toString(event.values[1]));
        txtv_accZ.setText(Float.toString(event.values[2]));


        Log.i("msensor.tostring",
                "x = " + Float.toString(event.values[0]) + "\t" +
                "y = " + Float.toString(event.values[1]) + "\t" +
                "z = " + Float.toString(event.values[2]) + "\n"
        );

        if (chkbx_record.isChecked()) {
            long curTime = System.currentTimeMillis();
            TableSenPos.add(new SensorPosition(event.timestamp,event.values[0],event.values[1],event.values[2]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    public boolean RecordAcc2File(){


        if (!TableSenPos.isEmpty()){
            Toast.makeText(this, "Export data ...", Toast.LENGTH_LONG).show();

            Log.i("msensor.tostring",(TableSenPos.toString()));


            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            date = new Date();

            String fileName= "RecACC_"+dateFormat.format(date)+".txt";
            generateCsvFile(this,TableSenPos,fileName);

            TableSenPos=new ArrayList<SensorPosition>();
            return true;
        } else {
            Toast.makeText(this, "No data ...", Toast.LENGTH_LONG).show();
            return false;
        }


    }





    /// Initialisation
    private void findViewsById() {

        //Create Buttun
        btn_start_record_Acc_data = (Button) findViewById(R.id.btn_start_rec_acc);

        txtv_accX= (TextView) findViewById(R.id.txtV_accX);
        txtv_accY=(TextView) findViewById(R.id.txtV_accY);
        txtv_accZ=(TextView) findViewById(R.id.txtV_accZ);

        chkbx_record= (CheckBox) findViewById(R.id.chckBx_record);
    }



    // back button
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }


}
