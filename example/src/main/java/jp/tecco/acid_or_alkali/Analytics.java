package jp.tecco.acid_or_alkali;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by makotonishimoto on 2015/06/03.
 */
public class Analytics extends Application {
    Tracker mTracker;
    Analytics(){
        super();
    }
    synchronized Tracker getTracker() {
        if(mTracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

}
