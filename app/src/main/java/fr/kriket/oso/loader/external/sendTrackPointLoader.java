package fr.kriket.oso.loader.external;

import android.content.Context;
import android.os.AsyncTask;
import java.util.List;

import fr.kriket.oso.controler.external.sendTrackPointControler;
import fr.kriket.oso.model.TrackPoint;

/**
 * Created by fred on 1/22/17.
 */

public class sendTrackPointLoader  extends AsyncTask<List<TrackPoint>, Void, List<String>> {

        public interface sendTrackPointLoaderListener {
            void onsendTrackPointSent(List<String> results);
            void onsendTrackPointFailed();
        }

        private Context mContext;
        private sendTrackPointLoaderListener mListener;
        private sendTrackPointControler mController;

        public sendTrackPointLoader(Context context, sendTrackPointLoaderListener listener) {
            mContext = context;
            mController = sendTrackPointControler.getInstance();
            mListener = listener;
        }


        @Override
        protected List<String> doInBackground(List<TrackPoint>... params) {

            if ( params==null || params.length==0 ) {
                return null;
            }

            List<TrackPoint> trackPoints = params[0];
            List<String> result;

            result = mController.SendTrackPoints(mContext,trackPoints);


            return result;
        }




        @Override
        protected void onPostExecute(List<String> results) {

            if ( results != null) {
                mListener.onsendTrackPointSent(results);
            } else {
                mListener.onsendTrackPointFailed();
            }
        }
    }
