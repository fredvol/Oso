package fr.kriket.oso.tools;

/**
 * Created by fred on 12/4/16.
 * fr.kriket.oso.tools
 */



 import android.content.Context;
 import android.util.Log;

 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.ArrayList;

 import fr.kriket.oso.model.SensorPosition;


public class GenerateCsv {


    public static void generateCsvFile(Context context, ArrayList<SensorPosition> TableSenPos, String filename) {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, filename);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String output = "RowID, Date, TimeStamp, AccX, AccY, Accz, Comment";

        Log.i("GeneratedCSV > path", path.toString());
        Log.i("GeneratedCSV > file", file.toString());


        for (SensorPosition senPos : TableSenPos) {

            output += senPos.getRowId() + ", "
                    + senPos.getDatePrise() + ", "
                    + senPos.getTimeStamp() + ", "
                    + senPos.getAccX() + ", "
                    + senPos.getAccY() + ", "
                    + senPos.getAccZ() + ", "
                    + senPos.getComment() + "\n";

        }


        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {

                    stream.write(output.getBytes());


                } catch (Exception e) {
                    e.printStackTrace();
                }


    }
}

