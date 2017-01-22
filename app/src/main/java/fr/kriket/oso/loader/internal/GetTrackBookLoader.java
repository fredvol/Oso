package fr.kriket.oso.loader.internal;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import fr.kriket.oso.controler.internal.GetTrackBookLoaderController;


/**
 * Created by fred on 1/7/17.
 */

public class GetTrackBookLoader extends AsyncTask<Void, Void, List> {

    private static final String TAG = "GetTrackBookLoader";

    public interface GetTrackBookLoaderListener {
        void onStartGetTrackBook();
        void onGetTrackBookSucess(List s);

        void onGetTrackBookFailed(String s);

    }

    private Context mContext;
    private GetTrackBookLoaderListener mListener;
    private GetTrackBookLoaderController mController;

    public GetTrackBookLoader(Context context, GetTrackBookLoaderListener listener) {
        mContext = context;
        mController = GetTrackBookLoaderController.getInstance();
        mListener = listener;

    }

    @Override
    protected void onPreExecute() {
        mListener.onStartGetTrackBook();
    }


    @Override
    protected List doInBackground(Void... voids) {
        Log.d(TAG,"doInBackground");
        //return mController.getallpoints(mContext);
        return mController.getPointsBySeesionId(mContext,null);
    }

    @Override
    protected void onPostExecute(List list) {
        if ( list != null) {
                mListener.onGetTrackBookSucess(list);

        } else {
            mListener.onGetTrackBookFailed("Error fetching Sqlite database!");
        }

    }
}
