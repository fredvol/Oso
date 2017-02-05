package fr.kriket.oso.loader.internal;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import fr.kriket.oso.controler.internal.GetTrackBookLoaderController;


/**
 * Created by fred on 1/7/17.
 */

public class GetTrackPointFromDBLoader extends AsyncTask<String, Void, List> {

    private static final String TAG = "GetTrackPtFrmDBLoader";

    public interface GetTrackPointFromDBLoaderListener {

        void onGetTrackPointFromDBLoaderSucess(List s);
        void onGetTrackPointFromDBLoaderFailed(String s);
    }


    private Context mContext;
    private GetTrackPointFromDBLoaderListener mListener;
    private GetTrackBookLoaderController mController;

    public GetTrackPointFromDBLoader(Context context, GetTrackPointFromDBLoaderListener listener) {
        mContext = context;
        mController = GetTrackBookLoaderController.getInstance();
        mListener = listener;

    }

    @Override
    protected List doInBackground(String... strings) {

        String sessionId = strings[0];

        Log.d(TAG,"doInBackground");
        return mController.getPointsBySeesionId(mContext,sessionId);
    }




    /**
     * @param list get the list of trackpoint from the controller
     * send the list to the listenner on send error message
     */
    @Override
    protected void onPostExecute(List list) {
        if ( list != null) {
                mListener.onGetTrackPointFromDBLoaderSucess(list);
        } else {
            mListener.onGetTrackPointFromDBLoaderFailed("Error fetching Sqlite database!");
        }

    }
}
