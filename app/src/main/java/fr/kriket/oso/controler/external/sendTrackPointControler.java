package fr.kriket.oso.controler.external;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.kriket.oso.model.TrackPoint;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by fred on 1/22/17.
 */

public class sendTrackPointControler {

    private static final String TAG = "sendTrackPointControler";
    private SharedPreferences sharedPref ;

    /**
     * Constructeur privé
     */
    private sendTrackPointControler() {
    }

    /**
     * Instance unique non préinitialisée
     */
    private static sendTrackPointControler INSTANCE = null;

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static synchronized sendTrackPointControler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new sendTrackPointControler();
        }
        return INSTANCE;
    }

    String mUrl;


    public List<String> SendTrackPoints(Context mcontext,List<TrackPoint> trackpoints) {
        OkHttpClient client = new OkHttpClient();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mcontext);
        mUrl =sharedPref.getString("serverURl_sendpoints","http://kriket.hd.free.fr:802/oso_web/V1/add.php");

        Log.d(TAG,"SendTrackPoints mUrl: "+mUrl);
        String jsonList=new Gson().toJson(trackpoints);  // TODO: 1/22/17 remove Gson lib  ?
        // TODO: 1/22/17 make a clear difference between sessionID and Track ID !

        Response response;

          final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


            RequestBody body = RequestBody.create(JSON, jsonList);
            Request request = new Request.Builder()
                    .url(mUrl)
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            try {
                response = call.execute();
                String StrResponse = response.body().string();
                Log.d(TAG, "StrResponse: " + StrResponse);
                if (response.isSuccessful()) {
                    String resultTimestamp = StrResponse.substring(StrResponse.indexOf("[") + 1, StrResponse.indexOf("]"));
                    List<String> ListTimestamp =  Arrays.asList(resultTimestamp.split(","));

                    return ListTimestamp;
                } else {
                    return null;
                }

            } catch (IOException e) {

                Log.e(TAG, "Execption caught: ", e);
                return null;
            }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                return null;
//            }
    }

}
