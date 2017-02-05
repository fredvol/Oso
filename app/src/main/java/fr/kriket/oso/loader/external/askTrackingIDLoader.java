package fr.kriket.oso.loader.external;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import fr.kriket.oso.controler.external.askTrackingIDControler;
import fr.kriket.oso.loader.internal.GetTrackPointFromDBLoader;

/**
 * Created by fred on 1/22/17.
 */

public class askTrackingIDLoader extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = "askTrackingIDLoader";

    public interface askTrackingIDLoaderListener {

        void onGetTrackingIDSucess();
        void onGetTrackingIDFailed();
    }

    private Context mContext;
    private askTrackingIDLoaderListener mListener;

    private askTrackingIDControler mController;


    public askTrackingIDLoader(Context context, askTrackingIDLoaderListener listener) {
        mContext = context;
        mController = askTrackingIDControler.getInstance();
        mListener = listener;

        Log.d(TAG, "init");
    }

    @Override
    protected Boolean doInBackground(String... params) {
        // TODO: 1/22/17 Send user name
        Log.d(TAG, "do in back ground");
        return mController.asktrackinID(mContext);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            mListener.onGetTrackingIDSucess();
        } else {
            mListener.onGetTrackingIDFailed();
        }

    }

}

