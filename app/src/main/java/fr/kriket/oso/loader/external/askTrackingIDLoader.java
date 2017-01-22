package fr.kriket.oso.loader.external;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import fr.kriket.oso.controler.external.askTrackingIDControler;

/**
 * Created by fred on 1/22/17.
 */

public class askTrackingIDLoader  extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = "askTrackingIDLoader";

        private Context mContext;

        private askTrackingIDControler mController;

        public askTrackingIDLoader(Context context) {
            mContext = context;
            mController = askTrackingIDControler.getInstance();
            Log.d(TAG,"init");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO: 1/22/17 Send user name
            Log.d(TAG,"do in back ground");
            return mController.asktrackinID(mContext);

        }
    }

