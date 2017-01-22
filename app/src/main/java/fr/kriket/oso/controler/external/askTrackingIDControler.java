package fr.kriket.oso.controler.external;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by fred on 1/22/17.
 */

public class askTrackingIDControler {

    private static final String TAG = "askTrckIDCntrlr";
    private SharedPreferences sharedPref ;
    private SharedPreferences.Editor editor;



    /** Private contrustor */
    private askTrackingIDControler()
    {}

    /** Instance unique non préinitialisée */
    private static askTrackingIDControler INSTANCE = null;

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized askTrackingIDControler getInstance()
    {
        if (INSTANCE == null)
        { 	INSTANCE = new askTrackingIDControler();
        }
        return INSTANCE;
    }



    public Boolean asktrackinID(Context mcontext){
        OkHttpClient client = new OkHttpClient();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mcontext);
        //editor
        editor = sharedPref.edit();

        String mUrl =sharedPref.getString("serverURl_queryId","http://kriket.hd.free.fr:802/oso_web/V1/queryId.php");


        Log.i(TAG, "asktrackinID  url= "+mUrl);

        ///////////////////////////



        RequestBody formBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(mUrl)
                .build();
        Log.i("Askgoogletoken", "request: " + request);

        Call call = client.newCall(request);
        try {
            Log.i("Askgoogletoken", " try");
            Response response = call.execute();
            String StrResponse =response.body().string();
            Log.d("Askgoogletoken", "StrResponse2: " + StrResponse);
            if (response.isSuccessful()) {

                // extract Token
                Log.d(TAG, "sucess , extracting Token ...");

                Log.i(TAG ," response: " + response);

                editor.putString("trackingID", StrResponse).apply();

                return true;

            }
        } catch (IOException e) {

            Log.e("OkHttpimpl", "Execption caught: ", e);

            return false;
        }

        return true;
    }
}
